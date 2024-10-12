package com.arham.uhack.data

import android.content.Context
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.arham.uhack.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Login(private val context: Context) {

    private val auth: FirebaseAuth = Firebase.auth

    suspend fun signInWithGoogle(): Result<Any> {
        return suspendCoroutine { continuation ->
            val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
            val credentialManager = CredentialManager.create(this.context)
            val nonce = Nonce()

            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(this.context.getString(R.string.web_client_id))
                .setAutoSelectEnabled(true)
                .setNonce(nonce.generateNonce(16))
                .build()

            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            scope.launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = this@Login.context,
                    )
                    val signInResult = handleSignIn(result) // Get the result from handleSignIn
                    continuation.resume(signInResult) // Resume the continuation with the result
                } catch (e: GetCredentialException) {
                    // Handle sign-up
                    val signUpResult = handleSignUp() // Get the result from handleSignUp
                    continuation.resume(signUpResult) // Resume the continuation with the result
                }
            }
        }
    }

    private suspend fun handleSignUp(): Result<Any> {
        return suspendCoroutine { continuation ->
            val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
            val credentialManager = CredentialManager.create(context)

            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.web_client_id))
                .build()

            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            // Launch a new coroutine for sign-up
            scope.launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = context,
                    )

                    val signInResult = handleSignIn(result) // Get the result from handleSignIn
                    continuation.resume(signInResult) // Resume the continuation with the result
                } catch (e: GetCredentialException) {
                    // Handle sign-up errors
                    continuation.resume(Result.Error(IOException(context.getString(R.string.login_failed))))
                }
            }
        }
    }

    private suspend fun handleSignIn(result: GetCredentialResponse): Result<Any> {
        // Handle the successfully returned credential.
        when (val credential = result.credential) {
            // GoogleIdToken credential
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract the ID to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        firebaseAuthWithGoogle(idToken)
                        return Result.Success(auth.currentUser)
                    } catch (e: Exception) {
                        return Result.Error(IOException(context.getString(R.string.login_failed)))
                    }
                }
                // Catch any unrecognized custom credential type here.
                return Result.Error(IOException(context.getString(R.string.invalid_token)))
            }
            else -> {
                // Catch any unrecognized credential type here.
                return Result.Error(IOException(context.getString(R.string.unexpected_token)))
            }
        }
    }

    private suspend fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .await()
        val user = auth.currentUser
        if (user != null) {
            Toast.makeText(context, context.getString(R.string.login_successful) + ": " + user.displayName, Toast.LENGTH_SHORT).show()
        }
        else {
            throw IOException(context.getString(R.string.login_failed))
        }
    }
}
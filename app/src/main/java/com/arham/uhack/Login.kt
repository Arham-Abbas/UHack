package com.arham.uhack

import android.content.Context
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
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

class Login(private val context: Context) {

    fun signInWithGoogle() {
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

                handleSignIn(result)
            } catch (e: GetCredentialException) {
                // Handle sign-up
                handleSignUp()
            }
        }
    }

    private fun handleSignUp() {
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

                handleSignIn(result) // Handle sign-in after successful sign-up
            } catch (e: GetCredentialException) {
                // Handle sign-up errors
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
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
                    } catch (e: Exception) {
                        Toast.makeText(context,
                            context.getString(R.string.invalid_token) + ":" + e.toString(), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Toast.makeText(context, context.getString(R.string.unexpected_token), Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                // Catch any unrecognized credential type here.
                Toast.makeText(context, context.getString(R.string.unexpected_token), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val auth: FirebaseAuth = Firebase.auth
        auth.signInWithCredential(credential)
            .addOnCompleteListener(MainActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    if (user != null) {
                        Toast.makeText(context, context.getString(R.string.login_successful) + ":" + user.displayName, Toast.LENGTH_SHORT).show()
                    }
                    // Update UI
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(context, context.getString(R.string.login_failed) + ":" + task.exception, Toast.LENGTH_SHORT).show()
                }
            }
    }
}
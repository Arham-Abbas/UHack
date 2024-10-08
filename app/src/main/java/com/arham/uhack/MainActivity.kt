package com.arham.uhack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.arham.uhack.ui.navigation.NavigationActivity
import com.arham.uhack.ui.theme.UHackTheme
import com.arham.uhack.ui.login.LoginActivity
import com.arham.uhack.ui.login.LoginViewModel
import com.arham.uhack.ui.login.LoginViewModelFactory
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private val timeoutDelay = 3000L
    private lateinit var loginViewModel: LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()
        setContent {
            UHackTheme {
                SplashScreen()
            }
        }
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            //Handler(Looper.getMainLooper()).postDelayed({
              //  val intent = Intent(this, LoginActivity::class.java)
               // startActivity(intent)
           // }, timeoutDelay)

            val credentialManager = CredentialManager.create(this)

            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId("758276425326-i3pe52r682knkhc6j8gmovtc01s7ohmg.apps.googleusercontent.com")
                .setAutoSelectEnabled(true)
                .setNonce(generateNonce(16))
                .build()

            val signInWithGoogleOption: GetSignInWithGoogleOption = GetSignInWithGoogleOption.Builder("758276425326-i3pe52r682knkhc6j8gmovtc01s7ohmg.apps.googleusercontent.com")
                .setNonce(generateNonce(16))
                .build()

            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()

            lifecycleScope.launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = this@MainActivity,
                    )
                    handleSignIn(result, this@MainActivity)
                } catch (e: GetCredentialException) {
                    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId("758276425326-i3pe52r682knkhc6j8gmovtc01s7ohmg.apps.googleusercontent.com")
                        .build()
                    val request: GetCredentialRequest = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()
                    val result = credentialManager.getCredential(
                        request = request,
                        context = this@MainActivity,
                    )

                    handleSignIn(result, this@MainActivity)
                    Toast.makeText(this@MainActivity, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            loginViewModel = LoginViewModelFactory().create(LoginViewModel::class.java)
            loginViewModel.login(user.email!!, user.uid)
        }
        val intent = Intent(this, NavigationActivity::class.java)
        startActivity(intent)
    }


}

@Composable
fun SplashScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp), // Adjust spacing as needed
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Image(
                painter = painterResource(R.drawable.united), // Replace with your image
                contentDescription = "United",
                modifier = Modifier.size(128.dp)
            )
            Image(
                painter = painterResource(R.drawable.technovators), // Replace with your image
                contentDescription = "Technovators",
                modifier = Modifier.size(128.dp)
            )
        }
        Image(
            painter = painterResource(R.drawable.uhack), // Replace with your app icon
            contentDescription = "UHack",
            modifier = Modifier.size(256.dp)
        )

        Spacer(modifier = Modifier.weight(1f)) // Pushes button downwards

        Button(onClick = { signInWithGoogle(context) }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.google), // Replace with your icon
                    contentDescription = "Google",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Login using Google")
            }
        }

        Spacer(modifier = Modifier.height(32.dp)) // Fixed height spacer
    }
}


fun generateNonce(length: Int): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}


private fun signInWithGoogle(context: Context) {

    val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())

    val credentialManager = CredentialManager.create(context)

    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId(context.getString(R.string.web_client_id))
        .setAutoSelectEnabled(true)
        .setNonce(generateNonce(16))
        .build()

    val signInWithGoogleOption: GetSignInWithGoogleOption = GetSignInWithGoogleOption.Builder(context.getString(R.string.web_client_id))
        .setNonce(generateNonce(16))
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    scope.launch {
        try {
            val result = credentialManager.getCredential(
                request = request,
                context = context,
            )
            handleSignIn(result, context)
        } catch (e: GetCredentialException) {
            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.web_client_id))
                .build()
            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            val result = credentialManager.getCredential(
                request = request,
                context = context,
            )

            handleSignIn(result, context)
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}

fun handleSignIn(result: GetCredentialResponse, context: Context) {
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
                    // You can use the members of googleIdTokenCredential directly for UX
                    // purposes, but don't use them to store or control access to user
                    // data. For that you first need to validate the token:
                    // pass googleIdTokenCredential.getIdToken() to the backend server.
                    //GoogleIdTokenVerifier verifier = ... // see validation instructions
                    val idToken = googleIdTokenCredential.idToken //verifier.verify(idTokenString);
                    // To get a stable account identifier (e.g. for storing user data),
                    // use the subject ID:
                    firebaseAuthWithGoogle(idToken)
                } catch (e: Exception) {
                    Toast.makeText(context,
                        "Received an invalid google id token response$e", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Catch any unrecognized custom credential type here.
                Toast.makeText(context, "Unexpected type of credential", Toast.LENGTH_SHORT).show()
            }
        }

        else -> {
            // Catch any unrecognized credential type here.
            Toast.makeText(MainActivity(), "Unexpected type of credential", Toast.LENGTH_SHORT).show()
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
                Log.d("TAG", "signInWithCredential:success")
                val user = auth.currentUser
                if (user != null) {
                    Log.d("TAG", user.uid + user.email + user.displayName + user.phoneNumber + user.providerId + user.tenantId + user.photoUrl)
                }
                // Update UI
            } else {
                // If sign in fails, display a message to the user.
                Log.w("TAG", "signInWithCredential:failure", task.exception)
                // Update UI
            }
        }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    UHackTheme {
        SplashScreen()
    }
}
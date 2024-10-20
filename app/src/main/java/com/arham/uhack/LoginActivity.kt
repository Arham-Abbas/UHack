package com.arham.uhack

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arham.uhack.data.Login
import com.arham.uhack.ui.theme.UHackTheme
import com.arham.uhack.data.Result
import com.arham.uhack.ui.theme.Typography

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            UHackTheme {
                LoginScreen()
            }
        }
    }
}

@Composable
fun LoginScreen() {
    val context = LocalContext.current
    val login = Login(context)
    var isSignInButtonClicked by remember { mutableStateOf(false) }
    var showProgress by remember { mutableStateOf(false) }
    var buttonText by remember { mutableStateOf(context.getString(R.string.action_sign_in)) }
    val activity = (LocalContext.current as ComponentActivity)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
                contentDescription = context.getString(R.string.description_united),
                modifier = Modifier.size(160.dp)
            )
            Image(
                painter = painterResource(R.drawable.technovators), // Replace with your image
                contentDescription = context.getString(R.string.description_technovators),
                modifier = Modifier.size(160.dp)
            )
        }
        Image(
            painter = painterResource(R.drawable.uhack), // Replace with your app icon
            contentDescription = context.getString(R.string.description_uhack),
            modifier = Modifier.size(320.dp)
        )

        Spacer(modifier = Modifier.weight(1f)) // Pushes button downwards

        Button(
            onClick = { isSignInButtonClicked = true
                showProgress = true
                buttonText = context.getString(R.string.action_signing_in) // Change button text
            },
            enabled = !showProgress, // Disable button while progress is shown
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary // Use dynamic color
            )
        ) {
            // Display progress indicator or button text
            if (showProgress) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                        color = MaterialTheme.colorScheme.onPrimary, // Use contrasting color
                        strokeWidth = 2.dp // Adjust stroke width as needed
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = context.getString(R.string.action_signing_in),
                        style = Typography.titleMedium
                    )
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.google), // Replace with your icon
                        contentDescription = context.getString(R.string.description_google),
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = context.getString(R.string.action_sign_in),
                        style = Typography.titleMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp)) // Fixed height spacer

        LaunchedEffect(key1 = isSignInButtonClicked) {
            if (isSignInButtonClicked) {
                isSignInButtonClicked = false

                when (val result = login.signInWithGoogle()) {
                    is Result.Success -> {
                        // Sign-in successful
                        val intent = Intent(context, NavigationActivity::class.java)
                        context.startActivity(intent)
                        activity.finish()
                    }
                    is Result.Error -> {
                        // Sign-in failed
                        Toast.makeText(context, result.exception.message, Toast.LENGTH_SHORT).show()
                        showProgress = false
                        buttonText = context.getString(R.string.action_sign_in) // Reset button text
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    UHackTheme {
        LoginScreen()
    }
}
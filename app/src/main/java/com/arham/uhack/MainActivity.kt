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
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.arham.uhack.data.Result

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            enableEdgeToEdge()
            setContent {
                UHackTheme {
                    MainScreen()
                }
            }

        } else {
            val intent = Intent(this, NavigationActivity::class.java)
            this.startActivity(intent)
            finish()
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val login = Login(context)
    var isSignInButtonClicked by remember { mutableStateOf(false) }
    val activity = (LocalContext.current as ComponentActivity)

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
                contentDescription = context.getString(R.string.description_united),
                modifier = Modifier.size(128.dp)
            )
            Image(
                painter = painterResource(R.drawable.technovators), // Replace with your image
                contentDescription = context.getString(R.string.description_technovators),
                modifier = Modifier.size(128.dp)
            )
        }
        Image(
            painter = painterResource(R.drawable.uhack), // Replace with your app icon
            contentDescription = context.getString(R.string.description_uhack),
            modifier = Modifier.size(256.dp)
        )

        Spacer(modifier = Modifier.weight(1f)) // Pushes button downwards

        Button(onClick = { isSignInButtonClicked = true }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.google), // Replace with your icon
                    contentDescription = context.getString(R.string.description_google),
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(context.getString(R.string.action_sign_in))
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
        MainScreen()
    }
}
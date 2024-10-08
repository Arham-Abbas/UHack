package com.arham.uhack

import android.os.Bundle
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
import com.arham.uhack.ui.theme.UHackTheme
import com.arham.uhack.ui.login.LoginViewModel
import com.arham.uhack.ui.login.LoginViewModelFactory
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private lateinit var loginViewModel: LoginViewModel

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
            loginViewModel = LoginViewModelFactory().create(LoginViewModel::class.java)
            loginViewModel.login(user.email!!, user.uid)
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val login = Login(context)

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

        Button(onClick = { login.signInWithGoogle() }) {
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
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    UHackTheme {
        MainScreen()
    }
}
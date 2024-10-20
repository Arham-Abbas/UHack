package com.arham.uhack

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.arham.uhack.data.QRCode
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.google.firebase.crashlytics.FirebaseCrashlytics
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true
        FirebaseApp.initializeApp(this)
        val user = FirebaseAuth.getInstance().currentUser
        val intent: Intent?
        if (user == null) {
            intent = Intent(this, LoginActivity::class.java)

        } else {
            lifecycleScope.launch {
                QRCode.generateQRCode(this@MainActivity, user.uid, 512, 512)
            }
            intent = Intent(this, NavigationActivity::class.java)
        }
        this.startActivity(intent)
        installSplashScreen().setOnExitAnimationListener { splashScreenView ->
            splashScreenView.remove()
        }
        finish()
    }
}
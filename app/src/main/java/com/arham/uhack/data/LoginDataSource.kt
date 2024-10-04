package com.arham.uhack.data

import com.arham.uhack.data.model.LoggedInUser
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {
    private val auth: FirebaseAuth = Firebase.auth
    private var loggedInUser: LoggedInUser? = null
    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        try {

            if (auth.currentUser == null)
            {
                auth.createUserWithEmailAndPassword(username, password)
                    .await()
            }
            val user = auth.currentUser
            if (user != null) {
                loggedInUser = LoggedInUser(user.uid, user.email!!)
                return Result.Success(loggedInUser!!)
            }
            return Result.Error(IOException("Error logging in"))
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        auth.signOut()
        loggedInUser = null
    }
}
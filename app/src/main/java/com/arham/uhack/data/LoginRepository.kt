package com.arham.uhack.data

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository {

    // in-memory cache of the loggedInUser object
    private var user: LoggedInUser? = null
    private var isLoggedIn: Boolean = false

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    //fun logout() {
        //user = null
        //dataSource.logout()
    //}

    //suspend fun login(username: String, password: String): Result<LoggedInUser> {
        // handle login
        //val result = dataSource.login(username, password)

        //if (result is Result.Success) {
            //setLoggedInUser(result.data)
        //}

        //return result
    //}

    fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        this.isLoggedIn = true
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}
package com.dev_musashi.randomchat.domain.repository

import com.dev_musashi.randomchat.domain.model.User
import com.dev_musashi.randomchat.domain.usecases.SignOut
import com.google.firebase.auth.AuthCredential

interface Repository {
    fun currentUser(): Boolean

    suspend fun hasNickName(
        onError: (Throwable) -> Unit,
        onSuccess: (Boolean) -> Unit
    )

    suspend fun signInWithEmailAndPassword(
        userEmail: String,
        userPassword: String,
        onResult: (Throwable?) -> Unit
    )

    suspend fun signInWithCredentialInAuth(
        credential: AuthCredential,
        onResult: (Throwable?) -> Unit
    )

    suspend fun createUserData(
        user: User,
        onResult: (Throwable?) -> Unit
    )

    suspend fun createUserAuth(
        userEmail: String,
        userPassword: String,
        onResult: (Throwable?) -> Unit
    )

    suspend fun getUserInfo(
        onError: (Throwable) -> Unit,
        onSuccess: (User) -> Unit
    )

    suspend fun changeUserData(
        user: User,
        onResult: (Throwable?) -> Unit
    )

    suspend fun online(
        onResult: (Throwable?) -> Unit
    )

    suspend fun connectUser(
        onResult: (Throwable?) -> Unit
    )

    suspend fun cancelConnect(
        onResult: (Throwable?) -> Unit
    )

    fun signOut()
}
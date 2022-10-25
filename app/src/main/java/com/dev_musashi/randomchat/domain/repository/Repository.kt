package com.dev_musashi.randomchat.domain.repository

import com.dev_musashi.randomchat.domain.model.User
import com.dev_musashi.randomchat.util.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult

interface Repository {
    fun currentUser(): Boolean

    suspend fun signInWithEmailAndPassword(userEmail: String, userPassword: String, ) : Resource<AuthResult>

    suspend fun signInWithCredentialInAuth(credential: AuthCredential) : Resource<AuthResult>

    suspend fun createUserAuth(userEmail: String, userPassword: String) : Resource<AuthResult>

    suspend fun hasUserData() : Resource<Boolean>

    suspend fun createUserData(user: User) : Resource<Throwable?>

    suspend fun getUserInfo() : Resource<User>

    suspend fun changeUserImage(userImage: String?) : Resource<Throwable?>

    suspend fun changeUserNickName(userNickName: String) : Resource<Throwable?>

    suspend fun online() : Resource<Throwable?>

    suspend fun changeStatus(currentStatus: String) : Resource<Throwable?>

//    suspend fun startMatching(): Resource<String?>

    suspend fun startMatching(onError: (Throwable)->Unit ,onSuccess: (String)->Unit, )

//    suspend fun createRoom(matchingUserId: String): Resource<String>

    suspend fun createRoom(matchingUserId: String,  onError: (Throwable)->Unit ,onSuccess: (String)->Unit)

    fun signOut()

    suspend fun disConnectUser()

}
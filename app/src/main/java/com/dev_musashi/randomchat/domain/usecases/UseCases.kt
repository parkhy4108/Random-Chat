package com.dev_musashi.randomchat.domain.usecases

data class UseCases(
    val changeUserImage: ChangeUserImage,
    val changeUserNickName: ChangeUserNickName,
    val createUserAuth: CreateUserAuth,
    val createUserData: CreateUserData,
    val currentUser: CurrentUser,
    val getUserData: GetUserData,
    val hasUserData: HasUserData,
    val online: Online,
    val signInWithCredential : SignInWithCredential,
    val signInWithEmailAndPassword: SignInWithEmailAndPassword,
    val signOut: SignOut,
    val disConnectUser: DisConnectUser,
    val changeConnectionStatus: ChangeConnectionStatus,
    val startMatching: StartMatching,
    val createRoom: CreateRoom
)

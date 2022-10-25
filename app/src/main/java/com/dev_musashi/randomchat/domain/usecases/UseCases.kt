package com.dev_musashi.randomchat.domain.usecases

data class UseCases(
    val changeUserData: ChangeUserData,
    val connectUser: ConnectUser,
    val cancelConnect: CancelConnect,
    val createUserAuth: CreateUserAuth,
    val createUserData: CreateUserData,
    val currentUser: CurrentUser,
    val getUserData: GetUserData,
    val hasNickName: HasNickName,
    val online: Online,
    val signInWithCredential : SignInWithCredential,
    val signInWithEmailAndPassword: SignInWithEmailAndPassword,
    val signOut: SignOut
)

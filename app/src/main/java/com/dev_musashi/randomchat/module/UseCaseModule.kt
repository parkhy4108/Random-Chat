package com.dev_musashi.randomchat.module

import com.dev_musashi.randomchat.domain.repository.Repository
import com.dev_musashi.randomchat.domain.usecases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideUseCases(repository: Repository) : UseCases {
        return UseCases(
            changeUserImage = ChangeUserImage(repository),
            changeUserNickName = ChangeUserNickName(repository),
            createUserAuth = CreateUserAuth(repository),
            createUserData = CreateUserData(repository),
            currentUser = CurrentUser(repository),
            getUserData = GetUserData(repository),
            hasUserData = HasUserData(repository),
            online = Online(repository),
            signInWithEmailAndPassword = SignInWithEmailAndPassword(repository),
            signInWithCredential = SignInWithCredential(repository),
            signOut = SignOut(repository),
            disConnectUser = DisConnectUser(repository),
            changeConnectionStatus = ChangeConnectionStatus(repository),
            startMatching = StartMatching(repository),
            createRoom = CreateRoom(repository)
        )
    }
}
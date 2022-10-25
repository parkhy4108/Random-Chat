package com.dev_musashi.randomchat.domain.usecases

import com.dev_musashi.randomchat.domain.repository.Repository
import javax.inject.Inject

class SignInWithEmailAndPassword @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(
        userEmail: String,
        userPassword: String,
        onResult: (Throwable?) -> Unit
    ) {
        return repository.signInWithEmailAndPassword(userEmail, userPassword, onResult)
    }
}
package com.dev_musashi.randomchat.domain.usecases

import com.dev_musashi.randomchat.domain.repository.Repository
import com.google.firebase.auth.AuthCredential
import javax.inject.Inject

class SignOut @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke() {
        return repository.signOut()
    }
}
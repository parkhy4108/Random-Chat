package com.dev_musashi.randomchat.domain.usecases

import com.dev_musashi.randomchat.domain.repository.Repository
import com.google.firebase.auth.AuthCredential
import javax.inject.Inject

class Online @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(onResult: (Throwable?)->Unit) {
        return repository.online(onResult)
    }
}
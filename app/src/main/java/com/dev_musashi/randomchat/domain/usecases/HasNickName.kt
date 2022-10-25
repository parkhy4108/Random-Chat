package com.dev_musashi.randomchat.domain.usecases

import com.dev_musashi.randomchat.domain.repository.Repository
import com.google.firebase.auth.AuthCredential
import javax.inject.Inject

class HasNickName @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(onError: (Throwable) -> Unit, onSuccess: (Boolean) -> Unit) {
        return repository.hasNickName(onError, onSuccess)
    }
}
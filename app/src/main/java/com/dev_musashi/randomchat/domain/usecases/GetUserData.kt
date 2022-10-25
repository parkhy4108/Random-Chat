package com.dev_musashi.randomchat.domain.usecases

import com.dev_musashi.randomchat.domain.model.User
import com.dev_musashi.randomchat.domain.repository.Repository
import javax.inject.Inject

class GetUserData @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(onError: (Throwable) -> Unit, onSuccess: (User) -> Unit) {
        return repository.getUserInfo(onError,onSuccess)
    }
}
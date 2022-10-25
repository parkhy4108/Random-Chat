package com.dev_musashi.randomchat.domain.usecases

import com.dev_musashi.randomchat.domain.model.User
import com.dev_musashi.randomchat.domain.repository.Repository
import javax.inject.Inject

class ConnectUser @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(onResult: (Throwable?)-> Unit) {
        return repository.connectUser(onResult)
    }
}
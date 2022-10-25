package com.dev_musashi.randomchat.domain.usecases

import com.dev_musashi.randomchat.domain.model.User
import com.dev_musashi.randomchat.domain.repository.Repository
import javax.inject.Inject

class ChangeUserData @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(
        user: User,
        onResult: (Throwable?)->Unit
    ) {
        return repository.changeUserData(user, onResult)
    }
}
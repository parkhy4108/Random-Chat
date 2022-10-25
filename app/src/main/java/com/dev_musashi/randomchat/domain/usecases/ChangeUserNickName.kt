package com.dev_musashi.randomchat.domain.usecases

import com.dev_musashi.randomchat.domain.model.User
import com.dev_musashi.randomchat.domain.repository.Repository
import com.dev_musashi.randomchat.util.Resource
import javax.inject.Inject

class ChangeUserNickName @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(userNickName: String) : Resource<Throwable?> {
        return repository.changeUserNickName(userNickName)
    }
}
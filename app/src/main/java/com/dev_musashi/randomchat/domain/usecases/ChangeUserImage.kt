package com.dev_musashi.randomchat.domain.usecases

import com.dev_musashi.randomchat.domain.model.User
import com.dev_musashi.randomchat.domain.repository.Repository
import com.dev_musashi.randomchat.util.Resource
import javax.inject.Inject

class ChangeUserImage @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(userImage: String?) : Resource<Throwable?> {
        return repository.changeUserImage(userImage = userImage)
    }
}
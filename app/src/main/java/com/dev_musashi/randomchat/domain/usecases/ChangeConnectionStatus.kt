package com.dev_musashi.randomchat.domain.usecases

import com.dev_musashi.randomchat.domain.repository.Repository
import com.dev_musashi.randomchat.util.Resource
import javax.inject.Inject

class ChangeConnectionStatus @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(currentStatus: String) : Resource<Throwable?> {
        return repository.changeStatus(currentStatus)
    }
}
package com.dev_musashi.randomchat.domain.usecases

import com.dev_musashi.randomchat.domain.repository.Repository
import com.dev_musashi.randomchat.util.Resource
import javax.inject.Inject

class HasUserData @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke() : Resource<Boolean> {
        return repository.hasUserData()
    }
}
package com.dev_musashi.randomchat.domain.usecases

import com.dev_musashi.randomchat.domain.repository.Repository
import com.dev_musashi.randomchat.util.Resource
import com.google.firebase.auth.AuthResult
import javax.inject.Inject

class CreateRoom @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(matchingUserId: String,  onError: (Throwable)->Unit ,onSuccess: (String)->Unit) {
        return repository.createRoom(matchingUserId,onError, onSuccess)
    }
}
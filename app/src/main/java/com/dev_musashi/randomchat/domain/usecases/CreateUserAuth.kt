package com.dev_musashi.randomchat.domain.usecases

import com.dev_musashi.randomchat.domain.repository.Repository
import com.dev_musashi.randomchat.util.Resource
import com.google.firebase.auth.AuthResult
import javax.inject.Inject

class CreateUserAuth @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(
        userEmail: String,
        userPassword: String
    ) : Resource<AuthResult> {
        return repository.createUserAuth(userEmail, userPassword)
    }
}
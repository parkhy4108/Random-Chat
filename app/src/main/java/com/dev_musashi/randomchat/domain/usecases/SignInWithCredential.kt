package com.dev_musashi.randomchat.domain.usecases

import com.dev_musashi.randomchat.domain.repository.Repository
import com.dev_musashi.randomchat.util.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import javax.inject.Inject

class SignInWithCredential @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(
        credential: AuthCredential
    ) : Resource<AuthResult> {
        return repository.signInWithCredentialInAuth(credential)
    }
}
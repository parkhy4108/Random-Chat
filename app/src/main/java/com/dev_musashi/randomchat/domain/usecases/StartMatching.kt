package com.dev_musashi.randomchat.domain.usecases

import com.dev_musashi.randomchat.domain.repository.Repository
import com.dev_musashi.randomchat.util.Resource
import javax.inject.Inject

class StartMatching @Inject constructor(
    private val repository: Repository
) {
//    suspend operator fun invoke() : Resource<String?> {
//        return repository.startMatching()
//    }

    suspend operator fun invoke( onError: (Throwable)->Unit, onSuccess: (String)->Unit,) {
        return repository.startMatching(onError ,onSuccess )
    }
}
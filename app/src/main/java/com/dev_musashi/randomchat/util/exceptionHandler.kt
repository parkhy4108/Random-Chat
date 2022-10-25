package com.dev_musashi.randomchat.util

inline fun <T> exceptionHandler(action: () -> Resource<T>): Resource<T> {
    return try {
        action()
    } catch (e: Exception) {
        Resource.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
    }
}
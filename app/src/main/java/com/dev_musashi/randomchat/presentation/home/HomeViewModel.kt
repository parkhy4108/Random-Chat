package com.dev_musashi.randomchat.presentation.home

import android.content.ContentValues.TAG
import android.os.CountDownTimer
import android.util.Log
import androidx.compose.animation.core.snap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_musashi.randomchat.common.Screen
import com.dev_musashi.randomchat.data.repositoryImpl.region
import com.dev_musashi.randomchat.domain.usecases.ChangeConnectionStatus
import com.dev_musashi.randomchat.domain.usecases.CreateRoom
import com.dev_musashi.randomchat.domain.usecases.StartMatching
import com.dev_musashi.randomchat.util.Resource
import com.dev_musashi.randomchat.util.SnackBarManager
import com.dev_musashi.randomchat.util.SnackBarMessage.Companion.toSnackBarMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val changeConnectionStatus: ChangeConnectionStatus,
    private val startMatching: StartMatching,
    private val createRoom: CreateRoom
) : ViewModel() {
    private lateinit var countDownTimer: CountDownTimer

    var state = mutableStateOf(HomeState())
        private set

    private var startTime: Long = 30000L
    private var leftTime: Long = 30000L
    private val isConnecting get() = state.value.isConnecting

    private val ref =
        Firebase.database(region).reference.child("status").child(FirebaseAuth.getInstance().uid!!)
    private var matchingUserId: String? = null
    private var room: String? = null

    private fun listener(open: (String) -> Unit) = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
//            if(snapshot.value == "Find") {
//                createRoom(open)
//            }
//            if(snapshot.value == "isFound") {
//                countDownTimer.cancel()
//                resetCountDownTimer()
//
//            }
            if (snapshot.value == "Find") {
//                createRoom(open)
            }

            if (snapshot.value != "NotReady" && snapshot.value != "Find"
                && snapshot.value != "isFound" && snapshot.value != "LookingForSomeone"
            ) {
                countDownTimer.cancel()
                resetCountDownTimer()
                state.value = state.value.copy(isConnecting = false, connect = "success")
                matchingUserId = null
                room = null
                open(Screen.ChattingRoom.passRoomId(snapshot.value.toString()))
            }

        }

        override fun onCancelled(error: DatabaseError) {

        }

    }


    fun buttonClick(open: (String) -> Unit) {
        connect(open)
    }


    private fun connect(open: (String) -> Unit) {
        state.value = state.value.copy(isConnecting = !isConnecting)
        viewModelScope.launch {
            if (isConnecting) {
                state.value = state.value.copy(connect = "connecting...")
                ref.addValueEventListener(listener(open))
                changeStatus("LookingForSomeone")
                matchingOn(open)
            } else {
                state.value = state.value.copy(connect = "cancel")
                changeStatus("NotReady")
                countDownTimer.cancel()
                resetCountDownTimer()
                ref.removeEventListener(listener(open))
            }
        }
    }

    private fun matchingOn(open: (String) -> Unit) {
        var roomID: String? = null

        Log.d(TAG, "match 시작")

        countDownTimer = object : CountDownTimer(leftTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d(TAG, "onTick: ${millisUntilFinished / 1000.toInt()}")
                leftTime = millisUntilFinished
                viewModelScope.launch {
                    startMatching(
                        onError = {
                            SnackBarManager.showMessage(it.toSnackBarMessage())
                            countDownTimer.onFinish()
                        }
                    ) {
                        Log.d(TAG, "matchingResult : $it")
                        matchingUserId = it
                        room = it
                        roomID = it
                        countDownTimer.onFinish()
                    }
                }
            }

            //            override fun onTick(millisUntilFinished: Long) {
//                Log.d(TAG, "onTick: ${millisUntilFinished/1000.toInt()}")
//                leftTime = millisUntilFinished
//                viewModelScope.launch {
//                    when(val matchingResult = startMatching()) {
//                        is Resource.Success -> {
//                            if(matchingResult.data != null) {
//                                Log.d(TAG, "matchingResult : ${matchingResult.data}")
//                                matchingUserId = matchingResult.data
//                                room = matchingResult.data
//                                roomID = matchingResult.data
//                                countDownTimer.onFinish()
//                            }
//                        }
//                        is Resource.Error -> {
//                            countDownTimer.onFinish()
//                        }
//                    }
//                }
//            }
            override fun onFinish() {
                countDownTimer.cancel()
                resetCountDownTimer()
                if(roomID != null) {
                    open(Screen.ChattingRoom.passRoomId(roomID!!))
                }
            }
        }.start()
    }

    private fun changeStatus(currentStatus: String) {
        viewModelScope.launch {
            changeConnectionStatus(currentStatus = currentStatus)
        }
    }

//    private fun createRoom(open: (String) -> Unit) {
//        countDownTimer.cancel()
//        resetCountDownTimer()
//        viewModelScope.launch {
//            createRoom(
//                matchingUserId = matchingUserId!!,
//                onError = { SnackBarManager.showMessage(it.message!!) }
//            ) {
//                room = it
//                Firebase.database(region).reference
//                    .child("status").child(FirebaseAuth.getInstance().uid!!).setValue(it)
//                    .addOnCompleteListener { result ->
//                        if (result.exception != null) { SnackBarManager.showMessage(result.exception!!.toSnackBarMessage())}
//                    }
//            }
//        }
//            when(val roomID = createRoom(matchingUserId!!)){
//                is Resource.Success -> {
//                    if(roomID.data != null) {
//                        room = roomID.data
//                        Firebase.database(region).reference
//                            .child("status").child(FirebaseAuth.getInstance().uid!!).setValue(roomID.data).await()
////                        open(Screen.ChattingRoom.passRoomId(roomID.data))
//                    }
//                }
//                is Resource.Error -> {
//
//                }
//            }
//    }

    private fun resetCountDownTimer() {
        leftTime = startTime
    }
}

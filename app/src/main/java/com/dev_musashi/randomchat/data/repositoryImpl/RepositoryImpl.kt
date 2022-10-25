package com.dev_musashi.randomchat.data.repositoryImpl

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import com.dev_musashi.randomchat.R
import com.dev_musashi.randomchat.data.data.ChatUser
import com.dev_musashi.randomchat.domain.model.User
import com.dev_musashi.randomchat.domain.repository.Repository
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.*
import javax.inject.Inject

const val region = "https://randomchat-f322b-default-rtdb.asia-southeast1.firebasedatabase.app"

class RepositoryImpl @Inject constructor(
    context: Context
) : Repository {
    val drawable = ContextCompat.getDrawable(context, R.drawable.ic_person)
    private val bitmap = drawable!!.toBitmap()
    private val byteArrayStream = ByteArrayOutputStream()

    val ref = FirebaseDatabase.getInstance(region)
        .reference.child("status").child("notconnect")

    override fun currentUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    override suspend fun hasNickName(
        onError: (Throwable) -> Unit,
        onSuccess: (Boolean) -> Unit
    ) {
        val uid = Firebase.auth.uid!!
        FirebaseDatabase.getInstance(region).reference
            .child("users")
            .child(uid)
            .get()
            .addOnSuccessListener {
                if (it.value == null) onSuccess(false)
                else onSuccess(true)
            }
            .addOnFailureListener { onError(it) }
    }

    override suspend fun signInWithEmailAndPassword(
        userEmail: String,
        userPassword: String,
        onResult: (Throwable?) -> Unit
    ) {
        Firebase.auth.signInWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener { onResult(it.exception) }
    }

    override suspend fun signInWithCredentialInAuth(
        credential: AuthCredential,
        onResult: (Throwable?) -> Unit
    ) {
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener { result ->
                onResult(result.exception)
            }
    }

    override suspend fun createUserData(user: User, onResult: (Throwable?) -> Unit) {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayStream)
        val data = byteArrayStream.toByteArray()
        val uid = Firebase.auth.uid!!
        val storage = Firebase.storage.reference
        if (user.userImage == null) {
            storage
                .child("userImages")
                .child(uid)
                .putBytes(data)
                .addOnSuccessListener { task ->
                    task.storage.downloadUrl
                        .addOnSuccessListener { imageUrl ->
                            val userData = user.copy(uid = uid, userImage = imageUrl)
                            createUserDataInRealTimeDB(userData) {
                                if (it == null) {
                                    onResult(null)
                                } else {
                                    onResult(it)
                                }
                            }
                        }
                        .addOnFailureListener { onResult(it) }

                }
                .addOnFailureListener { onResult(it) }
        } else {
            storage
                .child("userImages")
                .child(uid)
                .putFile(user.userImage)
                .addOnSuccessListener { task ->
                    task.storage.downloadUrl
                        .addOnSuccessListener { imageUrl ->
                            val userData = user.copy(uid = uid, userImage = imageUrl)
                            createUserDataInRealTimeDB(userData) {
                                if (it == null) {
                                    onResult(null)
                                } else {
                                    onResult(it)
                                }
                            }
                        }
                        .addOnFailureListener { onResult(it) }

                }
                .addOnFailureListener { onResult(it) }
        }


    }

    override suspend fun createUserAuth(
        userEmail: String,
        userPassword: String,
        onResult: (Throwable?) -> Unit
    ) {
        Firebase.auth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener { onResult(it.exception) }
    }


    override suspend fun getUserInfo(
        onError: (Throwable) -> Unit,
        onSuccess: (User) -> Unit
    ) {
        val uid = Firebase.auth.uid!!
        FirebaseDatabase.getInstance(region).reference
            .child("users")
            .child(uid)
            .get()
            .addOnSuccessListener {
                val user = User(
                    userName = it.child("userName").value.toString(),
                    userImage = it.child("userImage").toString().toUri(),
                    uid = it.child("uid").toString()
                )
                onSuccess(user)
            }
            .addOnFailureListener { onError(it) }
    }

    override suspend fun changeUserData(user: User, onResult: (Throwable?) -> Unit) {
        val uid = Firebase.auth.uid!!

        Firebase.storage.reference
            .child("userImages")
            .child(uid)
            .delete()
            .addOnSuccessListener {
                CoroutineScope(Dispatchers.IO).launch {
                    createUserData(user, onResult)
                }
            }
            .addOnFailureListener { onResult(it) }
    }

    override suspend fun online(onResult: (Throwable?) -> Unit) {
        val uid = Firebase.auth.currentUser!!.uid
        FirebaseDatabase.getInstance(region).reference
            .child("status")
            .child("notconnect")
            .updateChildren(mapOf(uid to false))
            .addOnCompleteListener { onResult(it.exception) }
    }


    override fun signOut() {
        Firebase.auth.signOut()
    }

    private fun createUserDataInRealTimeDB(
        user: User,
        onResult: (Throwable?) -> Unit
    ) {
        FirebaseDatabase.getInstance(region).reference
            .child("users")
            .child(user.uid)
            .setValue(
                mapOf(
                    "uid" to user.uid,
                    "userImage" to user.userImage.toString(),
                    "userName" to user.userName
                )
            )
            .addOnSuccessListener {
                onResult(null)
            }
            .addOnFailureListener {
                onResult(it)
            }
    }

    override suspend fun connectUser(onResult: (Throwable?) -> Unit) {
        changeMyConnectionStatus {
            if (it == null) {
                getMatchingUserUid(
                    onError = { onResult(it) }
                ) { uid ->
                    Log.d("TAG", "uid : $uid ")
//                    checkRoom(uid){ roomId ->
//                        if(roomId == null) {
//                            createRoom(uid) {
//                                onResult(it)
//                            }
//                        } else {
//
//                        }
//                    }
                }
            } else {
                onResult(it)
            }
        }


    }

    private fun changeMyConnectionStatus(onResult: (Throwable?) -> Unit) {
        val myUid = Firebase.auth.currentUser!!.uid
        FirebaseDatabase.getInstance(region)
            .reference.child("status").child("notconnect")
            .updateChildren(mapOf(myUid to true))
            .addOnSuccessListener { onResult(null) }
            .addOnFailureListener { onResult(it) }
    }

    override suspend fun cancelConnect(onResult: (Throwable?) -> Unit) {
        val myUid = Firebase.auth.currentUser!!.uid
        ref
            .updateChildren(mapOf(myUid to false))
            .addOnSuccessListener {
                onResult(null)
            }
            .addOnFailureListener { onResult(it) }
    }

    private fun checkRoom(
        matchingUserUid: String,
        roomId: (String?) -> Unit
    ) {
        val myUid = Firebase.auth.currentUser!!.uid
        val ref = FirebaseDatabase.getInstance(region)
            .reference.child("chatRoom")
            .orderByChild("users/$myUid").equalTo(true)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (item in snapshot.children) {
                    val chatUser = item.getValue(ChatUser::class.java)
                    if (chatUser?.users?.contains(matchingUserUid) == true) {
                        roomId(item.key!!)
                    } else {
                        roomId(null)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

//    private fun getMatchingUserUid(
//        onError: (Throwable) -> Unit,
//        onSuccess: (String) -> Unit
//    ) {
//        var matchingUser: String?
//        val myUid = Firebase.auth.currentUser!!.uid
//        val ref = FirebaseDatabase.getInstance(region)
//            .reference.child("status").child("notconnect")
//        ref.get()
//            .addOnSuccessListener { notConnect ->
//                val userList = notConnect.children.toList()
//                val random = Random()
//
//                while (true) {
//                    val cnt = random.nextInt(userList.size)
//                    if (userList[cnt].key == myUid) continue
//                    else {
//                        matchingUser = userList[cnt].key.toString()
//                        break
//                    }
//                }
//                matchingUser?.let { onSuccess(it) }
//            }
//            .addOnFailureListener { onError(it) }
//    }

//    private fun getMatchingUserUid(
//        onError: (Throwable) -> Unit,
//        onSuccess: (String) -> Unit
//    ) {
//        val random = Random()
//        var matchingUser: String?
//        val myUid = Firebase.auth.currentUser!!.uid
//
//        ref.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                Log.d("TAG", "반복중")
//                val userList = snapshot.children.toList()
//                val trueList = userList.filter { it.value == true }.filterNot { it.key == myUid }
//                    .toMutableList()
//                Log.d("TAG", "trueList : $trueList")
//                if (trueList.size != 0) {
//                    val cnt = random.nextInt(trueList.size)
//                    matchingUser = trueList[cnt].key.toString()
//                    Log.d("TAG", "matchingUser: $matchingUser")
//                    matchingUser?.let { onSuccess(it) }
//                } else {
//                    onSuccess("현재 대기 인원 : 0명 잠시후 다시 시도해주시기 바랍니다.")
//                }
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })
//    }

        private fun getMatchingUserUid(
        onError: (Throwable) -> Unit,
        onSuccess: (String) -> Unit
    ) {
        val random = Random()
        var matchingUser: String?
        val myUid = Firebase.auth.currentUser!!.uid

        ref.addChildEventListener(object : ChildEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                Log.d("TAG", "반복중")
//                val userList = snapshot.children.toList()
//                val trueList = userList.filter { it.value == true }.filterNot { it.key == myUid }
//                    .toMutableList()
//                Log.d("TAG", "trueList : $trueList")
//                if (trueList.size != 0) {
//                    val cnt = random.nextInt(trueList.size)
//                    matchingUser = trueList[cnt].key.toString()
//                    Log.d("TAG", "matchingUser: $matchingUser")
//                    matchingUser?.let { onSuccess(it) }
//                } else {
//                    onSuccess("현재 대기 인원 : 0명 잠시후 다시 시도해주시기 바랍니다.")
//                }
//
//            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun createRoom(matchingUserUid: String, onResult: (Throwable?) -> Unit) {
        val myUid = Firebase.auth.currentUser!!.uid
        val map = mutableMapOf<String, Boolean>()
        map[myUid] = true
        map[matchingUserUid] = true
        val chatUser = ChatUser(
            users = map,
            comments = null
        )
        FirebaseDatabase.getInstance(region).reference.child("chatRoom")
            .push()
            .setValue(chatUser)
            .addOnSuccessListener { onResult(null) }
            .addOnFailureListener { onResult(it) }
    }
}
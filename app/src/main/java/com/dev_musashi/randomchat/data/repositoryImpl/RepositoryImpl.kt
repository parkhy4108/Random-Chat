package com.dev_musashi.randomchat.data.repositoryImpl

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import com.dev_musashi.randomchat.data.data.ChatUser
import com.dev_musashi.randomchat.domain.model.User
import com.dev_musashi.randomchat.domain.repository.Repository
import com.dev_musashi.randomchat.util.Resource
import com.dev_musashi.randomchat.util.exceptionHandler
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import com.dev_musashi.randomchat.R.drawable as AppImg

const val region = "https://randomchat-f322b-default-rtdb.asia-southeast1.firebasedatabase.app"

class RepositoryImpl @Inject constructor(
    context: Context
) : Repository {
    private val mContext = context


    /**
     * 현재 유저 정보가 있는지 판별
     * @return 유저 있으면 true 없으면 false
     */
    override fun currentUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    /** 이메일 비밀번호 회원가입 함수
     *  @param userEmail 가입 이메일
     *  @param userPassword 가입 비밀번호
     */
    override suspend fun signInWithEmailAndPassword(
        userEmail: String,
        userPassword: String
    ): Resource<AuthResult> {
        return withContext(Dispatchers.IO) {
            exceptionHandler {
                val signIn =
                    Firebase.auth.signInWithEmailAndPassword(userEmail, userPassword).await()
                Resource.Success(signIn)
            }
        }
    }

    /**
     * 구글 회원가입 Credential 생성 함수
     * @param credential Google Credential
     */
    override suspend fun signInWithCredentialInAuth(
        credential: AuthCredential
    ): Resource<AuthResult> {
        return withContext(Dispatchers.IO) {
            exceptionHandler {
                val signInCredential = Firebase.auth.signInWithCredential(credential).await()
                Resource.Success(signInCredential)
            }
        }
    }

    /**
     * 유저 Auth 만드는 함수
     * @param userEmail 이메일
     * @param userPassword 패스워드
     * @return AuthResult
     */
    override suspend fun createUserAuth(
        userEmail: String,
        userPassword: String,
    ): Resource<AuthResult> {
        return withContext(Dispatchers.IO) {
            exceptionHandler {
                val register =
                    Firebase.auth.createUserWithEmailAndPassword(userEmail, userPassword).await()
                Resource.Success(register)
            }
        }
    }

    /**
     * 리얼타임 디비에 유저정보가 존재하는지 확인하는 함수
     * @return Boolean
     */
    override suspend fun hasUserData(): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            exceptionHandler {
                val uid = Firebase.auth.uid!!
                val usersRef = FirebaseDatabase.getInstance(region).reference
                    .child("users")
                    .child(uid)
                    .get().await()
                if (usersRef.value == null) Resource.Success(false)
                else Resource.Success(true)
            }
        }
    }


    /**
     * 유저 정보를 저장하는 함수
     * 닉네임 화면에서 프로필 사진을 설정한 경우, 설정하지 않은 경우 나눠서
     * Storage 에 저장하는 과정
     * RealTime Database 에 저장
     * @param user User 데이터
     * @return Resource<Throwable?>
     */
    override suspend fun createUserData(user: User): Resource<Throwable?> {
        return withContext(Dispatchers.IO) {
            exceptionHandler {
                val drawable = ContextCompat.getDrawable(mContext, AppImg.ic_person)
                val bitmap = drawable!!.toBitmap()
                val byteArrayStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayStream)
                val data = byteArrayStream.toByteArray()
                val uid = Firebase.auth.uid!!

                if (user.userImage == null) {
                    val storageWork =
                        Firebase.storage.reference.child("userImages").child(uid).putBytes(data)
                            .await()
                    val storageUrl = storageWork.storage.downloadUrl.await()
                    val userData = user.copy(uid = uid, userImage = storageUrl.toString())
                    createUserDataInRealTimeDB(user = userData)
                    Resource.Success(null)
                } else {
                    val storageWork = Firebase.storage.reference.child("userImages").child(uid)
                        .putFile(user.userImage.toUri()).await()
                    val storageUrl = storageWork.storage.downloadUrl.await()
                    val userData = user.copy(uid = uid, userImage = storageUrl.toString())
                    createUserDataInRealTimeDB(user = userData)
                    Resource.Success(null)
                }
            }
        }
    }

    /**
     * 유저 정보를 RealTime DB 에 저장하는 함수
     * @param user User 데이터
     * @return 예외처리
     */
    private suspend fun createUserDataInRealTimeDB(
        user: User
    ): Throwable? {
        return withContext(Dispatchers.IO) {
            try {
                Firebase.database(region).reference
                    .child("users")
                    .child(user.uid)
                    .setValue(
                        mapOf(
                            "uid" to user.uid,
                            "userImage" to user.userImage.toString(),
                            "userName" to user.userName
                        )
                    ).await()
                null
            } catch (e: Exception) {
                e
            }
        }
    }

    /**
     * user 정보를 RealTimeDB 에서 가져오는 함수
     */
    override suspend fun getUserInfo(): Resource<User> {
        return withContext(Dispatchers.IO) {
            exceptionHandler {
                val uid = Firebase.auth.uid!!
                val getUser = FirebaseDatabase.getInstance(region).reference
                    .child("users")
                    .child(uid)
                    .get().await()
                val user = User(
                    userName = getUser.child("userName").value.toString(),
                    userImage = getUser.child("userImage").value.toString(),
                    uid = getUser.child("uid").toString()
                )
                Resource.Success(user)
            }
        }
    }

    /**
     * user 데이터를 변경하는 함수
     * 프로필 화면에서 사진과 닉네임 변경시
     * 기존 이미지 삭제 후 새로운 이미지 생성 과정
     * @param userImage UserImage 데이터
     */
    override suspend fun changeUserImage(userImage: String?): Resource<Throwable?> {
        return withContext(Dispatchers.IO) {
            exceptionHandler {
                saveNewUserImage(userImage = userImage)
                deleteExistUserImage()
                Resource.Success(null)
            }
        }
    }

    override suspend fun changeUserNickName(userNickName: String): Resource<Throwable?> {
        return withContext(Dispatchers.IO) {
            exceptionHandler {
                val uid = Firebase.auth.uid!!
                Firebase.database(region).reference.child("users").child(uid).child("userName")
                    .setValue(userNickName).await()
                Resource.Success(null)
            }
        }
    }

    private suspend fun saveNewUserImage(userImage: String?): Resource<Throwable?> {
        return withContext(Dispatchers.IO) {
            exceptionHandler {
                val drawable = ContextCompat.getDrawable(mContext, AppImg.ic_person)
                val bitmap = drawable!!.toBitmap()
                val byteArrayStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayStream)
                val data = byteArrayStream.toByteArray()
                val uid = Firebase.auth.currentUser!!.uid

                if (userImage == null) {
                    val storageWork =
                        Firebase.storage.reference.child("userImages").child(uid).putBytes(data)
                            .await()
                    val storageUrl = storageWork.storage.downloadUrl.await()
                    changeUserImageUrl(storageUrl.toString())
                    Resource.Success(null)
                } else {
                    val storageWork = Firebase.storage.reference.child("userImages").child(uid)
                        .putFile(userImage.toUri()).await()
                    val storageUrl = storageWork.storage.downloadUrl.await()
                    changeUserImageUrl(storageUrl.toString())
                    Resource.Success(null)
                }
            }
        }
    }

    private suspend fun changeUserImageUrl(url: String): Resource<Throwable?> {
        return withContext(Dispatchers.IO) {
            exceptionHandler {
                val uid = Firebase.auth.currentUser!!.uid
                Firebase.database(region).reference.child("users").child(uid).child("userName")
                    .setValue(url).await()
                Resource.Success(null)
            }
        }
    }

    private suspend fun deleteExistUserImage(): Throwable? {
        return withContext(Dispatchers.IO) {
            val uid = Firebase.auth.currentUser!!.uid
            try {
                Firebase.storage.reference.child("userImages").child(uid).delete().await()
                null
            } catch (e: Exception) {
                e
            }
        }
    }

    /**
     * user 앱 활성화 시 RealTime DB 에 online 상태로 변경하는 함수
     */
    override suspend fun online(): Resource<Throwable?> {
        return withContext(Dispatchers.IO) {
            exceptionHandler {
                val uid = Firebase.auth.currentUser!!.uid
                Firebase.database(region).reference
                    .child("status")
                    .updateChildren(mapOf(uid to "NotReady"))
                    .await()
                Resource.Success(null)
            }
        }
    }

    override fun signOut() {
        Firebase.auth.signOut()
    }

    override suspend fun disConnectUser() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val notConnect = FirebaseDatabase.getInstance(region).reference
            .child("status").child(uid)
        notConnect.onDisconnect().removeValue()
    }
///////////////////////////////////////////////////////////


    override suspend fun changeStatus(currentStatus: String): Resource<Throwable?> {
        return withContext(Dispatchers.IO) {
            exceptionHandler {
                val myUid = Firebase.auth.currentUser!!.uid
                Firebase.database(region).reference.child("status")
                    .updateChildren(mapOf(myUid to currentStatus))
                Resource.Success(null)
            }
        }
    }

    override suspend fun startMatching(onError: (Throwable) -> Unit, onSuccess: (String) -> Unit) {
        var roomID: String? = null
        var matchingUser: String? = null
        val myUid = Firebase.auth.currentUser!!.uid
        Firebase.database(region).reference.child("status").get()
            .addOnSuccessListener {
                val myRef = Firebase.database(region).reference.child("status").child(myUid).addValueEventListener(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    }
                )
                val exceptMeList = it.children.filter { it.key != myUid }.toList()
                for (user in exceptMeList) {
                    if (user.value == "LookingForSomeone") {
                        matchingUser = user.key!!
                        val matchingUserStatusRef = Firebase.database(region).reference
                            .child("status").child(matchingUser!!)
                        matchingUserStatusRef.setValue("isFound")
                            .addOnSuccessListener {
                                Firebase.database(region).reference
                                    .child("status").child(myUid).setValue("Find")
                                    .addOnSuccessListener {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            createRoom(matchingUser!!, onError = { it }) { key ->
                                                roomID = key
                                                onSuccess(key)
                                            }
                                        }
                                    }
                                    .addOnFailureListener { error -> onError(error) }
                            }
                            .addOnFailureListener { error -> onError(error) }
                        break
                    }
                }
            }
            .addOnFailureListener { onError(it) }
    }


//    override suspend fun startMatching(): Resource<String?> {
//        return withContext(Dispatchers.IO) {
//            exceptionHandler {
//                var roomID: String? = null
//                var matchingUser: String? = null
//                val myUid = Firebase.auth.currentUser!!.uid
//                val onlineUsers = Firebase.database(region).reference.child("status").get().await()
//                val exceptMeList = onlineUsers.children.filter { it.key != myUid }.toList()
//                for (user in exceptMeList) {
//                    if (user.value == "LookingForSomeone") {
//                        matchingUser = user.key!!
//                        Firebase.database(region).reference
//                            .child("status").child(matchingUser).setValue("isFound").await()
//                        changeStatus("Find")
////                        when(val createRoom = createRoom(matchingUser)) {
////                            is Resource.Success -> {
////                                roomID = createRoom.data
////                            }
////                            is Resource.Error -> {
////
////                            }
////                        }
//                        break
//                    }
//                }
////                Resource.Success(matchingUser)
//                Resource.Success(matchingUser)
//            }
//        }
//    }

//    override suspend fun startMatching(onError: (Throwable) -> Unit , onSuccess: (String) -> Unit) {
//        var roomID: String? = null
//        var matchingUser: String? = null
//        val myUid = Firebase.auth.currentUser!!.uid
//        Firebase.database(region).reference.child("status").get()
//            .addOnSuccessListener {
//                val exceptMeList = it.children.filter { it.key != myUid }.toList()
//                for (user in exceptMeList) {
//                    if (user.value == "LookingForSomeone") {
//                        matchingUser = user.key!!
//                        val matchingUserStatusRef = Firebase.database(region).reference
//                            .child("status").child(matchingUser!!)
//                        matchingUserStatusRef.setValue("isFound")
//                            .addOnSuccessListener {
//                                Firebase.database(region).reference
//                                    .child("status").child(myUid).setValue("Find")
//                                    .addOnSuccessListener {
//                                        CoroutineScope(Dispatchers.IO).launch {
//                                            createRoom(matchingUser!! , onError = {it}) { key ->
//                                                roomID = key
//                                                onSuccess(key)
//                                            }
//                                        }
//                                    }
//                                    .addOnFailureListener { error -> onError(error) }
//                            }
//                            .addOnFailureListener { error -> onError(error) }
//                        break
//                    }
//                }
//            }
//            .addOnFailureListener { onError(it) }
//    }

//    override suspend fun startMatching(): Resource<String?> {
//        return withContext(Dispatchers.IO) {
//            exceptionHandler {
//                var roomID: String? = null
//                var matchingUser: String? = null
//                val myUid = Firebase.auth.currentUser!!.uid
//                val onlineUsers = Firebase.database(region).reference.child("status").get().await()
//                val exceptMeList = onlineUsers.children.filter { it.key != myUid }.toList()
//                for (user in exceptMeList) {
//                    if (user.value == "LookingForSomeone") {
//                        matchingUser = user.key!!
//                        Firebase.database(region).reference
//                            .child("status").child(matchingUser).setValue("isFound").await()
//                        changeStatus("Find")
////                        when(val createRoom = createRoom(matchingUser)) {
////                            is Resource.Success -> {
////                                roomID = createRoom.data
////                            }
////                            is Resource.Error -> {
////
////                            }
////                        }
//                        break
//                    }
//                }
////                Resource.Success(matchingUser)
//                Resource.Success(matchingUser)
//            }
//        }
//    }


    override suspend fun createRoom(
        matchingUserId: String,
        onError: (Throwable)->Unit,
        onSuccess: (String)->Unit)
    {
        var roomKey: String? = ""
        val myUid = Firebase.auth.currentUser!!.uid
        val map = mutableMapOf<String, Boolean>()
        map[myUid] = true
        map[matchingUserId] = true
        val chatUser = ChatUser(
            users = map,
            comments = null
        )
        when (val check = checkRoom(matchingUserId)) {
            is Resource.Success -> {
                if (check.data == null) {
                    val ref = Firebase.database(region).reference.child("chatRoom")
                    roomKey = ref.push().key.toString()
                    Firebase.database(region).reference
                        .child("status").child(matchingUserId).setValue(roomKey)
                        .addOnSuccessListener {
                            ref.child(roomKey!!).setValue(chatUser)
                                .addOnSuccessListener { onSuccess(roomKey!!) }
                                .addOnFailureListener { onError(it) }
                        }
                        .addOnFailureListener { onError(it) }
                } else {
                    roomKey = check.data
                    Firebase.database(region).reference
                        .child("status").child(matchingUserId).setValue(roomKey)
                        .addOnSuccessListener { onSuccess(roomKey) }
                        .addOnFailureListener { onError(it) }
                }
            }
            is Resource.Error -> {

            }
        }
    }

//    override suspend fun createRoom(matchingUserId: String): Resource<String> {
//        return withContext(Dispatchers.IO) {
//            var roomKey: String? = ""
//            val myUid = Firebase.auth.currentUser!!.uid
//            val map = mutableMapOf<String, Boolean>()
//            map[myUid] = true
//            map[matchingUserId] = true
//            val chatUser = ChatUser(
//                users = map,
//                comments = null
//            )
//            try {
//                when (val check = checkRoom(matchingUserId)) {
//                    is Resource.Success -> {
//                        if (check.data == null) {
//                            val ref = Firebase.database(region).reference.child("chatRoom")
//                            roomKey = ref.push().key.toString()
//                            ref.child(roomKey).setValue(chatUser).await()
//                            Firebase.database(region).reference
//                                .child("status").child(matchingUserId).setValue(roomKey).await()
////                            changeStatus(roomKey)
//                        } else {
//                            roomKey = check.data
//                            Firebase.database(region).reference
//                                .child("status").child(matchingUserId).setValue(roomKey).await()
////                            changeStatus(roomKey)
//
//                        }
//                    }
//                    is Resource.Error -> {
//
//                    }
//                }
//                Resource.Success(roomKey!!)
//            } catch (e: Exception) {
//                Resource.Error(e.toString())
//            }
//        }
//    }


    private suspend fun checkRoom(
        matchingUserUid: String
    ): Resource<String?> {
        return withContext(Dispatchers.IO) {
            exceptionHandler {
                Log.d(TAG, "checkRoom")
                val myUid = Firebase.auth.currentUser!!.uid
                val allRoom = Firebase.database(region).reference.child("chatRoom").get().await()
                val myRooms = allRoom.children.filter { room -> room.child("users").child(myUid).value == true }
                val room = myRooms.filter { it.child("users").child(matchingUserUid).value == true }
                Log.d(TAG, "room : $room")
                if (room.isEmpty()) {
                    Resource.Success(null)
                } else {
                    Resource.Success(room[0].key)
                }
            }
        }
    }
}


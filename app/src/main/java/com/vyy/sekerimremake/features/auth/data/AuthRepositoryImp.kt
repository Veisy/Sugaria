package com.vyy.sekerimremake.features.auth.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.vyy.sekerimremake.features.auth.domain.model.UserInfoModel
import com.vyy.sekerimremake.features.auth.domain.repository.AuthRepository
import com.vyy.sekerimremake.utils.FirestoreConstants.ACCOUNT
import com.vyy.sekerimremake.utils.FirestoreConstants.USER_INFO
import com.vyy.sekerimremake.utils.Response
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImp @Inject constructor(
    private val auth: FirebaseAuth,
    private val usersRef: CollectionReference
) : AuthRepository {

    private fun getAccountRef(): CollectionReference? {
        val uid = auth.currentUser?.uid
        return if (uid != null) {
            usersRef.document(uid).collection(ACCOUNT)
        } else {
            null
        }
    }

    override fun getAuthState() = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser == null)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    override fun getUserInfo() = callbackFlow {
        getAccountRef()?.document(USER_INFO)?.get()?.addOnSuccessListener { document ->
            val response = if (document != null) {
                try {
                    val userInfoModel = document.toObject(UserInfoModel::class.java)
                    Response.Success(userInfoModel)
                } catch (e: Exception) {
                    Response.Error("Failed to convert Firebase snapshot to UserInfoModel object. ${e.message ?: e.toString()}")
                }
            } else {
                Response.Error("Document is null.")
            }
            trySend(response)
        }
            ?.addOnFailureListener { exception ->
                val response = Response.Error(exception.message ?: exception.toString())
                trySend(response)
            }

        awaitClose {}
    }
}
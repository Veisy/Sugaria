package com.vyy.sekerimremake.features.settings.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.vyy.sekerimremake.features.settings.domain.model.UserModel
import com.vyy.sekerimremake.features.settings.domain.repository.RequestMonitorResponse
import com.vyy.sekerimremake.features.settings.domain.repository.SettingsRepository
import com.vyy.sekerimremake.utils.FirestoreConstants.NAME
import com.vyy.sekerimremake.utils.FirestoreConstants.UID
import com.vyy.sekerimremake.utils.FirestoreConstants.USER_NAME
import com.vyy.sekerimremake.utils.FirestoreConstants.WAITING_MONITORS
import com.vyy.sekerimremake.utils.Response
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SettingsRepositoryImp @Inject constructor(
    private val auth: FirebaseAuth, private val usersRef: CollectionReference
) : SettingsRepository {

    private fun getAccountRef(): DocumentReference? {
        val uid = auth.currentUser?.uid
        return if (uid != null) {
            usersRef.document(uid)
        } else {
            null
        }
    }

    override fun getUser() = callbackFlow {
        val snapshotListener = getAccountRef()?.addSnapshotListener { snapshot, e ->
            val response = if (snapshot != null) {
                try {
                    val userModel = snapshot.toObject(UserModel::class.java)
                    Response.Success(userModel)
                } catch (e: Exception) {
                    Response.Error("Failed to convert Firebase snapshot to userModel object. ${e.message ?: e.toString()}")
                }
            } else {
                Response.Error(e?.message ?: e.toString())
            }
            trySend(response)
        }
        awaitClose {
            snapshotListener?.remove()
        }
    }

    override suspend fun requestMonitor(user: UserModel): RequestMonitorResponse {
        return try {
            if (user.uid != null && user.name != null && user.userName != null) {
                getAccountRef()?.update(
                    WAITING_MONITORS, FieldValue.arrayUnion(
                        mapOf(
                            UID to user.uid,
                            NAME to user.name,
                            USER_NAME to user.userName
                        )
                    )
                )?.await()
                Response.Success(true)
            } else {
                Response.Error("User uid, name or userName is null.")
            }

        } catch (e: Exception) {
            Response.Error(e.message ?: e.toString())
        }
    }
}
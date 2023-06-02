package com.vyy.sekerimremake.features.settings.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.vyy.sekerimremake.features.settings.domain.model.UserModel
import com.vyy.sekerimremake.features.settings.domain.repository.SettingsRepository
import com.vyy.sekerimremake.utils.Response
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SettingsRepositoryImp @Inject constructor(
    private val auth: FirebaseAuth,
    private val usersRef: CollectionReference
) : SettingsRepository {

    private fun getAccountRef(): DocumentReference? {
        val uid = auth.currentUser?.uid
        return if (uid != null) {
            usersRef.document(uid)
        } else {
            null
        }
    }

    override fun getUser() =  callbackFlow {
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
}
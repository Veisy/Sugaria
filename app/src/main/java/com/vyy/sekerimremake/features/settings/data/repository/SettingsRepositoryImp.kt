package com.vyy.sekerimremake.features.settings.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.vyy.sekerimremake.features.settings.domain.model.ContactsModel
import com.vyy.sekerimremake.features.settings.domain.model.UserInfoModel
import com.vyy.sekerimremake.features.settings.domain.repository.SettingsRepository
import com.vyy.sekerimremake.utils.FirestoreConstants
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

    private fun getAccountRef(): CollectionReference? {
        val uid = auth.currentUser?.uid
        return if (uid != null) {
            usersRef.document(uid).collection(FirestoreConstants.ACCOUNT)
        } else {
            null
        }
    }

    override fun getUserInfo() = callbackFlow {
        getAccountRef()?.document(FirestoreConstants.USER_INFO)?.get()?.addOnSuccessListener { document ->
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

    override fun getContacts() =  callbackFlow {
        val snapshotListener = getAccountRef()?.document(FirestoreConstants.CONTACTS)?.addSnapshotListener { snapshot, e ->
            val response = if (snapshot != null) {
                try {
                    val chartDayModels = snapshot.toObject(ContactsModel::class.java)
                    Response.Success(chartDayModels)
                } catch (e: Exception) {
                    Response.Error("Failed to convert Firebase snapshot to ContactsModel object. ${e.message ?: e.toString()}")
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
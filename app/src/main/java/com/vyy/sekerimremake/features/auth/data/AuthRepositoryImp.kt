package com.vyy.sekerimremake.features.auth.data

import com.google.firebase.auth.FirebaseAuth
import com.vyy.sekerimremake.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImp @Inject constructor(
    private val auth: FirebaseAuth,
) : AuthRepository {

    override fun getAuthState() = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser == null)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }
}
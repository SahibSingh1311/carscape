package com.dmag.carscape.data.repository

import com.dmag.carscape.core.common.DispatcherProvider
import com.dmag.carscape.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val dispatchers: DispatcherProvider
) : AuthRepository{
    override suspend fun getPlayerId(): String = withContext(dispatchers.io) {
        val existingUser = auth.currentUser
        if (existingUser != null) {
            return@withContext existingUser.uid
        }

        val result = auth.signInAnonymously().await()
        result.user?.uid
            ?: throw IllegalStateException("Anonymous sign-in succeeded but returned no user")
    }
}
package com.dmag.carscape.data.fakes

import com.dmag.carscape.domain.repository.AuthRepository

class FakeAuthRepository(private val playerId: String = "test-uid") : AuthRepository {
    override suspend fun getPlayerId(): String = playerId
}
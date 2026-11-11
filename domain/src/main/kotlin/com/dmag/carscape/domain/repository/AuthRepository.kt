package com.dmag.carscape.domain.repository

interface AuthRepository {
    suspend fun getPlayerId(): String
}
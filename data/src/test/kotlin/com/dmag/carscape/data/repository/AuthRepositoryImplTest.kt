package com.dmag.carscape.data.repository

import com.dmag.carscape.core.common.DispatcherProvider
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()

    private val auth: FirebaseAuth = mock()
    private val dispatchers: DispatcherProvider = mock { on { io } doReturn testDispatcher }

    private val repo = AuthRepositoryImpl(auth, dispatchers)

    // Builds and fully stubs the mock in its own statement, then hands back
    // a finished reference -- never construct a stubbed mock as a direct
    // argument inside another open whenever(...).thenReturn(...) call.
    private fun user(uid: String): FirebaseUser {
        val mockUser: FirebaseUser = mock()
        whenever(mockUser.uid).thenReturn(uid)
        return mockUser
    }

    @Test
    fun `returns the existing user's uid without signing in again`() = runTest(testDispatcher) {
        val existingUser = user("existing-uid")
        whenever(auth.currentUser).thenReturn(existingUser)

        assertEquals("existing-uid", repo.getPlayerId())

        verify(auth, never()).signInAnonymously()
    }

    @Test
    fun `signs in anonymously when there is no current user`() = runTest(testDispatcher) {
        val newUser = user("new-uid")
        val authResult: AuthResult = mock()
        whenever(authResult.user).thenReturn(newUser)

        whenever(auth.currentUser).thenReturn(null)
        whenever(auth.signInAnonymously()).thenReturn(Tasks.forResult(authResult))

        assertEquals("new-uid", repo.getPlayerId())

        verify(auth).signInAnonymously()
    }

    @Test
    fun `throws IllegalStateException when sign-in returns no user`() = runTest(testDispatcher) {
        val authResult: AuthResult = mock() // user defaults to null
        whenever(auth.currentUser).thenReturn(null)
        whenever(auth.signInAnonymously()).thenReturn(Tasks.forResult(authResult))

        val result = runCatching { repo.getPlayerId() }

        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `propagates the failure when anonymous sign-in fails`() = runTest(testDispatcher) {
        whenever(auth.currentUser).thenReturn(null)
        whenever(auth.signInAnonymously())
            .thenReturn(Tasks.forException<AuthResult>(RuntimeException("network down")))

        val result = runCatching { repo.getPlayerId() }

        assertEquals("network down", result.exceptionOrNull()?.message)
    }
}
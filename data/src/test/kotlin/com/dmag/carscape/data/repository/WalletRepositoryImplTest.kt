package com.dmag.carscape.data.repository

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.dmag.carscape.core.common.DispatcherProvider
import com.dmag.carscape.data.fakes.FakeAuthRepository
import com.dmag.carscape.data.fakes.InMemoryDataStore
import com.dmag.carscape.domain.model.PowerUpType
import com.dmag.carscape.domain.repository.WalletRepository.Companion.HEART_REGEN_SECONDS
import com.dmag.carscape.domain.repository.WalletRepository.Companion.MAX_HEARTS
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class WalletRepositoryImplTest {

    // Same key names as WalletRepositoryImpl (its keys are file-private)
    private val heartsKey = intPreferencesKey("wallet_hearts")
    private val lastLostKey = longPreferencesKey("wallet_last_heart_lost_at")

    private val testDispatcher = StandardTestDispatcher()

    private val firestore: FirebaseFirestore = mock()
    private val collection: CollectionReference = mock()
    private val document: DocumentReference = mock()
    private val dispatchers: DispatcherProvider = mock { on { io } doReturn testDispatcher }

    private lateinit var dataStore: InMemoryDataStore

    @Before
    fun setUp() {
        dataStore = InMemoryDataStore()
        whenever(firestore.collection("users")).thenReturn(collection)
        whenever(collection.document(any<String>())).thenReturn(document)
        whenever(document.set(any(), any<SetOptions>())).thenReturn(Tasks.forResult<Void>(null))
    }

    private fun createRepo() =
        WalletRepositoryImpl(dataStore, firestore, FakeAuthRepository(), dispatchers)

    private fun nowSeconds() = System.currentTimeMillis() / 1000

    // ---------- Defaults ----------

    @Test
    fun `wallet emits defaults when store is empty`() = runTest(testDispatcher) {
        val wallet = createRepo().wallet.first()

        assertEquals(0, wallet.coins)
        assertEquals(0, wallet.diamonds)
        assertEquals(MAX_HEARTS, wallet.hearts)
        assertEquals(0, wallet.powerUps.hammer)
        assertEquals(0, wallet.powerUps.freeze)
        assertEquals(0, wallet.powerUps.addTime)
    }

    // ---------- Coins ----------

    @Test
    fun `addCoins accumulates locally and syncs the latest wallet to Firestore`() =
        runTest(testDispatcher) {
            val repo = createRepo()

            repo.addCoins(30)
            repo.addCoins(20)
            advanceUntilIdle()

            assertEquals(50, repo.wallet.first().coins)

            val captor = argumentCaptor<Map<String, Any>>()
            verify(collection, atLeastOnce()).document("test-uid")
            verify(document, atLeastOnce()).set(captor.capture(), any<SetOptions>())
            assertEquals(50, captor.lastValue["coins"])
        }

    @Test
    fun `spendCoins deducts and returns true when balance is enough`() = runTest(testDispatcher) {
        val repo = createRepo()
        repo.addCoins(100)

        assertTrue(repo.spendCoins(40))
        advanceUntilIdle()

        assertEquals(60, repo.wallet.first().coins)
    }

    @Test
    fun `spendCoins returns false and changes nothing when balance is too low`() =
        runTest(testDispatcher) {
            val repo = createRepo()
            repo.addCoins(10)
            advanceUntilIdle()
            clearInvocations(document)

            assertFalse(repo.spendCoins(50))
            advanceUntilIdle()

            assertEquals(10, repo.wallet.first().coins)
            verify(document, never()).set(any(), any<SetOptions>())
        }

    // ---------- Diamonds ----------

    @Test
    fun `diamonds can be added and spent, but not overspent`() = runTest(testDispatcher) {
        val repo = createRepo()

        repo.addDiamonds(5)
        assertTrue(repo.spendDiamonds(3))
        assertFalse(repo.spendDiamonds(3))
        advanceUntilIdle()

        assertEquals(2, repo.wallet.first().diamonds)
    }

    // ---------- Hearts ----------

    @Test
    fun `loseHeart decrements from full`() = runTest(testDispatcher) {
        val repo = createRepo()

        repo.loseHeart()
        advanceUntilIdle()

        assertEquals(MAX_HEARTS - 1, repo.wallet.first().hearts)
    }

    @Test
    fun `loseHeart never goes below zero`() = runTest(testDispatcher) {
        val repo = createRepo()

        repeat(MAX_HEARTS + 2) { repo.loseHeart() }
        advanceUntilIdle()

        assertEquals(0, repo.wallet.first().hearts)
    }

    @Test
    fun `loseHeart from full starts the regen timer`() = runTest(testDispatcher) {
        val repo = createRepo()
        advanceUntilIdle() // let init's refreshHeartRegen finish first
        val before = nowSeconds()

        repo.loseHeart()

        val stamp = dataStore.data.first()[lastLostKey]
        assertNotNull(stamp)
        assertTrue(stamp!! >= before)
    }

    @Test
    fun `addHeart increments up to the max`() = runTest(testDispatcher) {
        val repo = createRepo()
        repo.loseHeart()
        repo.loseHeart()

        repo.addHeart()
        advanceUntilIdle()
        assertEquals(MAX_HEARTS - 1, repo.wallet.first().hearts)

        repo.addHeart()
        repo.addHeart() // already full, must stay capped
        advanceUntilIdle()
        assertEquals(MAX_HEARTS, repo.wallet.first().hearts)
    }

    // ---------- Heart regen ----------

    @Test
    fun `regen adds elapsed hearts and keeps partial progress toward the next one`() =
        runTest(testDispatcher) {
            val lostAt = nowSeconds() - (2 * HEART_REGEN_SECONDS + 10)
            dataStore.edit {
                it[heartsKey] = MAX_HEARTS - 3
                it[lastLostKey] = lostAt
            }

            val repo = createRepo()
            advanceUntilIdle() // init runs refreshHeartRegen

            assertEquals(MAX_HEARTS - 1, repo.wallet.first().hearts)
            // Timestamp advances by exactly 2 intervals, so the extra 10s isn't lost
            assertEquals(lostAt + 2 * HEART_REGEN_SECONDS, dataStore.data.first()[lastLostKey])
        }

    @Test
    fun `regen caps at max hearts and clears the timestamp`() = runTest(testDispatcher) {
        dataStore.edit {
            it[heartsKey] = MAX_HEARTS - 1
            it[lastLostKey] = nowSeconds() - 10 * HEART_REGEN_SECONDS
        }

        val repo = createRepo()
        advanceUntilIdle()

        assertEquals(MAX_HEARTS, repo.wallet.first().hearts)
        assertNull(dataStore.data.first()[lastLostKey])
    }

    @Test
    fun `regen does nothing before a full interval has passed`() = runTest(testDispatcher) {
        val lostAt = nowSeconds() - 5
        dataStore.edit {
            it[heartsKey] = MAX_HEARTS - 1
            it[lastLostKey] = lostAt
        }

        val repo = createRepo()
        advanceUntilIdle()

        assertEquals(MAX_HEARTS - 1, repo.wallet.first().hearts)
        assertEquals(lostAt, dataStore.data.first()[lastLostKey])
    }

    @Test
    fun `regen starts the timer when below max with no timestamp`() = runTest(testDispatcher) {
        dataStore.edit { it[heartsKey] = MAX_HEARTS - 1 }

        val repo = createRepo()
        advanceUntilIdle()

        assertEquals(MAX_HEARTS - 1, repo.wallet.first().hearts)
        assertNotNull(dataStore.data.first()[lastLostKey])
    }

    // ---------- Power-ups ----------

    @Test
    fun `addPowerUp increases the matching counter and syncs the nested map`() =
        runTest(testDispatcher) {
            val repo = createRepo()

            repo.addPowerUp(PowerUpType.HAMMER, 2)
            repo.addPowerUp(PowerUpType.FREEZE, 1)
            advanceUntilIdle()

            val powerUps = repo.wallet.first().powerUps
            assertEquals(2, powerUps.hammer)
            assertEquals(1, powerUps.freeze)
            assertEquals(0, powerUps.addTime)

            val captor = argumentCaptor<Map<String, Any>>()
            verify(document, atLeastOnce()).set(captor.capture(), any<SetOptions>())
            val synced = captor.lastValue["powerUps"] as Map<*, *>
            assertEquals(2, synced["hammer"])
            assertEquals(1, synced["freeze"])
            assertEquals(0, synced["addTime"])
        }

    @Test
    fun `consumePowerUp uses one and returns false when none are left`() = runTest(testDispatcher) {
        val repo = createRepo()
        repo.addPowerUp(PowerUpType.ADD_TIME, 1)

        assertTrue(repo.consumePowerUp(PowerUpType.ADD_TIME))
        assertFalse(repo.consumePowerUp(PowerUpType.ADD_TIME))
        advanceUntilIdle()

        assertEquals(0, repo.wallet.first().powerUps.addTime)
    }

    // ---------- Firestore failures are non-fatal ----------

    @Test
    fun `local wallet stays correct when Firestore throws`() = runTest(testDispatcher) {
        doThrow(IllegalStateException("boom")).whenever(firestore).collection(any<String>())
        val repo = createRepo()

        repo.addCoins(25)
        advanceUntilIdle()

        assertEquals(25, repo.wallet.first().coins)
    }

    @Test
    fun `local wallet stays correct when the Firestore write task fails`() =
        runTest(testDispatcher) {
            whenever(document.set(any(), any<SetOptions>()))
                .thenReturn(Tasks.forException<Void>(RuntimeException("network down")))
            val repo = createRepo()

            repo.addCoins(25)
            advanceUntilIdle()

            assertEquals(25, repo.wallet.first().coins)
        }
}
package com.dmag.carscape.data.repository

import com.dmag.carscape.core.common.DispatcherProvider
import com.dmag.carscape.data.fakes.FakeAuthRepository
import com.dmag.carscape.data.fakes.InMemoryDataStore
import com.dmag.carscape.domain.model.GameMode
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ProgressRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()

    private val firestore: FirebaseFirestore = mock()
    private val collection: CollectionReference = mock()
    private val document: DocumentReference = mock()
    private val dispatchers: DispatcherProvider = mock { on { io } doReturn testDispatcher }

    private lateinit var dataStore: InMemoryDataStore

    // Builds and fully stubs a DocumentSnapshot in its own statement -- never
    // construct a stubbed mock as a direct argument inside another open
    // whenever(...).thenReturn(...) call (see AuthRepositoryImplTest notes).
    private fun snapshot(exists: Boolean, fields: Map<String, Long> = emptyMap()): DocumentSnapshot {
        val snap: DocumentSnapshot = mock()
        whenever(snap.exists()).thenReturn(exists)
        fields.forEach { (field, value) -> whenever(snap.getLong(field)).thenReturn(value) }
        return snap
    }

    // Stubs document.get() from an already-built snapshot, never from one
    // constructed inline -- same reason as the comment on snapshot() above.
    private fun stubRemoteDoc(snap: DocumentSnapshot) {
        val task = Tasks.forResult(snap)
        whenever(document.get()).thenReturn(task)
    }

    @Before
    fun setUp() {
        dataStore = InMemoryDataStore()
        whenever(firestore.collection("users")).thenReturn(collection)
        whenever(collection.document(any<String>())).thenReturn(document)
        whenever(document.set(any(), any<SetOptions>())).thenReturn(Tasks.forResult<Void>(null))
        val defaultSnapshot = snapshot(exists = false)
        stubRemoteDoc(defaultSnapshot)
    }

    private fun createRepo() =
        ProgressRepositoryImpl(dataStore, firestore, FakeAuthRepository(), dispatchers)

    // ---------- Local unlocked-level storage ----------

    @Test
    fun `getUnlockedLevel defaults to 1 for every mode`() = runTest(testDispatcher) {
        val repo = createRepo()

        GameMode.values().forEach { mode ->
            assertEquals(1, repo.getUnlockedLevel(mode))
        }
    }

    @Test
    fun `setUnlockedLevel updates only the given mode`() = runTest(testDispatcher) {
        val repo = createRepo()

        repo.setUnlockedLevel(GameMode.TIMED, 7)
        advanceUntilIdle()

        assertEquals(7, repo.getUnlockedLevel(GameMode.TIMED))
        assertEquals(1, repo.getUnlockedLevel(GameMode.DAILY))
        assertEquals(1, repo.getUnlockedLevel(GameMode.CASUAL))
    }

    @Test
    fun `setUnlockedLevel syncs all modes and uses the capitalized field names`() =
        runTest(testDispatcher) {
            val repo = createRepo()

            repo.setUnlockedLevel(GameMode.CASUAL, 3)
            advanceUntilIdle()

            val captor = argumentCaptor<Map<String, Any>>()
            verify(document, atLeastOnce()).set(captor.capture(), any<SetOptions>())
            val synced = captor.lastValue

            assertEquals(3, synced["unlockedLevelCasual"])
            assertEquals(1, synced["unlockedLevelDaily"])
            assertEquals(1, synced["unlockedLevelTimed"])
        }

    // ---------- Local daily-completion storage ----------

    @Test
    fun `getLastDailyCompletionEpochDay defaults to null`() = runTest(testDispatcher) {
        assertNull(createRepo().getLastDailyCompletionEpochDay())
    }

    @Test
    fun `setLastDailyCompletionEpochDay stores and syncs the value`() = runTest(testDispatcher) {
        val repo = createRepo()

        repo.setLastDailyCompletionEpochDay(19_900L)
        advanceUntilIdle()

        assertEquals(19_900L, repo.getLastDailyCompletionEpochDay())

        val captor = argumentCaptor<Map<String, Any>>()
        verify(document, atLeastOnce()).set(captor.capture(), any<SetOptions>())
        assertEquals(19_900L, captor.lastValue["lastDailyCompletionEpochDay"])
    }

    @Test
    fun `sync omits lastDailyCompletionEpochDay when it was never set`() = runTest(testDispatcher) {
        val repo = createRepo()

        repo.setUnlockedLevel(GameMode.DAILY, 2)
        advanceUntilIdle()

        val captor = argumentCaptor<Map<String, Any>>()
        verify(document, atLeastOnce()).set(captor.capture(), any<SetOptions>())
        assertEquals(false, captor.lastValue.containsKey("lastDailyCompletionEpochDay"))
    }

    // ---------- reconcileWithRemote ----------

    @Test
    fun `reconcile does nothing when the remote document does not exist`() = runTest(testDispatcher) {
        val missingDoc = snapshot(exists = false)
        stubRemoteDoc(missingDoc)
        val repo = createRepo()

        repo.reconcileWithRemote()

        GameMode.values().forEach { mode ->
            assertEquals(1, repo.getUnlockedLevel(mode))
        }
        assertNull(repo.getLastDailyCompletionEpochDay())
    }

    @Test
    fun `reconcile pulls remote levels that are ahead of local`() = runTest(testDispatcher) {
        val remoteDoc = snapshot(
            exists = true,
            fields = mapOf(
                "unlockedLevelDaily" to 5L,
                "unlockedLevelTimed" to 3L
                // unlockedLevelCasual intentionally missing -> defaults to 1 remotely
            )
        )
        stubRemoteDoc(remoteDoc)
        val repo = createRepo()

        repo.reconcileWithRemote()

        assertEquals(5, repo.getUnlockedLevel(GameMode.DAILY))
        assertEquals(3, repo.getUnlockedLevel(GameMode.TIMED))
        assertEquals(1, repo.getUnlockedLevel(GameMode.CASUAL))
    }

    @Test
    fun `reconcile never regresses a local level that is already ahead of remote`() =
        runTest(testDispatcher) {
            val remoteDoc = snapshot(exists = true, fields = mapOf("unlockedLevelDaily" to 2L))
            stubRemoteDoc(remoteDoc)
            val repo = createRepo()
            repo.setUnlockedLevel(GameMode.DAILY, 9)
            advanceUntilIdle()

            repo.reconcileWithRemote()

            assertEquals(9, repo.getUnlockedLevel(GameMode.DAILY))
        }

    @Test
    fun `reconcile adopts remote daily completion when local has none`() = runTest(testDispatcher) {
        val remoteDoc = snapshot(exists = true, fields = mapOf("lastDailyCompletionEpochDay" to 20_000L))
        stubRemoteDoc(remoteDoc)
        val repo = createRepo()

        repo.reconcileWithRemote()

        assertEquals(20_000L, repo.getLastDailyCompletionEpochDay())
    }

    @Test
    fun `reconcile never regresses a local daily completion that is already ahead`() =
        runTest(testDispatcher) {
            val remoteDoc = snapshot(exists = true, fields = mapOf("lastDailyCompletionEpochDay" to 10_000L))
            stubRemoteDoc(remoteDoc)
            val repo = createRepo()
            repo.setLastDailyCompletionEpochDay(15_000L)
            advanceUntilIdle()

            repo.reconcileWithRemote()

            assertEquals(15_000L, repo.getLastDailyCompletionEpochDay())
        }

    @Test
    fun `reconcile is non-fatal when Firestore throws`() = runTest(testDispatcher) {
        doThrow(IllegalStateException("boom")).whenever(firestore).collection(any<String>())
        val repo = createRepo()

        repo.reconcileWithRemote()

        assertEquals(1, repo.getUnlockedLevel(GameMode.DAILY))
    }

    // ---------- Sync failures are non-fatal ----------

    @Test
    fun `local progress stays correct when the Firestore write fails`() = runTest(testDispatcher) {
        whenever(document.set(any(), any<SetOptions>()))
            .thenReturn(Tasks.forException<Void>(RuntimeException("network down")))
        val repo = createRepo()

        repo.setUnlockedLevel(GameMode.TIMED, 4)
        advanceUntilIdle()

        assertEquals(4, repo.getUnlockedLevel(GameMode.TIMED))
    }
}
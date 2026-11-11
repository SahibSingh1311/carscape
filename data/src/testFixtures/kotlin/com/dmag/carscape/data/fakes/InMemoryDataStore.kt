package com.dmag.carscape.data.fakes

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class InMemoryDataStore : DataStore<Preferences> {
    private val _data = MutableStateFlow<Preferences>(mutablePreferencesOf())
    override val data: StateFlow<Preferences> get() = _data

    override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences {
        val updated = transform(_data.value)
        _data.value = updated
        return updated
    }
}
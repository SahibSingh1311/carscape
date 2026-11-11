package com.dmag.carscape.core.common

import kotlinx.coroutines.Dispatchers

class DefaultDispatcherProvider : DispatcherProvider {
    override val main: kotlinx.coroutines.CoroutineDispatcher get() = Dispatchers.Main
    override val io: kotlinx.coroutines.CoroutineDispatcher get() = Dispatchers.IO
    override val default: kotlinx.coroutines.CoroutineDispatcher get() = Dispatchers.Default
}

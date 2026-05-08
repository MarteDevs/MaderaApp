package com.mars.madereraapp.di

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.runBlocking

/**
 * Singleton event bus to signal a 401 Unauthorized response globally.
 * The MainActivity observes this and redirects to the Login screen.
 */
object UnauthorizedEventBus {
    private val _events = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    fun emit() {
        runBlocking { _events.emit(Unit) }
    }
}

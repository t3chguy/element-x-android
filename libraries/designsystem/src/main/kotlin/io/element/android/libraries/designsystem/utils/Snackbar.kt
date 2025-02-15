/*
 * Copyright (c) 2023 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.element.android.libraries.designsystem.utils

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.element.android.libraries.designsystem.components.button.ButtonVisuals
import io.element.android.libraries.designsystem.theme.components.IconSource
import io.element.android.libraries.designsystem.theme.components.Snackbar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A global dispatcher of [SnackbarMessage] to be displayed in [Snackbar] via a [SnackbarHostState].
 */
class SnackbarDispatcher {
    private val mutex = Mutex()

    private val _snackbarMessage = MutableStateFlow<SnackbarMessage?>(null)
    val snackbarMessage: Flow<SnackbarMessage?> = _snackbarMessage.asStateFlow()

    suspend fun post(message: SnackbarMessage) {
        mutex.withLock {
            _snackbarMessage.update { message }
        }
    }

    suspend fun clear() {
        mutex.withLock {
            _snackbarMessage.update { null }
        }
    }
}

/** Used to provide a [SnackbarDispatcher] to composable functions, it's needed for [rememberSnackbarHostState]. */
val LocalSnackbarDispatcher = compositionLocalOf<SnackbarDispatcher> { SnackbarDispatcher() }

@Composable
fun SnackbarDispatcher.collectSnackbarMessageAsState(): State<SnackbarMessage?> {
    return snackbarMessage.collectAsState(initial = null)
}

@Composable
fun SnackbarHost(hostState: SnackbarHostState, modifier: Modifier = Modifier) {
    androidx.compose.material3.SnackbarHost(hostState, modifier) { data ->
        Snackbar(
            modifier = Modifier.padding(12.dp), // Add default padding
            message = data.visuals.message,
            action = data.visuals.actionLabel?.let { ButtonVisuals.Text(it, data::performAction) },
            dismissAction = if (data.visuals.withDismissAction) {
                ButtonVisuals.Icon(IconSource.Vector(Icons.Default.Close), data::dismiss)
            } else null,
        )
    }
}

@Composable
fun rememberSnackbarHostState(snackbarMessage: SnackbarMessage?): SnackbarHostState {
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessageText = snackbarMessage?.let {
        stringResource(id = snackbarMessage.messageResId)
    }
    val dispatcher = LocalSnackbarDispatcher.current
    LaunchedEffect(snackbarMessage) {
        if (snackbarMessageText == null) return@LaunchedEffect
        launch {
            snackbarHostState.showSnackbar(
                message = snackbarMessageText,
                duration = snackbarMessage.duration,
            )
            if (isActive) {
                dispatcher.clear()
            }
        }
    }
    return snackbarHostState
}

data class SnackbarMessage(
    @StringRes val messageResId: Int,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    @StringRes val actionResId: Int? = null,
    val action: () -> Unit = {},
)

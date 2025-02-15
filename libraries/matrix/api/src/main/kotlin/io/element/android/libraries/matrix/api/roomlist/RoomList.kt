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

package io.element.android.libraries.matrix.api.roomlist

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import kotlin.time.Duration

/**
 * Holds some flows related to a specific set of rooms.
 * Can be retrieved from [RoomListService] methods.
 */
interface RoomList {
    sealed class LoadingState {
        object NotLoaded : LoadingState()
        data class Loaded(val numberOfRooms: Int) : LoadingState()
    }

    /**
     * The list of room summaries as a flow.
     */
    val summaries: StateFlow<List<RoomSummary>>

    /**
     * The loading state of the room list as a flow.
     * This is useful to know if a specific set of rooms is loaded or not.
     */
    val loadingState: StateFlow<LoadingState>
}

suspend fun RoomList.awaitLoaded(timeout: Duration = Duration.INFINITE) {
    try {
        Timber.d("awaitAllRoomsAreLoaded: wait")
        withTimeout(timeout) {
            loadingState.firstOrNull {
                it is RoomList.LoadingState.Loaded
            }
        }
    } catch (timeoutException: TimeoutCancellationException) {
        Timber.d("awaitAllRoomsAreLoaded: no response after $timeout")
    }
}

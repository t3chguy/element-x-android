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

package io.element.android.libraries.androidutils.file

import android.content.Context
import io.element.android.libraries.core.data.tryOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.util.UUID

fun File.safeDelete() {
    tryOrNull(
        onError = {
            Timber.e(it, "Error, unable to delete file $path")
        },
        operation = {
            if (delete().not()) {
                Timber.w("Warning, unable to delete file $path")
            }
        }
    )
}

suspend fun Context.createTmpFile(baseDir: File = cacheDir): File = withContext(Dispatchers.IO) {
    File.createTempFile(UUID.randomUUID().toString(), null, baseDir).apply { mkdirs() }
}

/*
 * Copyright (c) 2022 New Vector Ltd
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

package io.element.android.features.messages.impl.timeline.model.event

import io.element.android.features.poll.api.PollAnswerItem
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.poll.PollKind

data class TimelineItemPollContent(
    val question: String,
    val answerItems: List<PollAnswerItem>,
    val votes: Map<String, List<UserId>>,
    val pollKind: PollKind,
    val isDisclosed: Boolean,
) : TimelineItemEventContent {
    override val type: String = "TimelineItemPollContent"
}

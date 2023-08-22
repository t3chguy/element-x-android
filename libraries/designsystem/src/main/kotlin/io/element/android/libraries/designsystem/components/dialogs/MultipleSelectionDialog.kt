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

package io.element.android.libraries.designsystem.components.dialogs

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.airbnb.android.showkase.annotation.ShowkaseComposable
import io.element.android.libraries.designsystem.components.list.CheckboxListItem
import io.element.android.libraries.designsystem.preview.DayNightPreviews
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewGroup
import io.element.android.libraries.designsystem.theme.components.DialogPreview
import io.element.android.libraries.designsystem.theme.components.SimpleAlertDialogContent
import io.element.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultipleSelectionDialog(
    options: ImmutableList<String>,
    onConfirmClicked: (List<Int>) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    confirmButtonTitle: String = stringResource(CommonStrings.action_confirm),
    dismissButtonTitle: String = stringResource(CommonStrings.action_cancel),
    title: String? = null,
    initialSelection: ImmutableList<Int> = persistentListOf(),
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
    ) {
        MultipleSelectionDialogContent(
            title = title,
            options = options,
            confirmButtonTitle = confirmButtonTitle,
            onConfirmClicked = onConfirmClicked,
            dismissButtonTitle = dismissButtonTitle,
            onDismissRequest = onDismissRequest,
            initialSelected = initialSelection,
        )
    }
}

@Composable
internal fun MultipleSelectionDialogContent(
    options: ImmutableList<String>,
    confirmButtonTitle: String,
    onConfirmClicked: (List<Int>) -> Unit,
    dismissButtonTitle: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    initialSelected: ImmutableList<Int> = persistentListOf(),
) {
    val selectedOptionIndexes = remember { initialSelected.toMutableStateList() }

    fun isSelected(index: Int) = selectedOptionIndexes.any { it == index }

    SimpleAlertDialogContent(
        title = title,
        modifier = modifier,
        submitText = confirmButtonTitle,
        onSubmitClicked = {
            onConfirmClicked(selectedOptionIndexes.toList())
        },
        cancelText = dismissButtonTitle,
        onCancelClicked = onDismissRequest,
        applyPaddingToContents = false,
    ) {
        LazyColumn {
            itemsIndexed(options) { index, option ->
                CheckboxListItem(
                    headline = option,
                    checked = isSelected(index),
                    onChange = {
                        if (isSelected(index)) {
                            selectedOptionIndexes.remove(index)
                        } else {
                            selectedOptionIndexes.add(index)
                        }
                    },
                    compactLayout = true,
                )
            }
        }
    }
}

@DayNightPreviews
@ShowkaseComposable(group = PreviewGroup.Dialogs)
@Composable
internal fun MultipleSelectionDialogContentPreview() {
    ElementPreview(showBackground = false) {
        DialogPreview {
            val options = persistentListOf("Option 1", "Option 2", "Option 3")
            MultipleSelectionDialogContent(
                title = "Dialog title",
                options = options,
                onConfirmClicked = {},
                onDismissRequest = {},
                confirmButtonTitle = "Save",
                dismissButtonTitle = "Cancel",
                initialSelected = persistentListOf(0),
            )
        }
    }
}

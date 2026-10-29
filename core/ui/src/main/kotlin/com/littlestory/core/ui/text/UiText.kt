package com.littlestory.core.ui.text

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource

/**
 * Text a ViewModel can emit without holding a translated string (ADR-009). It is resolved in
 * the UI layer with the current locale, so a language switch re-renders it correctly.
 */
@Immutable
sealed interface UiText {
    data class Res(@StringRes val id: Int, val args: List<Any> = emptyList()) : UiText

    data class Plural(@PluralsRes val id: Int, val count: Int, val args: List<Any> = emptyList()) : UiText

    /** User-entered or content text only (child name, story title); never UI copy. */
    data class Dynamic(val value: String) : UiText
}

@Composable
fun UiText.asString(): String = when (this) {
    is UiText.Res -> stringResource(id, *args.toTypedArray())
    is UiText.Plural -> pluralStringResource(id, count, *(listOf<Any>(count) + args).toTypedArray())
    is UiText.Dynamic -> value
}

fun UiText.asString(context: Context): String = when (this) {
    is UiText.Res -> context.getString(id, *args.toTypedArray())
    is UiText.Plural -> context.resources.getQuantityString(id, count, *(listOf<Any>(count) + args).toTypedArray())
    is UiText.Dynamic -> value
}

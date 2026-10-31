package com.littlestory.core.ui.safety

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.window.DialogProperties
import com.littlestory.core.common.safety.ParentalGateChallenge
import com.littlestory.core.common.safety.ParentalGateChallenges
import com.littlestory.core.common.safety.RandomParentalGateChallenges
import com.littlestory.core.designsystem.component.ButtonVariant
import com.littlestory.core.designsystem.component.LittleStoryButton
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.designsystem.theme.tokenShadow
import com.littlestory.core.ui.R
import java.text.NumberFormat

object ParentalGateTags {
    const val DIALOG = "parental_gate"
    const val QUESTION = "parental_gate_question"
    const val ANSWER = "parental_gate_answer"
    const val CLEAR = "parental_gate_clear"
    const val SUBMIT = "parental_gate_submit"
    const val CANCEL = "parental_gate_cancel"

    fun key(digit: Int) = "parental_gate_key_$digit"
}

/** Where gate questions come from; `:app` provides its injected source, tests a fixed one. */
val LocalParentalGateChallenges = staticCompositionLocalOf<ParentalGateChallenges> { RandomParentalGateChallenges() }

private val challengeSaver =
    listSaver<ParentalGateChallenge, Int>(
        save = { listOf(it.first, it.second) },
        restore = { ParentalGateChallenge(it[0], it[1]) },
    )

/** Keypad rows; the last row is Clear, 0 and an empty slot, like a phone keypad. */
private val keypadRows = listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9))
private const val LAST_ROW_DIGIT = 0

/**
 * Grown-ups-only check (Google Play Families policy, T-08): a multiplication the parent answers on
 * an in-dialog keypad before a parent-only screen or action. A keypad (not the system keyboard)
 * keeps the dialog steady and works the same in every language. A wrong answer swaps in a new
 * question; back, a scrim tap and Cancel call [onDismiss]. Survives rotation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentalGateDialog(onPassed: () -> Unit, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val challenges = LocalParentalGateChallenges.current
    var challenge by rememberSaveable(stateSaver = challengeSaver) { mutableStateOf(challenges.next()) }
    var input by rememberSaveable { mutableStateOf("") }
    var wrong by rememberSaveable { mutableStateOf(false) }
    val maxDigits = challenge.answer.toString().length + 1
    val onDigit = { digit: Int ->
        if (input.length < maxDigits) input += digit.toString()
        wrong = false
    }
    val submit = {
        if (challenge.isAnsweredBy(input)) {
            onPassed()
        } else {
            wrong = true
            input = ""
            challenge = challenges.next()
        }
    }
    val colors = LittleStoryTheme.colors
    val spacing = LittleStoryTheme.spacing
    val numbers = NumberFormat.getIntegerInstance(LocalLocale.current.platformLocale)
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag(ParentalGateTags.DIALOG),
        properties = DialogProperties(),
    ) {
        Column(
            Modifier
                .tokenShadow(LittleStoryTheme.elevation.card, LittleStoryTheme.shapes.large)
                .background(colors.surface, LittleStoryTheme.shapes.large)
                .verticalScroll(rememberScrollState())
                .padding(spacing.xxl),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Text(
                stringResource(R.string.parental_gate_title),
                style = LittleStoryTheme.typography.topBarTitle,
                color = colors.textPrimary,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                stringResource(R.string.parental_gate_body),
                style = LittleStoryTheme.typography.bodyLarge,
                color = colors.textSecondary,
            )
            Text(
                stringResource(
                    R.string.parental_gate_question,
                    numbers.format(challenge.first),
                    numbers.format(challenge.second),
                ),
                style = LittleStoryTheme.typography.topBarTitle,
                color = colors.textPrimary,
                modifier = Modifier.testTag(ParentalGateTags.QUESTION),
            )
            AnswerBox(
                text = input.map { numbers.format(it.digitToInt()) }.joinToString(""),
                error = if (wrong) stringResource(R.string.parental_gate_wrong) else null,
            )
            keypadRows.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    row.forEach { digit -> DigitKey(digit, numbers, onDigit, Modifier.weight(1f)) }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                LittleStoryButton(
                    text = stringResource(R.string.parental_gate_clear),
                    onClick = { input = "" },
                    variant = ButtonVariant.Ghost,
                    modifier = Modifier.weight(1f).testTag(ParentalGateTags.CLEAR),
                )
                DigitKey(LAST_ROW_DIGIT, numbers, onDigit, Modifier.weight(1f))
                Spacer(Modifier.weight(1f))
            }
            LittleStoryButton(
                text = stringResource(R.string.action_continue),
                onClick = submit,
                enabled = input.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().testTag(ParentalGateTags.SUBMIT),
            )
            LittleStoryButton(
                text = stringResource(R.string.parental_gate_cancel),
                onClick = onDismiss,
                variant = ButtonVariant.Ghost,
                modifier = Modifier.fillMaxWidth().testTag(ParentalGateTags.CANCEL),
            )
        }
    }
}

@Composable
private fun DigitKey(digit: Int, numbers: NumberFormat, onDigit: (Int) -> Unit, modifier: Modifier = Modifier) {
    LittleStoryButton(
        text = numbers.format(digit),
        onClick = { onDigit(digit) },
        variant = ButtonVariant.Secondary,
        modifier = modifier.testTag(ParentalGateTags.key(digit)),
    )
}

/** The typed answer, announced politely so TalkBack users hear each digit and any error. */
@Composable
private fun AnswerBox(text: String, error: String?) {
    val colors = LittleStoryTheme.colors
    val dimens = LittleStoryTheme.dimens
    val label = stringResource(R.string.parental_gate_answer)
    Column(verticalArrangement = Arrangement.spacedBy(LittleStoryTheme.spacing.xs)) {
        Box(
            Modifier
                .fillMaxWidth()
                .heightIn(min = dimens.inputHeight)
                .border(
                    BorderStroke(dimens.borderWidth, if (error != null) colors.error else colors.outline),
                    LittleStoryTheme.shapes.small,
                ).padding(horizontal = LittleStoryTheme.spacing.md)
                .semantics {
                    contentDescription = label
                    liveRegion = LiveRegionMode.Polite
                }.testTag(ParentalGateTags.ANSWER),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(text, style = LittleStoryTheme.typography.topBarTitle, color = colors.textPrimary)
        }
        if (error != null) {
            Text(error, style = LittleStoryTheme.typography.chip, color = colors.error)
        }
    }
}

/**
 * Shows [content] only after a grown-up passes the [ParentalGateDialog]; until then the screen is
 * blank behind the dialog. Cancelling calls [onCancel], usually "go back". The unlock lasts while
 * this destination stays on the back stack, so returning from a sub-screen does not ask again.
 */
@Composable
fun ParentGated(onCancel: () -> Unit, content: @Composable () -> Unit) {
    var unlocked by rememberSaveable { mutableStateOf(false) }
    if (unlocked) {
        content()
    } else {
        Box(Modifier.fillMaxSize().background(LittleStoryTheme.colors.background))
        ParentalGateDialog(onPassed = { unlocked = true }, onDismiss = onCancel)
    }
}

package com.littlestory.core.common.safety

import com.littlestory.core.common.config.AppConfig
import kotlin.random.Random

/**
 * One grown-up check for the parental gate (Google Play Families policy, T-08): a multiplication
 * that a young child is unlikely to solve, asked before parent-only screens and actions.
 */
data class ParentalGateChallenge(val first: Int, val second: Int) {
    val answer: Int get() = first * second

    /**
     * True when [input] is the answer. Any decimal digits count, so Kannada digits (೦–೯) typed on a
     * Kannada keyboard work too; spaces are ignored and anything else is a wrong answer.
     */
    fun isAnsweredBy(input: String): Boolean {
        val values = input.filterNot(Char::isWhitespace).map { Character.digit(it, DECIMAL) }
        val valid = values.isNotEmpty() && values.size <= MAX_DIGITS && values.all { it >= 0 }
        return valid && values.fold(0) { total, digit -> total * DECIMAL + digit } == answer
    }

    private companion object {
        const val DECIMAL = 10
        const val MAX_DIGITS = 6
    }
}

/** Source of gate questions; the app uses [RandomParentalGateChallenges], tests a fixed one. */
fun interface ParentalGateChallenges {
    fun next(): ParentalGateChallenge
}

class RandomParentalGateChallenges(
    private val random: Random = Random.Default,
) : ParentalGateChallenges {
    override fun next(): ParentalGateChallenge =
        ParentalGateChallenge(
            first = AppConfig.ParentalGate.firstFactorRange.random(random),
            second = AppConfig.ParentalGate.secondFactorRange.random(random),
        )
}

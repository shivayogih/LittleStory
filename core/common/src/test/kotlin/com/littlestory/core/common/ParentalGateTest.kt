package com.littlestory.core.common

import com.littlestory.core.common.config.AppConfig
import com.littlestory.core.common.safety.ParentalGateChallenge
import com.littlestory.core.common.safety.RandomParentalGateChallenges
import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ParentalGateTest {
    private val challenge = ParentalGateChallenge(14, 7)

    @Test
    fun acceptsTheProductInLatinOrKannadaDigits() {
        assertTrue(challenge.isAnsweredBy("98"))
        assertTrue(challenge.isAnsweredBy(" 9 8 "))
        assertTrue(challenge.isAnsweredBy("೯೮"))
    }

    @Test
    fun rejectsWrongEmptyOrNonNumericAnswers() {
        assertFalse(challenge.isAnsweredBy("97"))
        assertFalse(challenge.isAnsweredBy(""))
        assertFalse(challenge.isAnsweredBy("9a8"))
        assertFalse(challenge.isAnsweredBy("-98"))
        assertFalse(challenge.isAnsweredBy("0000098"))
    }

    @Test
    fun randomChallengesStayInsideTheConfiguredRanges() {
        val source = RandomParentalGateChallenges(Random(seed = 7))
        repeat(200) {
            val next = source.next()
            assertTrue(next.first in AppConfig.ParentalGate.firstFactorRange)
            assertTrue(next.second in AppConfig.ParentalGate.secondFactorRange)
        }
    }
}

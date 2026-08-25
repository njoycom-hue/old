package com.dunoetoktok.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class MathQuestionGeneratorTest {

    @Test
    fun `choices contain the correct answer exactly once`() {
        repeat(200) { seed ->
            val question = MathQuestionGenerator.generate(Random(seed.toLong()))
            assertEquals(1, question.choices.count { it == question.answer })
        }
    }

    @Test
    fun `always exactly four unique non-negative choices`() {
        repeat(200) { seed ->
            val question = MathQuestionGenerator.generate(Random(seed.toLong()))
            assertEquals(4, question.choices.size)
            assertEquals(4, question.choices.toSet().size)
            assertTrue(question.choices.all { it >= 0 })
        }
    }

    @Test
    fun `subtraction problems never yield a negative answer`() {
        repeat(200) { seed ->
            val question = MathQuestionGenerator.generate(Random(seed.toLong()))
            if (question.text.contains("-")) {
                assertTrue(
                    "answer should be non-negative for: ${question.text}",
                    question.answer >= 0,
                )
            }
        }
    }
}

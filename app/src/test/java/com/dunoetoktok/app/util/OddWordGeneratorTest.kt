package com.dunoetoktok.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class OddWordGeneratorTest {

    @Test
    fun `produces four unique choices including the answer`() {
        repeat(200) { seed ->
            val question = OddWordGenerator.generate(Random(seed.toLong()))
            assertEquals(4, question.choices.size)
            assertEquals(4, question.choices.toSet().size)
            assertTrue(question.answer in question.choices)
        }
    }

    @Test
    fun `three choices share a category and the answer is from a different one`() {
        repeat(200) { seed ->
            val question = OddWordGenerator.generate(Random(seed.toLong()))
            val otherThree = question.choices - question.answer
            assertEquals(3, otherThree.size)

            val sharedCategory = WORD_CATEGORIES.firstOrNull { category ->
                otherThree.all { it in category.words }
            }
            assertNotNull("no single category contains $otherThree", sharedCategory)
            assertTrue(question.answer !in sharedCategory!!.words)
        }
    }
}

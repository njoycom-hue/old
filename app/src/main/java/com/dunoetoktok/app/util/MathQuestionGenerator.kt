package com.dunoetoktok.app.util

import kotlin.random.Random

data class MathQuestion(val text: String, val answer: Int, val choices: List<Int>)

/** Generates single-digit-friendly addition/subtraction problems with 4 multiple-choice answers. */
object MathQuestionGenerator {

    fun generate(random: Random = Random.Default): MathQuestion {
        var a = random.nextInt(1, 21)
        var b = random.nextInt(1, 21)
        val useSubtraction = random.nextBoolean()

        val answer: Int
        val text: String
        if (useSubtraction) {
            if (b > a) {
                val tmp = a; a = b; b = tmp
            }
            answer = a - b
            text = "$a - $b = ?"
        } else {
            answer = a + b
            text = "$a + $b = ?"
        }

        return MathQuestion(text = text, answer = answer, choices = generateChoices(answer, random))
    }

    private fun generateChoices(answer: Int, random: Random): List<Int> {
        val choices = linkedSetOf(answer)
        var attempts = 0
        while (choices.size < 4 && attempts < 100) {
            attempts++
            val candidate = answer + random.nextInt(-4, 5)
            if (candidate >= 0) choices.add(candidate)
        }
        var fallback = 1
        while (choices.size < 4) {
            choices.add(answer + fallback)
            fallback++
        }
        return choices.shuffled(random)
    }
}

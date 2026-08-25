package com.dunoetoktok.app.util

import kotlin.random.Random

data class OddWordQuestion(val choices: List<String>, val answer: String)

/**
 * Picks 3 words from one random category and 1 word from a different category;
 * the odd word out (from the different category) is the answer.
 */
object OddWordGenerator {

    fun generate(
        random: Random = Random.Default,
        categories: List<WordCategory> = WORD_CATEGORIES,
    ): OddWordQuestion {
        require(categories.size >= 2) { "Need at least two categories to pick an odd word out" }

        val mainIndex = random.nextInt(categories.size)
        var oddIndex: Int
        do {
            oddIndex = random.nextInt(categories.size)
        } while (oddIndex == mainIndex)

        val mainWords = categories[mainIndex].words.shuffled(random).take(3)
        val oddWord = categories[oddIndex].words.shuffled(random).first()

        return OddWordQuestion(choices = (mainWords + oddWord).shuffled(random), answer = oddWord)
    }
}

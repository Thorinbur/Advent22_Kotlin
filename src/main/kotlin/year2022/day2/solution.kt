package year2022.day2

import year2022.day2.Choice.*
import java.io.File

enum class Choice(val pointValue: Int, val alias: Char, val myMoveAlias: Char) {
    ROCK(1, 'A', 'X'),
    PAPER(2, 'B', 'Y'),
    SCISSORS(3, 'C', 'Z');

    companion object {
        fun fromMoveAlias(alias: Char) = values().find { alias == it.alias || alias == it.myMoveAlias }
        fun fromOutcomeAlias(opponentsChoice: Choice, alias: Char): Choice {
            val expectedOutcome = Outcome.fromAlias(alias)
            val outcomes = values().map { myChoice -> myChoice to getResult(opponentsChoice, myChoice) }
            return outcomes.find { it.second == expectedOutcome }!!.first
        }
    }
}

enum class Outcome(val pointValue: Int, val alias: Char) {
    LOSE(0, 'X'),
    DRAW(3, 'Y'),
    WIN(6, 'Z');

    companion object {
        fun fromAlias(alias: Char) = values().find { it.alias == alias }
    }
}

fun getResult(opponentsChoice: Choice, myChoice: Choice): Outcome {
    return when {
        opponentsChoice == myChoice -> Outcome.DRAW
        opponentsChoice == ROCK && myChoice == PAPER -> Outcome.WIN
        opponentsChoice == PAPER && myChoice == SCISSORS -> Outcome.WIN
        opponentsChoice == SCISSORS && myChoice == ROCK -> Outcome.WIN
        else -> Outcome.LOSE
    }
}

data class Round(
    val opponentsChoice: Choice,
    val myChoice: Choice,
) {
    val score = myChoice.pointValue + getResult(opponentsChoice, myChoice).pointValue
}

fun main() {
    val input = File("src/main/kotlin/year2022/day2/input").readLines()
    val roundsPart1 = parsePart1(input)
    val roundsPart2 = parsePart2(input)
    println(roundsPart1.sumOf { it.score })
    println(roundsPart2.sumOf { it.score })
}

fun parsePart1(input: List<String>) = input.map {
    Round(Choice.fromMoveAlias(it.first())!!, Choice.fromMoveAlias(it.last())!!)
}

fun parsePart2(input: List<String>) = input.map {
    val opponentsChoice = Choice.fromMoveAlias(it.first())!!
    Round(opponentsChoice, Choice.fromOutcomeAlias(opponentsChoice, it.last()))
}

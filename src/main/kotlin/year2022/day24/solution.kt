package year2022.day24

import year2022.day24.Direction.*
import java.io.File

var boardWidth = 0
var boardHeight = 0

private enum class Direction {
    N, S, E, W;

    companion object {
        fun from(char: Char) =
            when (char) {
                '>' -> E
                '^' -> N
                '<' -> W
                'v' -> S
                else -> null
            }
    }
}

private data class Tornado(var x: Int, var y: Int, val direction: Direction) {
    fun move() {
        when (direction) {
            N -> {
                y -= 1
                if (y < 0) y = boardHeight - 1
            }

            S -> {
                y += 1
                if (y >= boardHeight) y = 0
            }

            W -> {
                x -= 1
                if (x < 0) x = boardWidth - 1
            }

            E -> {
                x += 1
                if (x >= boardWidth) x = 0
            }
        }
    }
}

private var tornadoes: MutableList<Tornado> = mutableListOf()
private var reachablePositions: Set<Pair<Int, Int>> = HashSet()

fun main() {
    parseInput()
    var iteration = 0

    var movesRequired = calculateTimeRequired(0 to 0, boardWidth-1 to boardHeight -1)
    //we are stopping one step before exit so we do one more simulation step
    moveTornadoes()
    reachablePositions = emptySet()
    movesRequired++
    println("Moves required to reach the end: $movesRequired")

    var movesRequiredToGoBack = calculateTimeRequired(boardWidth-1 to boardHeight -1, 0 to 0,)
    moveTornadoes()
    reachablePositions = emptySet()
    movesRequiredToGoBack++

    var movesRequiredToReachEndSecondTime = calculateTimeRequired(0 to 0, boardWidth-1 to boardHeight -1)
    //we are stopping one step before exit so we do one more simulation step
    moveTornadoes()
    reachablePositions = emptySet()
    movesRequiredToReachEndSecondTime++
    val total = movesRequired+movesRequiredToGoBack+movesRequiredToReachEndSecondTime
    println("Moves required to reach the end second time: $total")
}

private fun calculateTimeRequired(startPosition: Pair<Int, Int>, endPosition: Pair<Int, Int>): Int {
    var iteration = 0
    while (!reachablePositions.contains(endPosition)) {
        moveTornadoes()
        val safeSpots = calculateSafeSpots()
        reachablePositions = updateReachablePositions(safeSpots, startPosition)
        iteration++
    }
    return iteration
}


private val Char.isTornado get() = this == 'v' || this == '^' || this == '<' || this == '>'

private fun parseInput() {
    val input = File("src/main/kotlin/year2022/day24/input").readLines()
    boardHeight = input.size - 2
    boardWidth = input.first().length - 2
    input.drop(1).dropLast(1).forEachIndexed { rowIndex, row ->
        row.drop(1).dropLast(1).forEachIndexed { columnIndex, char ->
            if (char.isTornado) tornadoes.add(Tornado(columnIndex, rowIndex, Direction.from(char)!!))
        }
    }
}

private fun calculateSafeSpots(): Array<Array<Boolean>> {
    val output = Array(boardHeight) { Array(boardWidth) { true } }
    tornadoes.forEach {
        output[it.y][it.x] = false
    }
    return output
}

private fun updateReachablePositions(
    safeSpots: Array<Array<Boolean>>,
    startPosition: Pair<Int, Int>
): Set<Pair<Int, Int>> {
    fun Pair<Int, Int>.isSafe():Boolean {
        return if (first < 0 || first >= boardWidth || second < 0 || second >= boardHeight) false
        else safeSpots[second][first]
    }

    val newPositions = mutableSetOf<Pair<Int, Int>>()

    fun Pair<Int, Int>.addIfSafe() {
        if (isSafe()) newPositions.add(this)
    }

    //try to enter the map
    startPosition.addIfSafe()

    //calculate new reachable positions
    reachablePositions.forEach {
        val x = it.first
        val y = it.second
        val candidates = listOf(
            x to y,
            x + 1 to y,
            x - 1 to y,
            x to y + 1,
            x to y - 1,
        )
        candidates.forEach { candidate ->
            candidate.addIfSafe()
        }
    }
    return newPositions
}

private fun moveTornadoes() = tornadoes.forEach { it.move() }

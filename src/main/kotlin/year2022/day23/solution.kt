package year2022.day23

import year2022.day23.Direction.*
import java.io.File

private var plannedMoves = HashMap<Pair<Int, Int>, MoveTarget>()
private var elves = HashMap<Pair<Int, Int>, Elf>()

private class MoveTarget(val x: Int, val y: Int) {
    var collision = false
}

private enum class Direction {
    N, S, W, E;

    fun next(): Direction {
        return when (this) {
            N -> S
            S -> W
            W -> E
            E -> N
        }
    }

    companion object {
        fun allStartingFrom(direction: Direction): List<Direction> {
            return listOf(direction, direction.next(), direction.next().next(), direction.next().next().next())
        }
    }
}

private class Elf(var x: Int, var y: Int) {
    var plannedMove: MoveTarget? = null

    fun canMove(): Boolean {
        return elves.containsKey(x - 1 to y - 1) ||
                elves.containsKey(x to y - 1) ||
                elves.containsKey(x + 1 to y - 1) ||
                elves.containsKey(x - 1 to y + 1) ||
                elves.containsKey(x to y + 1) ||
                elves.containsKey(x + 1 to y + 1) ||
                elves.containsKey(x - 1 to y) ||
                elves.containsKey(x + 1 to y)
    }

    fun canMoveUp(): Boolean {
        return !elves.containsKey(x - 1 to y - 1) &&
                !elves.containsKey(x to y - 1) &&
                !elves.containsKey(x + 1 to y - 1)
    }

    fun canMoveDown(): Boolean {
        return !elves.containsKey(x - 1 to y + 1) &&
                !elves.containsKey(x to y + 1) &&
                !elves.containsKey(x + 1 to y + 1)
    }

    fun canMoveRight(): Boolean {
        return !elves.containsKey(x + 1 to y - 1) &&
                !elves.containsKey(x + 1 to y) &&
                !elves.containsKey(x + 1 to y + 1)
    }

    fun canMoveLeft(): Boolean {
        return !elves.containsKey(x - 1 to y + 1) &&
                !elves.containsKey(x - 1 to y) &&
                !elves.containsKey(x - 1 to y - 1)
    }

    fun canMoveDirection(dir: Direction): Boolean {
        return canMove() && when (dir) {
            N -> canMoveUp()
            S -> canMoveDown()
            E -> canMoveRight()
            W -> canMoveLeft()
        }
    }

    fun planMove(dir: Direction) {
        when (dir) {
            N -> planMove(x, y - 1)
            S -> planMove(x, y + 1)
            E -> planMove(x + 1, y)
            W -> planMove(x - 1, y)
        }
    }

    fun planMove(x: Int, y: Int) {
        plannedMove = if (plannedMoves.containsKey(x to y)) {
            //planed move collides with other
            plannedMoves[x to y]!!.also { it.collision = true }
        } else {
            MoveTarget(x, y).also { plannedMoves[x to y] = it }
        }
    }
}

fun main() {
    val input = File("src/main/kotlin/year2022/day23/input").readLines()
    val parsedElves = mutableListOf<Elf>()
    input.forEachIndexed { rowIndex, row ->
        row.forEachIndexed { columnIndex, char ->
            if (char == '#') parsedElves.add(Elf(columnIndex, rowIndex))
        }
    }

    var firstDirectionToConsider = N
    var listOfElves: List<Elf> = parsedElves
    var iteration = 0
    var elvesMoved = 1
    while(elvesMoved > 0) {
        listOfElves.forEach {
            elves[it.x to it.y] = it
        }
        val allDirections = Direction.allStartingFrom(firstDirectionToConsider)
        allDirections.forEach { direction ->
            elves.forEach { (_, elf) ->
                if (elf.plannedMove == null && elf.canMoveDirection(direction)) elf.planMove(direction)
            }
        }
        elvesMoved = 0
        elves.forEach { (_, elf) ->
            if (elf.plannedMove?.collision == false) {
                elf.x = elf.plannedMove!!.x
                elf.y = elf.plannedMove!!.y
                elvesMoved++
            }
            elf.plannedMove = null
        }
        listOfElves = elves.values.toList()
        if(iteration == 9) {
            val area = calculateBoundingArea(listOfElves)
            val emptySpace = area - listOfElves.size
            println("Part1: $emptySpace")
        }
        elves.clear()
        plannedMoves.clear()
        firstDirectionToConsider = firstDirectionToConsider.next()
        iteration++
    }
    println("Part2: $iteration")
}

private fun calculateBoundingArea(listOfElves: List<Elf>): Int {
    val rectangleHeight = listOfElves.maxBy { it.y }.y - listOfElves.minBy { it.y }.y + 1
    val rectangleWidth = listOfElves.maxBy { it.x }.x - listOfElves.minBy { it.x }.x + 1

    return rectangleWidth * rectangleHeight
}

private fun drawArea(minX: Int, minY: Int, rectangleHeight: Int, rectangleWidth: Int) {
    (0 until rectangleHeight).forEach { row ->
        (0 until rectangleWidth).forEach { column ->
            if (elves.values.any { it.x == column + minX && it.y == row + minY }) print("#") else print(".")
        }
        println()
    }
}
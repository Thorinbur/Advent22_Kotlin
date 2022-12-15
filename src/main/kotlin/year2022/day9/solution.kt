package year2022.day9

import year2022.day9.Direction.*
import java.io.File
import java.security.InvalidParameterException
import kotlin.math.absoluteValue

private enum class Direction {
    UP, DOWN, LEFT, RIGHT;

    companion object {
        fun fromString(string: String) = when (string) {
            "U" -> UP
            "D" -> DOWN
            "L" -> LEFT
            "R" -> RIGHT
            else -> throw InvalidParameterException("Value needs to be one of \"U\" \"D\" \"L\" \"R\"")
        }
    }
}

private data class Position(var x: Int, var y: Int) {
    fun move(direction: Direction) {
        when (direction) {
            UP -> y += 1
            DOWN -> y -= 1
            LEFT -> x -= 1
            RIGHT -> x += 1
        }
    }

    infix fun sameColumnAs(other: Position) = y == other.y
    infix fun sameRowAs(other: Position) = x == other.x
    infix fun isDiagonalOf(other: Position) = !(this sameColumnAs other) && !(this sameRowAs other)
    infix fun isApartOf(other: Position) = (x - other.x).absoluteValue > 1 || (y - other.y).absoluteValue > 1
}

private class Rope(length:Int) {
    val segments = List(length){Position(0,0)}
    val head = segments.first()
    val tail = segments.last()
    val tailPositions = HashSet<Pair<Int, Int>>()

    fun move(direction: Direction) {
        head.move(direction)
        updateSegments()
        collectTailPosition()
    }

    fun updateSegments(){
        segments.forEachIndexed{index, segment ->
            if (index > 0) updateSegmentPosition(segment, segments[index-1])
        }
    }
    private fun updateSegmentPosition(segment:Position, previous:Position) {
        fun catchUpVertically(){
            if (previous.y > segment.y) segment.y++ else segment.y--
        }

        fun catchUpHorizontally() {
            if (previous.x > segment.x) segment.x++ else segment.x--
        }

        if (previous sameColumnAs segment && previous isApartOf segment) {
            catchUpVertically()
        }
        if (previous sameRowAs segment && previous isApartOf segment) {
            catchUpHorizontally()
        }
        if (previous isDiagonalOf segment && previous isApartOf segment) {
            catchUpVertically()
            catchUpHorizontally()
        }
    }

    private fun collectTailPosition(){
        tailPositions.add(tail.x to tail.y)
    }
}


fun main() {
    val data = File("src/main/kotlin/year2022/day9/input").readLines()
    val moves = data.map {line->
        val (direction, distance) = line.split(" ")
        Pair(Direction.fromString(direction), distance.toInt())
    }

    val shortRope = Rope(2)
    val longRope = Rope(10)

    for (move in moves){
        repeat(move.second){
            shortRope.move(move.first)
            longRope.move(move.first)
        }
    }

    println("tails unique positions: ${shortRope.tailPositions.size}")
    println("tails unique positions: ${longRope.tailPositions.size}")
}
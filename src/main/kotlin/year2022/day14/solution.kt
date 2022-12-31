package year2022.day14

import java.io.File
import kotlin.math.max
import kotlin.math.min

@JvmInline
value class Coordinates(private val field: Pair<Int, Int>) {
    val x get() = field.first
    val y get() = field.second
    val down get() = Coordinates(Pair(x, y + 1))
    val downLeft get() = Coordinates(Pair(x - 1, y + 1))
    val downRight get() = Coordinates(Pair(x + 1, y + 1))
}

fun main() {
    val data = File("src/main/kotlin/year2022/day14/input").readLines()
    val walls = mutableMapOf<Int, MutableMap<Int, Boolean>>()
    val sand = mutableSetOf<Coordinates>()

    var lowestWallY = 0
    data.forEach { line ->
        line.split(" -> ")
            .map { stringCoordinates ->
                val coordinates = stringCoordinates
                    .split(',')
                    .map {
                        it.toInt()
                    }
                Coordinates(coordinates[0] to coordinates[1])
            }.reduce { previous, current ->
                val lowestY = max(previous.y, current.y)
                if (lowestY > lowestWallY) lowestWallY = lowestY

                if (previous.x == current.x) {
                    (min(previous.y, current.y)..max(previous.y, current.y)).forEach {
                        walls.getOrPut(previous.x) { mutableMapOf() }[it] = true
                    }
                } else if (previous.y == current.y) {
                    (min(previous.x, current.x)..max(previous.x, current.x)).forEach {
                        walls.getOrPut(it) { mutableMapOf() }[previous.y] = true
                    }
                }
                current
            }
    }

    val sandStartPoint = Coordinates(Pair(500, 0))
    var sandCoordinates = sandStartPoint
    var sandSettledBeforeOverflow:Int? = null

    while (true) {
        val candidates = listOf(
            sandCoordinates.down,
            sandCoordinates.downLeft,
            sandCoordinates.downRight,
        ).filter { !walls.contains(it) && !sand.contains(it) && it.y < lowestWallY + 2 }
        if (candidates.isEmpty()) {
            //nowhere to go, sand settles at current position
            sand.add(sandCoordinates)
            if(sandCoordinates == sandStartPoint) break
            sandCoordinates = sandStartPoint
        } else {
            sandCoordinates = candidates.first()
            if (sandCoordinates.y > lowestWallY && sandSettledBeforeOverflow == null) {
                sandSettledBeforeOverflow = sand.count()
            }
        }
    }

    println(sandSettledBeforeOverflow)
    println(sand.count())

}

private fun MutableMap<Int, MutableMap<Int, Boolean>>.contains(
    coordinates: Coordinates
) = getOrPut(coordinates.x) { mutableMapOf() }.getOrDefault(coordinates.y, false)


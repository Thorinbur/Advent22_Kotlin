package year2022.day15

import java.io.File
import java.math.BigInteger
import kotlin.math.absoluteValue

private sealed class RangeEdge(val x: Int) {
    class RangeStart(x: Int) : RangeEdge(x)
    class RangeEnd(x: Int) : RangeEdge(x)
}

@JvmInline
value class Coordinates(private val field: Pair<Int, Int>) {
    val x get() = field.first
    val y get() = field.second

    constructor(x: Int, y: Int) : this(Pair(x, y))
}

val data = File("src/main/kotlin/year2022/day15/input").readLines().map { line ->
    val (sensorX, sensorY, beaconX, beaconY) = line.split(":")
        .flatMap { it.split(',').map { it.substringAfter('=').toInt() } }
    Pair(Coordinates(sensorX to sensorY), Coordinates(beaconX to beaconY))
}

fun main() {
    val sensors = data.map { it.first to getRadius(it.first, it.second) }
    val coveredSquares = countCoveredSpaces(sensors, 2000000)
    val beaconsOnTargetY = data.map { it.second }.filter { it.y == 2000000 }.distinct().count()
    println("spaces covered at Y = 2000000: ${coveredSquares - beaconsOnTargetY}")
    val y = 2601918
    val coveredSquaresClamped = countCoveredSpaces(sensors, y, 0..4000000)
    if (coveredSquaresClamped != 4000001) println("found candidate at: y = $y")

}

fun countCoveredSpaces(sensors: List<Pair<Coordinates, Int>>, targetY: Int, clampRange: IntRange? = null): Int {
    val coveredRanges = sensors.map { getIntersectingRange(it.first, it.second, targetY) }
    val rangeEdges = coveredRanges.flatMap { itersectRange ->
        if (itersectRange.isEmpty()) emptyList()
        else listOf(
            RangeEdge.RangeStart(itersectRange.start.coerceAtLeast(clampRange?.start ?: Int.MIN_VALUE)),
            RangeEdge.RangeEnd(itersectRange.endInclusive.coerceAtMost(clampRange?.endInclusive ?: Int.MAX_VALUE))
        )
    }.sortedBy { it.x * 10 + if (it is RangeEdge.RangeEnd) 2 else 1 }
    var coveredSquares = 0
    var openedRanges = 0
    var outerMostStartX = 0
    rangeEdges.forEachIndexed { index, edge ->
        if (edge is RangeEdge.RangeStart) {
            if (openedRanges == 0) outerMostStartX = edge.x
            openedRanges++
        } else {
            openedRanges--
            if (openedRanges == 0)
                if (rangeEdges.getOrNull(index + 1)?.x != edge.x + 1) println(
                    "found discontinuity at index = ${edge.x + 1}, frequency = ${
                        (BigInteger.valueOf(
                            4000000
                        ).times(BigInteger.valueOf((edge.x + 1).toLong()))).add(BigInteger.valueOf(2601918L))
                    }"
                )
            coveredSquares += (outerMostStartX..edge.x).count()
        }
    }
    return coveredSquares
}

fun getRadius(sensor: Coordinates, beacon: Coordinates): Int {
    return (beacon.x - sensor.x).absoluteValue + (beacon.y - sensor.y).absoluteValue
}

fun getIntersectingRangeLength(origin: Coordinates, radius: Int, targetY: Int): Int {
    val distanceToTarget = (origin.y - targetY).absoluteValue
    return ((radius - distanceToTarget) * 2 + 1).coerceAtLeast(0)
}

fun getIntersectingRange(sensor: Coordinates, radius: Int, targetY: Int): IntRange {
    val intersectionLength = getIntersectingRangeLength(sensor, radius, targetY)
    if (intersectionLength == 0) return IntRange.EMPTY
    val rangeCenter = sensor.x
    return rangeCenter - (intersectionLength / 2)..rangeCenter + (intersectionLength / 2)
}
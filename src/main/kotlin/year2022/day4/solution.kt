@file:Suppress("ReplaceRangeStartEndInclusiveWithFirstLast")

package year2022.day4

import java.io.File

private fun parseAsRange(range: String) =
    range.split('-')
        .map { it.toInt() }
        .let {
            it[0]..it[1]
        }

fun IntRange.fullyOverlap(other: IntRange): Boolean {
    return contains(other.start) && contains(other.endInclusive)
}

fun IntRange.partiallyOverlap(other: IntRange): Boolean {
    return contains(other.start) || contains(other.endInclusive)
}

fun main() {
    val assignments = File("src/main/kotlin/year2022/day4/input").readLines().map {
        val (first, second) = it.split(',')
        parseAsRange(first) to parseAsRange(second)
    }

    println("part 1: ${assignments.count { it.first.fullyOverlap(it.second) || it.second.fullyOverlap(it.first) }}")
    println("part 2: ${assignments.count { it.first.partiallyOverlap(it.second) || it.second.partiallyOverlap(it.first) }}")
}


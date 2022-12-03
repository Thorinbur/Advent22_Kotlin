package year2022.day3

import java.io.File

fun Char.priority() =
    if (isLowerCase()) this - 'a' + 1
    else this - 'A' + 27


fun main() {
    val sacks = File("src/main/kotlin/year2022/day3/input").readLines()
    val groups = sacks
        .chunked(3)
        .map { group -> group.sortedBy { it.length } }
    val prioritiesPerGroup = groups.map { group ->
        group
            .first()
            .find { group[1].contains(it) && group[2].contains(it) }!!
            .priority()
        }

    val prioritiesPerSack = sacks.map {
        it.take(it.length / 2) to it.drop(it.length / 2)
    }.map { sack ->
        sack.first.find { sack.second.contains(it) }!!.priority()
    }

    println("part 1: ${prioritiesPerSack.sum()}")
    println("part 2: ${prioritiesPerGroup.sum()}")
}
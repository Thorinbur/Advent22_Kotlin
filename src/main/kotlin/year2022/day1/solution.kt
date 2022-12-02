package year2022.day1

import java.io.File

fun main() {
    val data = File("src/main/kotlin/year2022/day1/input").readLines()
    val grouped = data.split { it.isBlank() }
    val sums = grouped.map { it.sumOf { line -> line.toInt() } }

    fun getTopN(n: Int) = sums.sortedDescending().take(n).sum()

    println("part 1 solution: ${getTopN(1)}")
    println("part 2 solution: ${getTopN(3)}")
}

/** Splits a list on items matching predicate
 *  Items matching predicate are NOT included in the resulting lists
 */
private fun <E> List<E>.split(predicate: (E) -> Boolean) =
    fold(mutableListOf<MutableList<E>>(mutableListOf())) { acc, element ->
        if (predicate(element)) acc.apply { this.add(mutableListOf()) }
        else acc.apply { last().add(element) }
    }

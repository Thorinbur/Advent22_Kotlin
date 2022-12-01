package year2022.problem1.a

import java.io.File

fun main() {
    val data = File("src/main/kotlin/year2022/problem1/1.in").readLines()
    val grouped = data.split { it.isBlank() }
    val sums = grouped.map { it.sumOf { line -> line.toInt() } }

    fun getTopN(n: Int) = sums.sortedDescending().take(n).sum()

    println("part 1 solution: ${getTopN(1)}")
    println("part 1 solution: ${getTopN(3)}")
}

private fun <E> List<E>.split(predicate: (E) -> Boolean) =
    fold(mutableListOf<MutableList<E>>(mutableListOf())) { acc, element ->
        if (predicate(element)) acc.apply { this.add(mutableListOf()) }
        else acc.apply { last().add(element) }
    }

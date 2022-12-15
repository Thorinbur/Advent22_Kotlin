package year2022.day6

import java.io.File

private val data = File("src/main/kotlin/year2022/day6/input").readText()
fun main() {
    println("start-of-packet marker:" + findUniqueSubset(4))
    println("start-of-message marker:" + findUniqueSubset(14))
}

fun findUniqueSubset(length:Int): Int {
    val subsets = data.mapIndexed { index, _ ->
        if (index < length-1) "aa"
        else data.subSequence(index-(length-1), index+1)
    }

    return subsets.indexOfFirst { it.toSet().size == length } + 1
}

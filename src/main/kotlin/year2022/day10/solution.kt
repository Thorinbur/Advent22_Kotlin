package year2022.day10

import java.io.File
import kotlin.math.absoluteValue

const val NOOP = "noop"
fun main() {
    val data = File("src/main/kotlin/year2022/day10/input").readLines()
    val operations = data.flatMap {
        if (it.startsWith(NOOP)) listOf(0) else listOf(0, it.split(" ")[1].toInt())
    }

    var x = 1
    var cumulativeSignalStrength = 0
    operations.forEachIndexed { index, value ->
        val cycle = index + 1
        val cursorPosition = index % 40
        draw(spritePos = x, cursorPosition = cursorPosition)
        if (cursorPosition == 39) println()
        if (cycle % 40 == 20) cumulativeSignalStrength += cycle * x
        x += value
    }

    println(cumulativeSignalStrength)
}

fun draw(spritePos: Int, cursorPosition: Int) {
    if ((spritePos - cursorPosition).absoluteValue <= 1) print("#") else print(".")
}

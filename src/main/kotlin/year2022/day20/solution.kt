package year2022.day20

import java.io.File
import java.util.LinkedList
import kotlin.math.absoluteValue

class Value(val value: Long){
    override fun toString() = value.toString()
}

private const val KEY = 811589153L

fun main() {
    val values = File("src/main/kotlin/year2022/day20/input").readLines().map { Value(it.toLong()) }

    val part1 = decrypt(values, 1, 1L)
    printCoordinates(part1)

    val part2 = decrypt(values, 10, KEY)
    printCoordinates(part2)
}

private fun printCoordinates(list: LinkedList<Value>) {
    val size = list.size
    val indexOf0 = list.indexOfFirst { it.value == 0L }
    val _1000th = list.get((indexOf0 + 1000) % size).value
    val _2000th = list.get((indexOf0 + 2000) % size).value
    val _3000th = list.get((indexOf0 + 3000) % size).value
    println(_1000th + _2000th + _3000th)
}

private fun decrypt(
    input: List<Value>,
    interations: Int,
    key: Long
): LinkedList<Value> {
    val values = input.map { Value(it.value * key) }
    val list = LinkedList(values)
    val size = values.size

    repeat(interations) {
        for (value in values) {
            val currentPosition = list.indexOf(value)
            var targetPosition = currentPosition + (value.value % (size - 1))
            if (targetPosition < 0) targetPosition = size - 1 - targetPosition.absoluteValue
            targetPosition = targetPosition % (size - 1)
            list.removeAt(currentPosition)
            list.add(targetPosition.toInt(), value)
        }
    }
    return list
}

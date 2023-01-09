package year2022.day25

import java.io.File

fun main() {
    val input = File("src/main/kotlin/year2022/day25/input").readLines()
    val sum = input.map { decode(it) }.sum()
    println(sum)
    println(encode(sum))
}

fun decode(snafu: String): Long {
    val reversed = snafu.reversed()
    var sum = 0L
    reversed.forEachIndexed { power, char ->
        sum += decodeDigit(char) * (Math.pow(5.0, power.toDouble()).toLong())
    }
    return sum
}

fun decodeDigit(char: Char) =
    when (char) {
        '-' -> -1
        '=' -> -2
        else -> char.digitToInt()
    }

fun encode(value: Long): String {
    var factor = 5

    if (value == 0L) return "0"

    var value = value
    var output = ""
    while (value > 0) {
        val reminder = (value % factor)
        if(reminder == 3L){
            output += "="
            value = (value+factor) / factor
        } else if(reminder == 4L){
            output += "-"
            value = (value+factor) / factor
        } else {
            output += reminder.toString()
            value /= factor
        }
    }
    return output.reversed()
}
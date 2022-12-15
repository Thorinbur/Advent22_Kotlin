package year2022.day13

import java.io.File
import java.lang.IllegalArgumentException

private sealed class Entry() : Comparable<Entry> {
    abstract fun print(): String
}

private class EntryList(
    val items: List<Entry>
) : Entry() {
    override fun print() = "[" + items.joinToString { it.print() } + "]"

    override fun compareTo(other: Entry): Int {
        val otherItems = when (other) {
            is Item -> listOf(other as Entry)
            is EntryList -> other.items
        }
        items.forEachIndexed { index, value ->
            val otherItem = otherItems.getOrNull(index) ?: return 1
            value.compareTo(otherItem).takeIf { it != 0 }?.let { return it }
        }
        return items.size - otherItems.size
    }
}

private class Item(
    val value: Int
) : Entry() {
    override fun print() = value.toString()

    override fun compareTo(other: Entry): Int {
        return when (other) {
            is Item -> value.compareTo(other.value)
            is EntryList -> EntryList(listOf(Item(value))).compareTo(other)
        }
    }
}

fun main() {
    val data = File("src/main/kotlin/year2022/day13/input").readLines()

    val pairs = data.chunked(3).map { it[0].parse() to it[1].parse() }

    val sumOfIndexes = pairs.mapIndexed { index, pair ->
        if (pair.first < pair.second) index + 1 else 0
    }.sum()
    val dividerStart = EntryList(listOf(EntryList(listOf(Item(2)))))
    val dividerEnd = EntryList(listOf(EntryList(listOf(Item(6)))))
    val allPackets = (pairs.flatMap { listOf(it.first, it.second) } + listOf(dividerStart, dividerEnd)).sorted()
    val startIndex = allPackets.indexOf(dividerStart) + 1
    val endIndex = allPackets.indexOf(dividerEnd) + 1

    allPackets.forEach { println(it.print()) }

    println(sumOfIndexes)
    println(startIndex * endIndex)
}

private fun String.parse(): Entry {
    return if (first() == '[') parseList()
    else substringBefore(',').parseItem()
}

private fun String.parseList(): EntryList {
    if (first() != '[') throw IllegalArgumentException()
    val items = mutableListOf<Entry>()
    var substring = this.drop(1).dropLast(1)
    while (substring.isNotEmpty()) {
        if (substring.first() == ',') {
            substring = substring.drop(1)
        } else if (substring.first() == '[') {
            val indexOfMatching = substring.indexOfMatchingBrace()
            items.add((substring.substring(0, indexOfMatching + 1)).parseList())
            substring = substring.substring(indexOfMatching + 1)
        } else {
            items.add((substring.substringBefore(',').parseItem()))
            substring = substring.substringAfter(',', "")
        }
    }
    return EntryList(items)
}

private fun String.parseItem(): Item {
    return Item(this.toInt())
}

private fun String.indexOfMatchingBrace(): Int {
    val depth = scan(0) { acc, c -> if (c == '[') acc + 1 else if (c == ']') acc - 1 else acc }
    return depth.drop(1).indexOfFirst { it == 0 }
}


package year2022.day8

import java.io.File
import kotlin.math.max


fun main() {
    val data = File("src/main/kotlin/year2022/day8/input").readLines()
    val trees = data.map { it.map { it.digitToInt() }.toIntArray() }.toTypedArray()

    val visible = HashSet<Pair<Int, Int>>()

    trees.drop(1).dropLast(1).forEachIndexed { index, array ->
        countRow(array.toList(), index + 1, visible)
    }
    val columns = (0 until trees[0].size).map { column ->
        Array(trees.size) { row -> trees[row][column] }
    }
    columns.drop(1).dropLast(1).forEachIndexed { index, array ->
        countColumn(array.toList(), index + 1, visible)
    }

    printOnlyVisible(trees, visible)

    println("trees visible from the edges: " + (visible.count() + trees.size * 2 + trees[0].size * 2 - 4))

    val horizontalScores = trees.map { row -> row.mapIndexed { index, _ -> score(row.toList(), index) } }
    val verticalScores = columns.map { column -> column.mapIndexed { index, _ -> score(column.toList(), index) } }
    val totals = trees.mapIndexed { rowIndex, row ->
        row.mapIndexed { columnIndex, value ->
            horizontalScores[rowIndex][columnIndex] * verticalScores[columnIndex][rowIndex]
        }
    }
    println("highest score tree: " + (totals.map {
        it.toList().max()
    }.toList().max()))
}

private fun printOnlyVisible(
    trees: Array<IntArray>,
    visible: HashSet<Pair<Int, Int>>
) {
    trees.forEachIndexed { rowIndex, row ->
        row.forEachIndexed { columnIndex, value ->
            print(if (visible.contains(columnIndex to rowIndex)) value else " ")
        }
        println()
    }
}

fun countRow(row: List<Int>, rowIndex: Int, visible: HashSet<Pair<Int, Int>>) {
    row.reduceIndexed { index, acc, i ->
        if (index != row.size - 1 && i > acc) visible.add(index to rowIndex)
        max(acc, i)
    }
    row.reduceRightIndexed { index, i, acc ->
        if (index != 0 && i > acc) visible.add(index to rowIndex)
        max(acc, i)
    }
}

fun countColumn(column: List<Int>, columnIndex: Int, visible: HashSet<Pair<Int, Int>>) {
    column.reduceIndexed { index, acc, i ->
        if (index != column.size - 1 && i > acc) visible.add(columnIndex to index)
        max(acc, i)
    }
    column.reduceRightIndexed { index, i, acc ->
        if (index != 0 && i > acc) visible.add(columnIndex to index)
        max(acc, i)
    }
}

fun score(list: List<Int>, position: Int): Int {
    val value = list[position]
    val itemsBefore = list.take(position)
    val firstTallerBefore = itemsBefore.indexOfLast { it >= value }.takeIf { it > -1 } ?: 0
    val visibleBefore = itemsBefore.size - firstTallerBefore
    val itemsAfter = list.drop(position + 1)
    val firstTallerAfter = itemsAfter.indexOfFirst { it >= value }.takeIf { it > -1 } ?: (itemsAfter.size - 1)
    val visibleAfter = firstTallerAfter + 1

    return visibleBefore * visibleAfter
}
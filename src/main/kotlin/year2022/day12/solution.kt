package year2022.day12

import java.io.File
import java.util.LinkedList

lateinit var startNode: Node
lateinit var endNode: Node

class Node(
    val height: Int,
    var x: Int,
    var y: Int
) {
    var distance = Int.MAX_VALUE
    var queued = false
}

val queue = LinkedList<Node>()

lateinit var map: List<List<Node>>

fun main() {
    val data = File("src/main/kotlin/year2022/day12/input").readLines()

    map = data.mapIndexed { rowIndex: Int, value: String ->
        value.mapIndexed { columnIndex, char ->
            val height = when (char) {
                'S' -> 0
                'E' -> 23
                else -> (char - 'a')
            }
            Node(height, columnIndex, rowIndex).also {
                if (char == 'S') startNode = it else if (char == 'E') endNode = it
            }
        }
    }

    endNode.distance = 0
    endNode.queued = true
    var currentNode = endNode
    queue.add(endNode)
    while (currentNode != startNode) {
        val neighbours = listOf(
            getUp(currentNode.x, currentNode.y),
            getRight(currentNode.x, currentNode.y),
            getDown(currentNode.x, currentNode.y),
            getLeft(currentNode.x, currentNode.y),
        ).filterNotNull().filter{it.distance>currentNode.distance+1}.filter { it.height >= (currentNode.height-1) }
        neighbours.forEach {
            it.queued = true
            it.distance = currentNode.distance + 1
        }
        queue.addAll(neighbours)
        currentNode = queue.poll()
    }

    println( startNode.distance)
    println(map.flatten().filter { it.height == 0 }.minBy { it.distance }.distance)
}

fun getUp(x: Int, y: Int) = map.getOrNull(y - 1)?.getOrNull(x)
fun getRight(x: Int, y: Int) = map.getOrNull(y)?.getOrNull(x + 1)
fun getDown(x: Int, y: Int) = map.getOrNull(y)?.getOrNull(x - 1)
fun getLeft(x: Int, y: Int) = map.getOrNull(y + 1)?.getOrNull(x)

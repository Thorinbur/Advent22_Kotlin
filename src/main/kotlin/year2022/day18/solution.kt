package year2022.day18

import java.io.File
import java.util.*

data class Cube(
    val x: Int,
    val y: Int,
    val z: Int,
) {
    val up get() = Cube(x, y + 1, z)
    val down get() = Cube(x, y - 1, z)
    val left get() = Cube(x - 1, y, z)
    val right get() = Cube(x + 1, y, z)
    val forward get() = Cube(x, y, z + 1)
    val back get() = Cube(x, y, z - 1)
    val neighbours
        get() = listOf(
            up, down, left, right, forward, back
        )
}

val cubes = File("src/main/kotlin/year2022/day18/input").readLines().map {
    val (x, y, z) = it.split(",").map { it.toInt() }
    Cube(x, y, z)
}.toHashSet()

fun main() {
    val minX = cubes.minBy { it.x }.x - 1
    val maxX = cubes.maxBy { it.x }.x + 1
    val minY = cubes.minBy { it.y }.y - 1
    val maxY = cubes.maxBy { it.y }.y + 1
    val minZ = cubes.minBy { it.z }.z - 1
    val maxZ = cubes.maxBy { it.z }.z + 1
    val validXRange = minX..maxX
    val validYRange = minY..maxY
    val validZRange = minZ..maxZ
    println("minX: $minX")
    println("maxX: $maxX")
    println("minY: $minY")
    println("maxY: $maxY")
    println("minZ: $minZ")
    println("maxZ: $maxZ")
    val nonCoveredSides = cubes.map { cube ->
        6 - cube.neighbours.count { it in cubes }
    }.sum()
    println(nonCoveredSides)

    //Part 2
    val queue = LinkedList<Cube>()
    val air = hashSetOf<Cube>()
    queue.add(Cube(minX, minY, minZ))
    while (!queue.isEmpty()) {
        val cube = queue.pop()
        cube.neighbours.forEach {
            if (it.x in validXRange && it.y in validYRange && it.z in validZRange) {
                if (it !in air && it !in cubes) {
                    air.add(it)
                    queue.push(it)
                }
            }
        }
    }
    val outerSides = cubes.map { cube ->
        cube.neighbours.count { it in air }
    }.sum()
    println(outerSides)
}
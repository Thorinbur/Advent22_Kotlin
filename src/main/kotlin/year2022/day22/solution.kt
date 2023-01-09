package year2022.day22

import java.io.File
import java.lang.Integer.max

private class Segment(val tiles: List<List<Tile>>) {
    private val _transposed = tiles.transpose()
    fun stitchInner() {
        fun stitchInner(tiles: List<Tile>, setPrevious: Tile.(Tile) -> Unit, setNext: Tile.(Tile) -> Unit) {
            val size = tiles.size
            tiles.forEachIndexed { index, tile ->
                if (index > 0) {
                    tile.setPrevious(tiles[index - 1])
                }
                if (index < size - 1) {
                    tile.setNext(tiles[index + 1])
                }
            }
        }

        tiles.forEach { row ->
            stitchInner(row, { this.left = it }, { this.right = it })
        }
        tiles.transpose().forEach { column ->
            stitchInner(column, { this.top = it }, { this.bottom = it })
        }
    }

    val topEdge get() = tiles.first()
    val bottomEdge get() = tiles.last()
    val leftEdge get() = _transposed.first()
    val rightEdge get() = _transposed.last()
}

private fun Array<Array<Segment?>>.firstNonNullLeftOf(x: Int, y: Int): Segment {
    val row = this[y].toList()
    row.take(x).filterNotNull().lastOrNull()?.let { return it }
    return row.reversed().filterNotNull().first()
}

private fun Array<Array<Segment?>>.firstNonNullRightOf(x: Int, y: Int): Segment {
    val row = this[y].toList()
    row.drop(x + 1).filterNotNull().firstOrNull()?.let { return it }
    return row.filterNotNull().first()
}

private fun Array<Array<Segment?>>.firstNonNullTopOf(x: Int, y: Int): Segment {
    val column = this.toList().map { it.toList() }.transpose()[x]
    column.take(y).filterNotNull().lastOrNull()?.let { return it }
    return column.reversed().filterNotNull().first()
}

private fun Array<Array<Segment?>>.firstNonNullBottomOf(x: Int, y: Int): Segment {
    val column = this.toList().map { it.toList() }.transpose()[x]
    column.drop(y + 1).filterNotNull().firstOrNull()?.let { return it }
    return column.filterNotNull().first()
}

fun <T> List<List<T>>.transpose(): List<List<T>> {
    return (0 until first().size).map { column ->
        List(size) { row -> this[row][column] }
    }
}

private enum class Direction(val passwordValue: Int, val character: Char) {
    RIGHT(0, '>'),
    DOWN(1, 'v'),
    LEFT(2, '<'),
    UP(3, '^'),
    ;

    fun turn(turnDirection: TurnDirection): Direction =
        if (turnDirection == TurnDirection.CLOCKWISE)
            when (this) {
                RIGHT -> DOWN
                DOWN -> LEFT
                LEFT -> UP
                UP -> RIGHT
            }
        else
            when (this) {
                RIGHT -> UP
                DOWN -> RIGHT
                LEFT -> DOWN
                UP -> LEFT
            }
}

private enum class TurnDirection {
    CLOCKWISE,
    COUNTERCLOCKWISE,
    ;

    companion object {
        fun from(representation: Char) =
            when (representation) {
                'R' -> CLOCKWISE
                else -> COUNTERCLOCKWISE
            }
    }
}

private sealed class Tile(val x: Int, val y: Int, val isPassable: Boolean) {
    lateinit var top: Tile
    lateinit var right: Tile
    lateinit var bottom: Tile
    lateinit var left: Tile
    var topIsWarp = false
    var rightIsWarp = false
    var bottomIsWarp = false
    var leftIsWarp = false
    lateinit var topDirection: Direction
    lateinit var rightDirection: Direction
    lateinit var bottomDirection: Direction
    lateinit var leftDirection: Direction

    class BoardTile(x: Int, y: Int) : Tile(x, y, true)
    class Wall(x: Int, y: Int) : Tile(x, y, false)

    fun takeStep(direction: Direction): Tile {
        return when (direction) {
            Direction.RIGHT -> right.takeIf { it.isPassable } ?: this
            Direction.DOWN -> bottom.takeIf { it.isPassable } ?: this
            Direction.LEFT -> left.takeIf { it.isPassable } ?: this
            Direction.UP -> top.takeIf { it.isPassable } ?: this
        }
    }

    fun isWarp(direction: Direction): Boolean {
        return when (direction) {
            Direction.RIGHT -> rightIsWarp
            Direction.DOWN -> bottomIsWarp
            Direction.LEFT -> leftIsWarp
            Direction.UP -> topIsWarp
        }
    }

    fun directionAfterWarp(direction: Direction): Direction {
        return when (direction) {
            Direction.RIGHT -> rightDirection
            Direction.DOWN -> bottomDirection
            Direction.LEFT -> leftDirection
            Direction.UP -> topDirection
        }
    }

    companion object {
        fun from(representation: Char, x: Int, y: Int): Tile? {
            return when (representation) {
                '.' -> BoardTile(x, y)
                '#' -> Wall(x, y)
                else -> null
            }
        }
    }
}

private var steps = HashMap<Pair<Int, Int>, Direction>()

fun main() {
    val input = File("src/main/kotlin/year2022/day22/input").readLines()

    val part1StartingTile = parseAsMap(input.dropLast(2), false)
    val instructions = parseInstructions(input.last())

    var currentTile = part1StartingTile
    var currentDirection = Direction.RIGHT

    fun runSimulation() {
        instructions.forEach { instruction ->
            if (instruction is Int) {
                repeat(instruction) {
                    steps.put(currentTile.x to currentTile.y, currentDirection)
                    val previousTile = currentTile
                    currentTile = currentTile.takeStep(currentDirection)
                    if (previousTile != currentTile && previousTile.isWarp(currentDirection)) {
                        currentDirection = previousTile.directionAfterWarp(currentDirection)
                    }
                }
            } else if (instruction is TurnDirection) {
                currentDirection = currentDirection.turn(instruction)
            }
        }
    }

    runSimulation()

//    printMap(input)
    println("Part1:")
    println("Final position: x = ${currentTile.x}, y = ${currentTile.y}, direction = ${currentDirection},")
    println("password = ${1000 * currentTile.y + 4 * currentTile.x + currentDirection.passwordValue}")

    val part2StartingTile = parseAsMap(input.dropLast(2), stitchAsCube = true)
    steps = HashMap<Pair<Int, Int>, Direction>()
    currentTile = part2StartingTile
    currentDirection = Direction.RIGHT

    runSimulation()

//    printMap(input)

    println()
    println("Part2:")
    println("Final position: x = ${currentTile.x}, y = ${currentTile.y}, direction = ${currentDirection},")
    println("password = ${1000 * currentTile.y + 4 * currentTile.x + currentDirection.passwordValue}")
}

private fun printMap(input: List<String>) {
    input.forEachIndexed { rowIndex, row ->
        row.forEachIndexed { columnIndex, char ->
            if (steps.containsKey((columnIndex + 1) to (rowIndex + 1))) {
                print(steps[(columnIndex + 1) to (rowIndex + 1)]!!.character)
            } else {
                print(char)
            }
        }
        println()
    }
}

fun parseInstructions(instructions: String): List<Any> {
    val stepValues = instructions.split('R', 'L').map { it.toInt() }
    val rotations = instructions.partition { it == 'R' || it == 'L' }.first.map { TurnDirection.from(it) }
    return stepValues.zip(rotations) { steps, rotation -> listOf(steps, rotation) }.flatten() + stepValues.last()
}

private fun parseAsMap(mapRepresentation: List<String>, stitchAsCube: Boolean): Tile {
    val representationWidth = mapRepresentation.maxBy { it.length }.length
    val mapSegmentSize = max(mapRepresentation.size, mapRepresentation.maxBy { it.length }.length) / 4
    val horizontalSegments = representationWidth / mapSegmentSize
    val verticalSegments = mapRepresentation.size / mapSegmentSize
    val tiles = mapRepresentation.mapIndexed() { y, row ->
        row.padEnd(representationWidth, ' ').mapIndexed { x, tileRepresentation ->
            Tile.from(tileRepresentation, x + 1, y + 1)
        }
    }
    val segments: Array<Array<Segment?>> = Array(verticalSegments) { segmentY ->
        Array(horizontalSegments) { segmentX ->
            if (tiles[segmentY * mapSegmentSize][segmentX * mapSegmentSize] == null) null
            else {
                Segment(tiles.drop(segmentY * mapSegmentSize).take(mapSegmentSize).map {
                    it.drop(segmentX * mapSegmentSize).take(mapSegmentSize).filterNotNull()
                })
            }
        }
    }

    fun stitchTiles(first: List<Tile>, second: List<Tile>, operation: Tile.(other: Tile) -> Unit) {
        first.forEachIndexed { index, tile -> tile.operation(second[index]) }
    }

    fun stitchEdgesFlat() {
        segments.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, segment ->
                if (segment != null) {
                    val leftEdge = segment.tiles.transpose().first()
                    val leftSegment = segments.firstNonNullLeftOf(columnIndex, rowIndex)
                    stitchTiles(leftEdge, leftSegment.tiles.transpose().last()) {
                        this.left = it
                        this.leftIsWarp = true
                        this.leftDirection = Direction.LEFT
                    }
                    val rightEdge = segment.tiles.transpose().last()
                    val rightSegment = segments.firstNonNullRightOf(columnIndex, rowIndex)
                    stitchTiles(rightEdge, rightSegment.tiles.transpose().first()) {
                        this.right = it
                        this.rightIsWarp = true
                        this.rightDirection = Direction.RIGHT
                    }
                    val topEdge = segment.tiles.first()
                    val topSegment = segments.firstNonNullTopOf(columnIndex, rowIndex)
                    stitchTiles(topEdge, topSegment.tiles.last()) {
                        this.top = it
                        this.topIsWarp = true
                        this.topDirection = Direction.UP
                    }
                    val bottomEdge = segment.tiles.last()
                    val bottomSegment = segments.firstNonNullBottomOf(columnIndex, rowIndex)
                    stitchTiles(bottomEdge, bottomSegment.tiles.first()) {
                        this.bottom = it
                        this.bottomIsWarp = true
                        this.bottomDirection = Direction.DOWN
                    }
                }
            }
        }
    }

    fun stitchEdgesCube() {
        val frontSegment = segments[0][1]!!
        val rightSegment = segments[0][2]!!
        val bottomSegment = segments[1][1]!!
        val backSegment = segments[2][1]!!
        val leftSegment = segments[2][0]!!
        val topSegment = segments[3][0]!!
        stitchTiles(frontSegment.bottomEdge, bottomSegment.topEdge) {
            this.bottom = it
            this.bottomIsWarp = true
            this.bottomDirection = Direction.DOWN
            it.top = this
            it.topIsWarp = true
            it.topDirection = Direction.UP
        }
        stitchTiles(bottomSegment.bottomEdge, backSegment.topEdge) {
            this.bottom = it
            this.bottomIsWarp = true
            this.bottomDirection = Direction.DOWN
            it.top = this
            it.topIsWarp = true
            it.topDirection = Direction.UP
        }
        stitchTiles(backSegment.bottomEdge, topSegment.rightEdge) {
            this.bottom = it
            this.bottomIsWarp = true
            this.bottomDirection = Direction.LEFT
            it.right = this
            it.rightIsWarp = true
            it.rightDirection = Direction.UP
        }
        stitchTiles(topSegment.leftEdge, frontSegment.topEdge) {
            this.left = it
            this.leftIsWarp = true
            this.leftDirection = Direction.DOWN
            it.top = this
            it.topIsWarp = true
            it.topDirection = Direction.RIGHT
        }
        stitchTiles(frontSegment.rightEdge, rightSegment.leftEdge) {
            this.right = it
            this.rightIsWarp = true
            this.rightDirection = Direction.RIGHT
            it.left = this
            it.leftIsWarp = true
            it.leftDirection = Direction.LEFT
        }
        stitchTiles(rightSegment.rightEdge, backSegment.rightEdge.reversed()) {
            this.right = it
            this.rightIsWarp = true
            this.rightDirection = Direction.LEFT
            it.right = this
            it.rightIsWarp = true
            it.rightDirection = Direction.LEFT
        }
        stitchTiles(backSegment.leftEdge, leftSegment.rightEdge) {
            this.left = it
            this.leftIsWarp = true
            this.leftDirection = Direction.LEFT
            it.right = this
            it.rightIsWarp = true
            it.rightDirection = Direction.RIGHT
        }
        stitchTiles(leftSegment.leftEdge, frontSegment.leftEdge.reversed()) {
            this.left = it
            this.leftIsWarp = true
            this.leftDirection = Direction.RIGHT
            it.left = this
            it.leftIsWarp = true
            it.leftDirection = Direction.RIGHT
        }
        stitchTiles(leftSegment.bottomEdge, topSegment.topEdge) {
            this.bottom = it
            this.bottomIsWarp = true
            this.bottomDirection = Direction.DOWN
            it.top = this
            it.topIsWarp = true
            it.topDirection = Direction.UP
        }
        stitchTiles(leftSegment.topEdge, bottomSegment.leftEdge) {
            this.top = it
            this.topIsWarp = true
            this.topDirection = Direction.RIGHT
            it.left = this
            it.leftIsWarp = true
            it.leftDirection = Direction.DOWN
        }
        stitchTiles(bottomSegment.rightEdge, rightSegment.bottomEdge) {
            this.right = it
            this.rightIsWarp = true
            this.rightDirection = Direction.UP
            it.bottom = this
            it.bottomIsWarp = true
            it.bottomDirection = Direction.LEFT
        }
        stitchTiles(rightSegment.topEdge, topSegment.bottomEdge) {
            this.top = it
            this.topIsWarp = true
            this.topDirection = Direction.UP
            it.bottom = this
            it.bottomIsWarp = true
            it.bottomDirection = Direction.DOWN
        }
    }

    fun stitch() {
        segments.forEach { row ->
            row.forEach { segment ->
                segment?.stitchInner()
            }
        }
        if (stitchAsCube) stitchEdgesCube()
        else stitchEdgesFlat()
    }

    val startTile = tiles.first().filterNotNull().first()
    stitch()
    return startTile
}

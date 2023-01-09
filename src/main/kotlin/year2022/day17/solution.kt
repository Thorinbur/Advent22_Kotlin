package year2022.day17

import java.io.File
import java.lang.Integer.max
import java.util.*
import kotlin.collections.HashMap

//0,0 is bottom left corner of the shaft
//false = empty, true = occupied
private class Shaft(var bottom: Int, val rows: LinkedList<Array<Boolean>>) {
    fun isEmpty(x: Int, y: Int): Boolean {
        val adjustedY = bottom + y
        return if (x < 0 || x >= 7 || adjustedY <= 0) false else if (adjustedY >= rows.size) true else !rows[y][x]
    }

    fun mark(x: Int, y: Int) {
        val adjustedY = bottom + y
        while (rows.size <= adjustedY) {
            rows.add(Array(7) { false })
        }
        rows[adjustedY][x] = true
    }
}

private val shaft = Shaft(0, LinkedList())

//all calculation assume bottom left tile of a bounding box to be its position
sealed class BlockType(
) {
    abstract fun getOccupied(x: Int, y: Int): List<Pair<Int, Int>>
    fun checkLeftSimple(x: Int) = getOccupied(x - 1, 0).all { it.first >= 0 }
    fun checkRightSimple(x: Int) = getOccupied(x + 1, 0).all { it.first < 7 }
    fun checkLeft(x: Int, y: Int) = getOccupied(x - 1, y).all { shaft.isEmpty(it.first, it.second) }
    fun checkRight(x: Int, y: Int) = getOccupied(x + 1, y).all { shaft.isEmpty(it.first, it.second) }
    fun checkUnder(x: Int, y: Int) = getOccupied(x, y - 1).all { shaft.isEmpty(it.first, it.second) }
    fun stop(x: Int, y: Int) = getOccupied(x, y).forEach {
        shaft.mark(it.first, it.second)
    }

    /**
     * returns how much higher the top tile of a block is over its position x,y
     */
    val topOffset = getOccupied(0, 0).maxOfOrNull { it.second } ?: 0
    abstract fun next(): BlockType

    //####
    object HLine : BlockType() {
        override fun getOccupied(x: Int, y: Int) = listOf(
            x to y,
            x + 1 to y,
            x + 2 to y,
            x + 3 to y,
        )

        override fun next(): BlockType = Cross
    }

    //#
    //#
    //#
    //#
    object VLine : BlockType() {
        override fun getOccupied(x: Int, y: Int) = listOf(
            x to y,
            x to y + 1,
            x to y + 2,
            x to y + 3,
        )

        override fun next(): BlockType = Box
    }

    //##
    //##
    object Box : BlockType() {
        override fun getOccupied(x: Int, y: Int) = listOf(
            x to y,
            x + 1 to y,
            x to y + 1,
            x + 1 to y + 1,
        )

        override fun next(): BlockType = HLine
    }

    //.#.
    //###
    //.#.
    object Cross : BlockType() {
        override fun getOccupied(x: Int, y: Int) = listOf(
            x + 1 to y,
            x to y + 1,
            x + 1 to y + 1,
            x + 2 to y + 1,
            x + 1 to y + 2,
        )

        override fun next(): BlockType = LBlock
    }

    //..#
    //..#
    //###
    object LBlock : BlockType() {
        override fun getOccupied(x: Int, y: Int) = listOf(
            x to y,
            x + 1 to y,
            x + 2 to y,
            x + 2 to y + 1,
            x + 2 to y + 2,
        )

        override fun next(): BlockType = VLine
    }
}

lateinit var data: String

fun getMoves(length: Int): String {
    if (currentMove + length <= data.length) return data.substring(currentMove, currentMove + length)
    return data.takeLast(data.length - currentMove) + data.take(length - data.length + currentMove)
}

private var currentTop = 0
private fun getSpawnX() = precalculatedDrops[currentBlock to getMoves(4)]!!
private fun getSpawnY() = currentTop + 1
private var currentX = 0
private var currentY = 0
private var currentBlock: BlockType = BlockType.Box
private var currentBlockNum = 0L
private var currentMove: Int = 0
private var lastCycleBlock:BlockType? = null
private var lastCycleBlockNum = 0L
private var lastCycleTop = 0L
private var cycleHeight = 0L
private var cycleBlockNum = 0L
private val repeats = 1000000000000
private var skippedHeigh = 0L

private fun spawn(blockNumber:Long) {
    currentBlock = currentBlock.next()
    if (currentMove == 0) {
        if(lastCycleBlock == currentBlock){
            cycleBlockNum = blockNumber - lastCycleBlockNum
            cycleHeight = currentTop - lastCycleTop
            println("Found Cycle with height = $cycleHeight and blockCount = $cycleBlockNum")
            val cycles = (repeats- currentBlockNum)/cycleBlockNum
            val blocksDroppedWhileCycling = cycles * cycleBlockNum
            skippedHeigh = cycles * cycleHeight
            currentBlockNum += blocksDroppedWhileCycling
        }
        lastCycleBlock = currentBlock
        lastCycleBlockNum = blockNumber
        lastCycleTop = currentTop.toLong()
    }
    currentX = getSpawnX()
    currentY = getSpawnY()
    currentMove = (currentMove + 4) % data.length
}

private fun shift() {
    when (data[currentMove]) {
        '<' -> if (currentBlock.checkLeft(currentX, currentY)) currentX--
        else -> if (currentBlock.checkRight(currentX, currentY)) currentX++
    }
    currentMove = (currentMove + 1) % data.length
}

private fun drop(): Boolean {
    return if (currentBlock.checkUnder(currentX, currentY)) {
        currentY--
        true
    } else {
        currentBlock.stop(currentX, currentY)
        currentTop = max(currentY + currentBlock.topOffset, currentTop)
        false
    }
}

var precalculatedDrops = HashMap<Pair<BlockType, String>, Int>()

fun precalculateDrops() {
    val shifts = (0..15).map { it.toString(2).padStart(4, '0').replace("0", "<").replace("1", ">") }
    val blockTypes = listOf(
        BlockType.LBlock,
        BlockType.HLine,
        BlockType.Box,
        BlockType.VLine,
        BlockType.Cross,
    )
    blockTypes.forEach { block ->
        shifts.forEach { shiftPattern ->
            var x = 2
            for (char in shiftPattern) {
                when (char) {
                    '<' -> if (block.checkLeftSimple(x)) x--
                    else -> if (block.checkRightSimple(x)) x++
                }
                precalculatedDrops[block to shiftPattern] = x
            }
        }
    }
}

fun main() {
    data = File("src/main/kotlin/year2022/day17/input").readText()
    precalculateDrops()
    while(currentBlockNum < repeats)  {
        spawn(currentBlockNum)
        while (drop()) {
            shift()
        }
        currentBlockNum++
        if(currentBlockNum == 2022L) println(currentTop)

    }
   // printMap()
    println(currentTop + skippedHeigh)
}


fun printMap() {
    for (y in (0..shaft.rows.size).reversed()) {
        for (x in 0..6) {
            if (x to y in currentBlock.getOccupied(currentX, currentY)) print("@")
            else print(if (shaft.isEmpty(x, y)) "." else "#")
        }
        println()
    }
    println()
}
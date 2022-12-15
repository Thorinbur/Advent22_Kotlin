package year2022.day5

import java.io.File

val stacks9000: HashMap<Int, MutableList<Char>> = HashMap()
val stacks9001: HashMap<Int, MutableList<Char>> = HashMap()

data class Operation(
    val source: Int,
    val destination: Int,
    val quantity: Int,
){
    fun perform9000(stacks:HashMap<Int, MutableList<Char>>){
        stacks[destination]!!.addAll(stacks[source]!!.takeLast(quantity).reversed())
        stacks.put(source, stacks[source]!!.dropLast(quantity).toMutableList())
    }


    fun perform9001(stacks:HashMap<Int, MutableList<Char>>){
        stacks[destination]!!.addAll(stacks[source]!!.takeLast(quantity))
        stacks.put(source, stacks[source]!!.dropLast(quantity).toMutableList())
    }
}


fun main() {
    val data = File("src/main/kotlin/year2022/day5/input").readLines()

    loadInitialState(
        data.take(8)
            .reversed()
            .map { line ->
                line.chunked(4)
                    .map { crate ->
                        crate[1].takeIf { it != ' ' }
                    }
            }
    )

    val operations = data.drop(10).map {
        val (quantity, sourceAndDestination) = it.split(" from ")
        val (source, _ , destination) = sourceAndDestination.split(' ')
        Operation(source.toInt(), destination.toInt(), quantity.drop(5).toInt())
    }

    operations.forEach { it.perform9000(stacks9000) }
    operations.forEach { it.perform9001(stacks9001) }

    println(stacks9000.values.map{ it.last() }.joinToString (separator = ""))
    println(stacks9001.values.map{ it.last() }.joinToString (separator = ""))
}

fun loadInitialState(initialState: List<List<Char?>>) {
    initialState.forEach { line ->
        line.forEachIndexed { index, value ->
            if (value != null) (stacks9000.getOrPut(index + 1) { mutableListOf() }).add(value)
            if (value != null) (stacks9001.getOrPut(index + 1) { mutableListOf() }).add(value)
        }
    }
}


package year2022.day11

import java.io.File

const val truePrompt = "    If true: throw to monkey "
const val falsePrompt = "    If false: throw to monkey "
const val testPromp = "  Test: divisible by "
const val operationPrompt = "  Operation: new = "
const val startingItemsPrompt = "  Starting items: "

data class Item(var worryLevel: Long)

lateinit var monkeys: List<Monkey>
var worryLevelLimit: Long = 0

class Monkey(
    val inventory: MutableList<Item>,
    private val inspect: (Long) -> Long,
    val testDivisor: Long,
    private var targetMonkeyIfTrue: Int,
    private var targetMonkeyIfFalse: Int,
) {
    var itemInspections = 0L
    private fun throwItem(item: Item, monkey: Monkey) {
        monkey.inventory.add(item)
    }

    fun simulate(reliefFactor: Long) {
        inventory.forEach { item ->
            item.worryLevel = inspect(item.worryLevel)
            item.worryLevel = item.worryLevel % worryLevelLimit
            itemInspections++
            item.worryLevel /= reliefFactor
            val targetMonkeyNumber = if (item.worryLevel % testDivisor == 0L) targetMonkeyIfTrue
            else targetMonkeyIfFalse
            throwItem(item, monkeys[targetMonkeyNumber])
        }
        inventory.clear()
    }
}


fun main() {
    runSimulation(3, 20)
    runSimulation(1, 10000)
}

fun runSimulation(releifFactor: Long, rounds: Int) {
    parseInput()
    worryLevelLimit = monkeys.map { it.testDivisor }.reduce { acc, i -> acc * i }
    repeat(rounds) {
        monkeys.forEach { monkey ->
            monkey.simulate(releifFactor)
        }
    }
    val monkeyBusiness = monkeys.map { it.itemInspections }.sortedDescending().take(2).reduce { acc, i -> acc * i }
    println("monkey business for relief factor of $releifFactor after $rounds rounds is: $monkeyBusiness")
}

fun parseInput() {
    val data = File("src/main/kotlin/year2022/day11/input").readLines()

    val monkeyDescriptions = data.chunked(7)
    monkeys = monkeyDescriptions.map {
        parseMonkeyDescription(it)
    }
}

fun createAddOperation(parameter1: (Long) -> Long, parameter2: (Long) -> Long): (Long) -> Long =
    { old -> parameter1(old) + parameter2(old) }

fun createMultiplyOperation(parameter1: (Long) -> Long, parameter2: (Long) -> Long): (Long) -> Long =
    { old -> parameter1(old) * parameter2(old) }

fun parseOperationParam(param: String): (Long) -> Long {
    return if (param == "old") { old: Long -> old } else { _ -> param.toLong() }
}

fun parseMonkeyDescription(description: List<String>): Monkey {
    val items = description[1].drop(startingItemsPrompt.length).split(", ").map { Item(it.toLong()) }
    val operationDescription = description[2].drop(operationPrompt.length)
    val (param1, operator, param2) = operationDescription.split(" ")
    val operationParam1 = parseOperationParam(param1)
    val operationParam2 = parseOperationParam(param2)
    val operation = if (operator == "+") createAddOperation(operationParam1, operationParam2)
    else createMultiplyOperation(operationParam1, operationParam2)
    val testDivisor = description[3].drop(testPromp.length).toLong()
    val trueMonkey = description[4].drop(truePrompt.length).toInt()
    val falseMonkey = description[5].drop(falsePrompt.length).toInt()
    return Monkey(
        inventory = items.toMutableList(),
        inspect = operation,
        testDivisor = testDivisor,
        targetMonkeyIfTrue = trueMonkey,
        targetMonkeyIfFalse = falseMonkey,
    )
}

package year2022.day21

import java.io.File
import kotlin.IllegalStateException

private val monkeys = HashMap<String, Operation>()

private sealed class Operation() {
    abstract fun evaluate(): Long?
    abstract fun match(valueToMatch:Long)

    data class Reference(val monkeyId: String) : Operation() {
        override fun evaluate() = monkeys[monkeyId]!!.evaluate()
        override fun match(valueToMatch: Long) {
            monkeys[monkeyId]!!.match(valueToMatch)
        }
    }

    object Human : Operation() {
        override fun evaluate() = null
        override fun match(valueToMatch: Long) {
            println("value to match = $valueToMatch")
        }
    }

    class Root(val lParam: Operation, val rParam: Operation, val part1Operation:Operation) : Operation() {
        override fun evaluate() = part1Operation.evaluate()
        override fun match(valueToMatch: Long) {
           throw IllegalStateException("Root monkey should not be asked to match a value")
        }
        fun compare(){
            val leftAnswer = lParam.evaluate()
            val rightAnswer = rParam.evaluate()
            if(leftAnswer == null) lParam.match(rightAnswer!!)
            if(rightAnswer == null) rParam.match(leftAnswer!!)
        }
    }

    data class YellNumber(val value: Long) : Operation() {
        override fun evaluate() = value
        override fun match(valueToMatch: Long) {
         throw IllegalStateException("Yell monkeys should not be asked to match a value")
        }
    }

    class Sum(val lParam: Operation, val rParam: Operation) : Operation() {
        override fun evaluate(): Long? {
            val leftAnswer = lParam.evaluate()
            val rightAnswer = rParam.evaluate()
            return if (leftAnswer != null && rightAnswer != null) leftAnswer + rightAnswer else null
        }
        override fun match(valueToMatch: Long) {
            val leftAnswer = lParam.evaluate()
            val rightAnswer = rParam.evaluate()
            if(leftAnswer == null) lParam.match(valueToMatch - rightAnswer!!)
            if(rightAnswer == null) rParam.match(valueToMatch - leftAnswer!!)
        }
    }

    class Substract(val lParam: Operation, val rParam: Operation) : Operation() {
        override fun evaluate(): Long? {
            val leftAnswer = lParam.evaluate()
            val rightAnswer = rParam.evaluate()
            return if (leftAnswer != null && rightAnswer != null) leftAnswer - rightAnswer else null
        }
        override fun match(valueToMatch: Long) {
            val leftAnswer = lParam.evaluate()
            val rightAnswer = rParam.evaluate()
            if(leftAnswer == null) lParam.match(valueToMatch + rightAnswer!!)
            if(rightAnswer == null) rParam.match(leftAnswer!! - valueToMatch)
        }
    }

    class Multiply(val lParam: Operation, val rParam: Operation) : Operation() {
        override fun evaluate(): Long? {
            val leftAnswer = lParam.evaluate()
            val rightAnswer = rParam.evaluate()
            return if (leftAnswer != null && rightAnswer != null) leftAnswer * rightAnswer else null
        }
        override fun match(valueToMatch: Long) {
            val leftAnswer = lParam.evaluate()
            val rightAnswer = rParam.evaluate()
            if(leftAnswer == null) lParam.match(valueToMatch / rightAnswer!!)
            if(rightAnswer == null) rParam.match(valueToMatch / leftAnswer!!)
        }
    }

    class Divide(val lParam: Operation, val rParam: Operation) : Operation() {
        override fun evaluate(): Long? {
            val leftAnswer = lParam.evaluate()
            val rightAnswer = rParam.evaluate()
            return if (leftAnswer != null && rightAnswer != null) leftAnswer / rightAnswer else null
        }
        override fun match(valueToMatch: Long) {
            val leftAnswer = lParam.evaluate()
            val rightAnswer = rParam.evaluate()
            if(leftAnswer == null) lParam.match(valueToMatch * rightAnswer!!)
            if(rightAnswer == null) rParam.match(leftAnswer!! / valueToMatch)
        }
    }
}

fun main() {
    val input = File("src/main/kotlin/year2022/day21/input").readLines()
    input.forEach { line ->
        val (monkeyId, job) = line.split(": ")
        val operation = if (job.contains("+")) {
            val (lparam, rParam) = job.split(" + ")
            Operation.Sum(Operation.Reference(lparam), Operation.Reference(rParam))
        } else if (job.contains("-")) {
            val (lparam, rParam) = job.split(" - ")
            Operation.Substract(Operation.Reference(lparam), Operation.Reference(rParam))
        } else if (job.contains("*")) {
            val (lparam, rParam) = job.split(" * ")
            Operation.Multiply(Operation.Reference(lparam), Operation.Reference(rParam))
        } else if (job.contains("/")) {
            val (lparam, rParam) = job.split(" / ")
            Operation.Divide(Operation.Reference(lparam), Operation.Reference(rParam))
        } else Operation.YellNumber(job.toLong())
        monkeys[monkeyId] = operation
    }

    val rootOperation = monkeys["root"]!! as Operation.Sum
    val rootMonkey = Operation.Root(lParam = rootOperation.lParam, rParam = rootOperation.rParam, rootOperation)
    monkeys["root"] = rootMonkey

    //Part1
    println(rootMonkey.evaluate())

    //Part2
    monkeys["humn"] = Operation.Human
    rootMonkey.compare()
}

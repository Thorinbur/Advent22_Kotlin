package year2022.day16

import java.io.File
import java.util.*


val valves = hashMapOf<String, Valve>()
val importantValves = mutableListOf<Valve>()

data class Valve(val name: String, val flowRate: Int, val exits: List<String>) {
    var destinations: Map<Valve, Int> = hashMapOf()
}

val data = File("src/main/kotlin/year2022/day16/input").readLines()

fun main() {
    parseData()
    for (valve in valves.values) calculateDistances(valve)
    val startValve = valves["AA"]!!

    //PART 1:
    var soloStartState =
        State(0, 0, startValve, null, Int.MAX_VALUE, emptySet())
    val soloBestScore = calculateBestScore(soloStartState, 30)
    println("Part1: $soloBestScore")

    //PART 2:
    val elephantStartOptions = startValve.destinations.keys
    var duoStartStates = elephantStartOptions.map {
        State(0, 0, startValve, it, startValve.destinations[it]!! + 1, emptySet())
    }
    val bestDuoScore = duoStartStates.maxOfOrNull { calculateBestScore(it, 26) }
    println("Part2: $bestDuoScore")
}

fun parseData() {
    data.forEach { line ->
        val name = line.drop("Valve ".length).take(2)
        val rate = line.substringAfter("=").substringBefore(";").toInt()
        val exits = if (line.contains("valves")) line.substringAfter("valves ")
            .split(", ") else listOf(line.substringAfter("to valve "))

        val valve = Valve(name, rate, exits)
        valves[name] = valve
        if (rate > 0) importantValves.add(valve)
    }
}

fun calculateDistances(startValve: Valve) {
    val queue = LinkedList<Valve>()
    val distances = hashMapOf<Valve, Int>()
    distances[startValve] = 0

    queue.add(startValve)
    while (!queue.isEmpty()) {
        val currentValve = queue.pop()
        val neighbours = currentValve.exits.map { valves[it]!! }

        val distanceToCurrent = distances[currentValve] ?: Int.MAX_VALUE
        for (valve in neighbours) {
            val knownShortestPath = distances.getOrDefault(valve, Int.MAX_VALUE)
            if (knownShortestPath > distanceToCurrent + 1) {
                distances[valve] = distanceToCurrent + 1
                queue.add(valve)
            }
        }
    }
    startValve.destinations = distances.filter { it.key.flowRate > 0 }
}

private class State(
    val accumulatedFlow: Int,
    val elapsedTime: Int,
    val currentPosition: Valve,
    val otherDestination: Valve?,
    val otherTimeToDestination: Int,
    val openedValves: Set<Valve>
) {
    val currentFlowRate get() = openedValves.sumOf { it.flowRate }
    fun endScore(timeLimit: Int) = accumulatedFlow + ((timeLimit - elapsedTime) * currentFlowRate)


    fun canReach(destination: Valve, timeLimit: Int = 30): Boolean {
        return (elapsedTime + currentPosition.destinations[destination]!! < timeLimit) && otherDestination != destination
    }

    fun next(destination: Valve): State {
        val travelTime = currentPosition.destinations[destination]!! + 1
        if (otherTimeToDestination >= travelTime) {
            return State(
                accumulatedFlow + travelTime * currentFlowRate,
                elapsedTime + travelTime,
                destination,
                otherDestination,
                otherTimeToDestination - travelTime,
                openedValves + destination
            )
        } else {
            return State(
                accumulatedFlow + otherTimeToDestination * currentFlowRate,
                elapsedTime + otherTimeToDestination,
                otherDestination!!,
                destination,
                travelTime - otherTimeToDestination,
                openedValves + otherDestination,
            )
        }
    }

    fun reachOther(): State {
        return State(
            accumulatedFlow + otherTimeToDestination * currentFlowRate,
            elapsedTime + otherTimeToDestination,
            otherDestination!!,
            null,
            Int.MAX_VALUE,
            openedValves + otherDestination,
        )
    }
}

private fun calculateBestScore(state: State, timeLimit: Int): Int {
    val noMoveScore = state.endScore(timeLimit)
    val options =
        state.currentPosition.destinations.keys.filter { it !in state.openedValves && state.canReach(it, timeLimit) }
    return if (options.isEmpty()) {
        if (state.otherDestination != null){
            calculateBestScore(state.reachOther(), timeLimit)
        }
        else noMoveScore
    }
    else {
        options.map {
            calculateBestScore(state.next(it), timeLimit)
        }.max()
    }
}
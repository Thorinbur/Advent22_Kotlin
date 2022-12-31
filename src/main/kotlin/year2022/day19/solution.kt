package year2022.day19

import year2022.day19.Action.*
import java.io.File

data class Blueprint(
    val id: Int,
    val oreRobotOreCost: Int,
    val clayRobotOreCost: Int,
    val obsidianRobotOreCost: Int,
    val obsidianRobotClayCost: Int,
    val geodeRobotOreCost: Int,
    val geodeRobotObsidianCost: Int,
){
    val highestOreCost = listOf(oreRobotOreCost, clayRobotOreCost, obsidianRobotOreCost, geodeRobotOreCost).max()
    val highestClayCost = obsidianRobotClayCost
    val highestObsidianCost = geodeRobotObsidianCost
}

enum class Action {
    craftOreRobot, craftClayRobot, craftObsidianRobot, craftGeodeRobot, none
}

data class State(
    val ore: Int,
    val clay: Int,
    val obsidian: Int,
    val geodes: Int,
    val oreRobots: Int,
    val clayRobots: Int,
    val obsidianRobots: Int,
    val geodeRobots: Int,
) {
    fun geodesInStepsWithCurrentProduction(steps: Int) = geodeRobots * steps
    fun theoreticalMaxInSteps(steps: Int) = ((geodeRobots + (geodeRobots + steps))/2) * steps

    fun canPerformAction(action: Action, blueprint: Blueprint): Boolean {
        val oreMaxedOut = oreRobots >= blueprint.highestOreCost
        val clayMaxedOut = clayRobots >= blueprint.highestClayCost
        val obsidianMaxedOut = obsidianRobots >= blueprint.highestObsidianCost
        val canProduceGeodeRobot = ore >= blueprint.geodeRobotOreCost && obsidian >= blueprint.geodeRobotObsidianCost

        return when (action) {
            craftOreRobot -> !oreMaxedOut && ore >= blueprint.oreRobotOreCost
            craftClayRobot -> !clayMaxedOut && ore >= blueprint.clayRobotOreCost
            craftObsidianRobot -> !obsidianMaxedOut && ore >= blueprint.obsidianRobotOreCost && clay >= blueprint.obsidianRobotClayCost
            craftGeodeRobot -> canProduceGeodeRobot
            none -> (!oreMaxedOut || !clayMaxedOut || !obsidianMaxedOut) && !canProduceGeodeRobot
//            none -> true
        }
    }

    fun takeCost(action: Action, blueprint: Blueprint): State {
        return when (action) {
            craftOreRobot -> copy(ore = ore - blueprint.oreRobotOreCost)
            craftClayRobot -> copy(ore = ore - blueprint.clayRobotOreCost)
            craftObsidianRobot -> copy(
                ore = ore - blueprint.obsidianRobotOreCost,
                clay = clay - blueprint.obsidianRobotClayCost
            )

            craftGeodeRobot -> copy(
                ore = ore - blueprint.geodeRobotOreCost,
                obsidian = obsidian - blueprint.geodeRobotObsidianCost
            )

            none -> this
        }
    }

    fun harvest(): State {
        return copy(
            ore = ore + oreRobots,
            clay = clay + clayRobots,
            obsidian = obsidian + obsidianRobots,
            geodes = geodes + geodeRobots
        )
    }

    fun buildRobot(action: Action, blueprint: Blueprint): State {
        return when (action) {
            craftOreRobot -> copy(oreRobots = oreRobots + 1)
            craftClayRobot ->copy(clayRobots = clayRobots + 1)
            craftObsidianRobot -> copy(obsidianRobots = obsidianRobots + 1)
            craftGeodeRobot -> copy(geodeRobots = geodeRobots + 1)
            none -> this
        }.takeCost(action, blueprint)
    }

    fun proceed(action: Action, blueprint: Blueprint): State {
        val canPerformAction = canPerformAction(action, blueprint)
        val newState = harvest()
        return if(canPerformAction) newState.buildRobot(action, blueprint) else newState
    }
}

val blueprints = File("src/main/kotlin/year2022/day19/input").readLines().map { parseBlueprint(it) }

fun main() {
    println("===Part1===")
    //Part1:
    val result1 = blueprints.map { calculateBestScore(it, 24) * it.id}.sum()
    println("totalQualityLevel = $result1")
    println()
    println()
    println("===Part2===")
    //Part2:
    val result2 = blueprints.take(3).map { calculateBestScore(it, 32)}.reduce{acc, value -> acc * value}
    println("totalQualityLevel = $result2")
}

fun calculateBestScore(blueprint: Blueprint, minutes:Int):Int{
    val startState = State(0, 0, 0, 0, 1, 0, 0, 0)
    var states = setOf(startState)

    fun getTopScore() = states.maxBy { it.geodes }.geodes

    repeat(minutes){
        val remainingMinutes = minutes-it + 1
        states = states.flatMap {state -> Action.values().filter { state.canPerformAction(it, blueprint) }.map {action -> state.proceed(action, blueprint) } }.toSet()
        //trim branches that cannot catch up
        val currentLeaderProjection = states.map { it.geodesInStepsWithCurrentProduction(remainingMinutes) }.max()
        states = states.filter{it.theoreticalMaxInSteps(remainingMinutes) >= currentLeaderProjection}.toSet()
    }
    val bestScore =  getTopScore()
    println("Blueprint ${blueprint.id}: $bestScore")
    return bestScore
}

//Example:
//Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 20 clay. Each geode robot costs 2 ore and 12 obsidian.
fun parseBlueprint(string:String):Blueprint{
    val (blueprintIdString, blueprintDescription) = string.split(":")
    val id = blueprintIdString.drop("Blueprint ".length).toInt()
    val (oreRobotDescription, clayRobotDescription, obsidianRobotDescription, geodeRobotDescription) = blueprintDescription.split(".")
    val oreRobotOreCost = oreRobotDescription.drop(" Each ore robot costs ".length).dropLast(" ore".length).toInt()
    val clayRobotOreCost = clayRobotDescription.drop(" Each clay robot costs ".length).dropLast(" ore".length).toInt()
    val obsidianRobotOreCost = obsidianRobotDescription.drop(" Each obsidian robot costs ".length).substringBefore(" ore").toInt()
    val obsidianRobotClayCost = obsidianRobotDescription.substringAfter(" and ").substringBefore(" clay").toInt()
    val geodeRobotOreCost = geodeRobotDescription.substringAfter("costs ").substringBefore(" ore").toInt()
    val geodeRobotObsidianCost = geodeRobotDescription.substringAfter(" and ").substringBefore(" obsidian").toInt()
    return Blueprint(
        id,
        oreRobotOreCost,
        clayRobotOreCost,
        obsidianRobotOreCost,
        obsidianRobotClayCost,
        geodeRobotOreCost,
        geodeRobotObsidianCost
    )
}
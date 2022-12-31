package year2022.day15

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SolutionTest {
    //.#.
    //#SB
    //.#.
    @Test
    fun testRadiusSideBySideHorizontally() {
        val sensor = Coordinates(0, 0)
        val beacon = Coordinates(1, 0)

        assertEquals(1, getRadius(sensor, beacon))
    }

    //.B.
    //#S#
    //.#.
    @Test
    fun testRadiusSideBySideVertically() {
        val sensor = Coordinates(0, 0)
        val beacon = Coordinates(0, 1)

        assertEquals(1, getRadius(sensor, beacon))
    }

    //..#..
    //.##B.
    //##S##
    //.###.
    //..#..
    @Test
    fun testRadiusSideBySideDiagonally() {
        val sensor = Coordinates(0, 0)
        val beacon = Coordinates(1, 1)

        assertEquals(2, getRadius(sensor, beacon))
    }


    //.....#.....
    //....###....
    //...#####...
    //..######B..
    //.#########.
    //#####S#####
    //.#########.
    //..#######..
    //...#####...
    //....###....
    //.....#.....
    @Test
    fun testRadiusDiagonally() {
        val sensor = Coordinates(0, 0)
        val beacon = Coordinates(3, 2)

        assertEquals(5, getRadius(sensor, beacon))
    }

    @Test
    fun testRadiusDataSet() {
        val sensor = Coordinates(2, 18)
        val beacon = Coordinates(-2, 15)

        assertEquals(7, getRadius(sensor, beacon))
    }

    //.#.
    //#O#
    //.#.
    //======

    @Test
    fun testNoIntersection() {
        val origin = Coordinates(0, 0)
        val radius = 1
        val targetY = 2

        assertEquals(0, getIntersectingRangeLength(origin, radius, targetY))
    }

    //..#..
    //.###.
    //##O##
    //.###.
    //==#=====
    //
    @Test
    fun testIntersection() {
        val origin = Coordinates(0, 0)
        val radius = 2
        val targetY = 2

        assertEquals(1, getIntersectingRangeLength(origin, radius, targetY))
    }

    @Test
    fun testIntersectionDataSet() {
        val origin = Coordinates(2, 18)
        val radius = 7
        val targetY = 10

        assertEquals(0, getIntersectingRangeLength(origin, radius, targetY))
    }

    @Test
    fun testIntersectionNonZeroOrigin() {
        val origin = Coordinates(0, -5)
        val radius = 7
        val targetY = 2

        assertEquals(1, getIntersectingRangeLength(origin, radius, targetY))
    }

    @Test
    fun testIntersectionNonZeroOriginTargetAbove() {
        val origin = Coordinates(0, -5)
        val radius = 3
        val targetY = -7

        assertEquals(3, getIntersectingRangeLength(origin, radius, targetY))
    }
}
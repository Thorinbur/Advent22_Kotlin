package year2022.day25

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SolutionKtTest {
    @Test
    fun `decode single digit`() {
        assertEquals(0, decode("0"))
        assertEquals(1, decode("1"))
        assertEquals(2, decode("2"))
    }

    @Test
    fun `decode tens`() {
        assertEquals(5, decode("10"))
        assertEquals(6, decode("11"))
        assertEquals(7, decode("12"))
    }

    @Test
    fun `decode negatives`() {
        assertEquals(3, decode("1="))
        assertEquals(4, decode("1-"))
    }

    @Test
    fun `decode examples`() {
        assertEquals(8, decode("2="))
        assertEquals(9, decode("2-"))
        assertEquals(10, decode("20"))
        assertEquals(15, decode("1=0"))
        assertEquals(20, decode("1-0"))
        assertEquals(12345, decode("1-0---0"))
        assertEquals(314159265, decode("1121-1110-1=0"))
    }

    @Test
    fun `encode single digit`() {
        assertEquals("0", encode(0L))
        assertEquals("1", encode(1L))
        assertEquals("2", encode(2L))
    }

    @Test
    fun `encode tens`() {
        assertEquals("10", encode(5L))
        assertEquals("11", encode(6L))
        assertEquals("12", encode(7L))
    }

    @Test
    fun `encode negatives`() {
        assertEquals("1=", encode(3L))
        assertEquals("1-", encode(4L))
    }

    @Test
    fun `encode examples`() {
        assertEquals("2=", encode(8L))
        assertEquals("2-", encode(9L))
        assertEquals("20", encode(10L))
        assertEquals("1=0", encode(15L))
        assertEquals("1-0", encode(20L))
        assertEquals("1-0---0", encode(12345L))
        assertEquals("1121-1110-1=0", encode(314159265L))
    }

}
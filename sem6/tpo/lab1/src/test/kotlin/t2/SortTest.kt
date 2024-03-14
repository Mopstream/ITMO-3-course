package t2

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.random.Random

class SortTest {

    @ParameterizedTest(name = "sort({0})")
    @DisplayName("Checking simple arrays")
    @MethodSource("getTestingArgs")
    fun checkSort(arr: IntArray) {
        InsertionSort.sort(arr)
        assertTrue(arr.toList().zipWithNext { i, j -> i <= j }.all { it })
    }


    @Test
    @DisplayName("Checking empty array")
    fun checkEmpty() {
        val a: IntArray = intArrayOf()
        InsertionSort.sort(a)
        assertArrayEquals(a, intArrayOf())
    }

    @Test
    @DisplayName("Checking throw exception on null")
    fun checkThrows() {
        assertThrows<NullPointerException> { InsertionSort.sort(null) }
    }

    @Test
    @DisplayName("Checking single value array")
    fun checkSingle() {
        val v: Int = Random.nextInt(-1000, 1000)
        val a = IntArray(100) { v }
        print(a.toList())
        InsertionSort.sort(a)
        assertArrayEquals(a, IntArray(100) { v })
    }

    companion object {
        @JvmStatic
        fun getTestingArgs(): Iterable<IntArray> {
            return generateSequence {
                IntArray(Random.nextInt(0, 1000)) {
                    Random.nextInt(-1000, 1000)
                }
            }.take(1000).asIterable()
        }
    }
}
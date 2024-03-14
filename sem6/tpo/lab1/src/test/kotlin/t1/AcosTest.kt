package t1


import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.provider.CsvFileSource
import org.junit.jupiter.params.provider.MethodSource
import scuf.mopstream.t1.Acos
import kotlin.math.acos


class AcosTest {

    @ParameterizedTest(name = "acos({0})")
    @DisplayName("Checking simple points")
    @MethodSource("getTestingArgs")
    fun check(x: Double) {
        assertEquals(acos(x), Acos.calc(x, 1000), 0.001)
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/test.csv"], numLinesToSkip = 0, delimiter = ',')
    fun testFromFile(input: Double, expected: Double) {
        val actual:Double = Acos.calc(input, 1000);
        assertEquals(expected, actual, 0.0001);
    }


    companion object {
        @JvmStatic
        fun getTestingArgs(): Iterable<Double> {
            return generateSequence(-1.2) { (it + 0.01) }.takeWhile { it < 1.2 }.asIterable()
        }
    }

}
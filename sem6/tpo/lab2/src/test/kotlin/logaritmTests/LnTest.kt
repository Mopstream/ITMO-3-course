package logaritmTests

import scuf.mopstream.logarithm.Ln
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class LnTest {
    @ParameterizedTest
    @MethodSource("provideTestData")
    fun testLn(x: Double, expected: Double) {
        val actual = ln.calculate(x)

        assertEquals(expected, actual, DELTA)
    }

    @Test
    fun throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException::class.java) {
            ln.calculate(-1.0)
        }

        assertThrows(IllegalArgumentException::class.java) {
            Ln(-0.01)
        }
    }

    companion object {
        private const val DELTA = 0.001
        private val ln = Ln(DELTA * 0.01)

        @JvmStatic
        fun provideTestData() =
            mapOf(
                0.0 to Double.NEGATIVE_INFINITY,
                0.5 to -0.69315,
                // пересечение с осью OX
                1.0 to 0.0,
                1.4 to 0.33647223662121295,
                2.0 to 0.6931471805599453,
                5.0 to 1.609,
                // проверка для x>2
                3.0 to 1.098612,
                100.0 to 4.60517,
                Double.NaN to Double.NaN,
                Double.POSITIVE_INFINITY to Double.POSITIVE_INFINITY,
            )
                .map { Arguments.of(it.key, it.value) }
    }
}
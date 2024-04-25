package trigonomTests

import scuf.mopstream.trigonom.Sin
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.lang.Math
import kotlin.math.sqrt

class SinTest {

    @ParameterizedTest
    @MethodSource("provideTestData")
    fun testSin(x: Double, expected: Double) {
        val actual = sin.calculate(x)
        assertEquals(expected, actual, DELTA)
    }

    companion object {
        private const val DELTA = 0.00001
        private val sin = Sin(DELTA)

        @JvmStatic
        fun provideTestData() =
            mapOf(
                // проверка нулевого значения
                0.0 to 0.0,
                // проверка правой стороны
                Math.PI / 6 to 0.5,
                Math.PI / 4 to sqrt(2.0) / 2,
                Math.PI / 3 to sqrt(3.0) / 2,
                Math.PI / 2 to 1.0,
                2 * Math.PI / 3 to sqrt(3.0) / 2,
                3 * Math.PI / 4 to sqrt(2.0) / 2,
                5 * Math.PI / 6 to 0.5,
                // проверка левой стороны (на четность)
                -Math.PI / 6 to -0.5,
                -Math.PI / 4 to -sqrt(2.0) / 2,
                -Math.PI / 3 to -sqrt(3.0) / 2,
                -Math.PI / 2 to -1.0,
                -2 * Math.PI / 3 to -sqrt(3.0) / 2,
                -3 * Math.PI / 4 to -sqrt(2.0) / 2,
                -5 * Math.PI / 6 to -0.5,
                // проверка граничных значений
                -Math.PI to 0.0,
                Math.PI to 0.0,
                // тестирование NaN, Infinity
                Double.NaN to Double.NaN,
                Double.POSITIVE_INFINITY to Double.NaN,
                Double.NEGATIVE_INFINITY to Double.NaN,
                // тестовые значения за границами -2pi ; 2pi
                7 * Math.PI / 6 to -0.5,
                -7 * Math.PI / 6 to 0.5
            )
                .map { Arguments.of(it.key, it.value) }
    }
}
package trigonomTests

import scuf.mopstream.trigonom.Cos
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito
import scuf.mopstream.trigonom.Sin
import scuf.mopstream.trigonom.Tan

class TanTest {
    companion object {
        private const val EPS = 0.01
        private val sin = Mockito.mock(Sin::class.java)
        private val cos = Mockito.mock(Cos::class.java)
        private lateinit var tan: Tan

        @BeforeAll
        @JvmStatic
        fun setUp() {
            Mockito.`when`(sin.calculate(Math.PI / 2)).thenReturn(1.0)
            Mockito.`when`(sin.calculate(-Math.PI / 2)).thenReturn(-1.0)
            Mockito.`when`(sin.calculate(Math.PI / 6)).thenReturn(0.5)
            Mockito.`when`(sin.calculate(Math.PI / 4)).thenReturn(0.707106781)
            Mockito.`when`(sin.calculate(-Math.PI / 4)).thenReturn(-0.707106781)
            Mockito.`when`(sin.calculate(Math.PI / 3)).thenReturn(0.866025404)
            Mockito.`when`(sin.calculate(2 * Math.PI / 3)).thenReturn(0.866025404)
            Mockito.`when`(sin.calculate(Math.PI)).thenReturn(0.0)
            Mockito.`when`(sin.calculate(3 * Math.PI / 2)).thenReturn(-1.0)
            Mockito.`when`(sin.calculate(2 * Math.PI)).thenReturn(0.0)
            Mockito.`when`(sin.calculate(5 * Math.PI / 2)).thenReturn(1.0)
            Mockito.`when`(sin.calculate(0.0)).thenReturn(0.0)
            Mockito.`when`(sin.calculate(Double.NaN)).thenReturn(Double.NaN)
            Mockito.`when`(sin.calculate(Double.POSITIVE_INFINITY)).thenReturn(Double.NaN)
            Mockito.`when`(sin.calculate(Double.NEGATIVE_INFINITY)).thenReturn(Double.NaN)

            Mockito.`when`(sin.calculate(Math.PI / 3 + Math.PI / 2)).thenReturn(0.5)
            Mockito.`when`(sin.calculate(Math.PI / 4 + Math.PI / 2)).thenReturn(0.70711)
            Mockito.`when`(sin.calculate(-Math.PI / 4 + Math.PI / 2)).thenReturn(0.70711)
            Mockito.`when`(sin.calculate(-Math.PI / 3 + Math.PI / 2)).thenReturn(0.5)

            Mockito.`when`(sin.calculate(8 * Math.PI)).thenReturn(0.0)
            Mockito.`when`(sin.calculate(3 * Math.PI)).thenReturn(0.0)
            Mockito.`when`(sin.calculate(-Math.PI)).thenReturn(0.0)
            Mockito.`when`(sin.calculate(-8 * Math.PI)).thenReturn(0.0)

            Mockito.`when`(cos.calculate(Math.PI / 2)).thenReturn(0.0)
            Mockito.`when`(cos.calculate(-Math.PI / 2)).thenReturn(0.0)
            Mockito.`when`(cos.calculate(Math.PI / 6)).thenReturn(0.866025404)
            Mockito.`when`(cos.calculate(Math.PI / 4)).thenReturn(0.707106781)
            Mockito.`when`(cos.calculate(-Math.PI / 4)).thenReturn(0.707106781)
            Mockito.`when`(cos.calculate(Math.PI / 3)).thenReturn(0.5)
            Mockito.`when`(cos.calculate(2 * Math.PI / 3)).thenReturn(-0.5)
            Mockito.`when`(cos.calculate(Math.PI)).thenReturn(-1.0)
            Mockito.`when`(cos.calculate(3 * Math.PI / 2)).thenReturn(0.0)
            Mockito.`when`(cos.calculate(2 * Math.PI)).thenReturn(1.0)
            Mockito.`when`(cos.calculate(5 * Math.PI / 2)).thenReturn(0.0)
            Mockito.`when`(cos.calculate(0.0)).thenReturn(1.0)
            Mockito.`when`(cos.calculate(Double.NaN)).thenReturn(Double.NaN)
            Mockito.`when`(cos.calculate(Double.POSITIVE_INFINITY)).thenReturn(Double.NaN)
            Mockito.`when`(cos.calculate(Double.NEGATIVE_INFINITY)).thenReturn(Double.NaN)

            Mockito.`when`(cos.calculate(Math.PI / 3 + Math.PI / 2)).thenReturn(-0.866025404)
            Mockito.`when`(cos.calculate(Math.PI / 4 + Math.PI / 2)).thenReturn(-0.70711)
            Mockito.`when`(cos.calculate(-Math.PI / 4 + Math.PI / 2)).thenReturn(0.70711)
            Mockito.`when`(cos.calculate(-Math.PI / 3 + Math.PI / 2)).thenReturn(0.866025404)

            Mockito.`when`(cos.calculate(3 * Math.PI)).thenReturn(-1.0)
            Mockito.`when`(cos.calculate(-Math.PI)).thenReturn(-1.0)
            Mockito.`when`(cos.calculate(8 * Math.PI)).thenReturn(1.0)
            Mockito.`when`(cos.calculate(-8 * Math.PI)).thenReturn(1.0)

            tan = Tan(sin, cos)
        }

        @JvmStatic
        fun provideTestData() =
            listOf(
                Arguments.of(Math.PI / 6, 0.577350269),
                Arguments.of(Math.PI / 4, 1.0),
                Arguments.of(-Math.PI / 4, -1.0),
                Arguments.of(Math.PI / 3, 1.732050808),
                Arguments.of(2 * Math.PI / 3, -1.732050808),
                Arguments.of(3 * Math.PI, 0.0),
                Arguments.of(-Math.PI, 0.0),
                Arguments.of(Double.NaN, Double.NaN),
                Arguments.of(Double.POSITIVE_INFINITY, Double.NaN),
                Arguments.of(Double.NEGATIVE_INFINITY, Double.NaN),

                Arguments.of(8 * Math.PI, 0.0),
                Arguments.of(-8 * Math.PI, 0.0),
            )

    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    fun `tan test`(x: Double, excepted: Double) {
        Assertions.assertEquals(excepted, tan.calculate(x), EPS)
    }

    @Test
    fun throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException::class.java) {
            tan.calculate(Math.PI / 2)
        }

        assertThrows(IllegalArgumentException::class.java) {
            tan.calculate(-Math.PI / 2)
        }
    }

}
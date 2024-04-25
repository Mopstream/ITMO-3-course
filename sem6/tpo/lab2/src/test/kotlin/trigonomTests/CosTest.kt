package trigonomTests

import scuf.mopstream.trigonom.Cos
import scuf.mopstream.trigonom.Sin
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito

class CosTest {
    companion object {
        private const val EPS = 0.01
        private val sin = Mockito.mock(Sin::class.java)
        private lateinit var cos : Cos

        @BeforeAll
        @JvmStatic
        fun setUp(){
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

            Mockito.`when`(sin.calculate(8 * Math.PI + Math.PI / 2)).thenReturn(1.0)
            Mockito.`when`(sin.calculate(-8 * Math.PI + Math.PI / 2)).thenReturn(1.0)

            cos = Cos(sin)
        }

        @JvmStatic
        fun provideTestData() =
            listOf(
                Arguments.of(0.0, 1),
                Arguments.of(Math.PI / 2, 0.0),
                Arguments.of(Math.PI, -1.0),
                Arguments.of(3 * Math.PI / 2, 0.0),
                Arguments.of(2 * Math.PI, 1.0),
                Arguments.of(-Math.PI / 2, 0.0),
                Arguments.of(-Math.PI, -1.0),
                Arguments.of(Double.NaN, Double.NaN),
                Arguments.of(Double.POSITIVE_INFINITY, Double.NaN),
                Arguments.of(Double.NEGATIVE_INFINITY, Double.NaN),

                Arguments.of(Math.PI / 3, 0.5),
                Arguments.of(Math.PI / 4, 0.70711),
                Arguments.of(-Math.PI / 4, 0.70711),
                Arguments.of(-Math.PI / 3, 0.5),

                Arguments.of(8 * Math.PI, 1.0),
                Arguments.of(-8 * Math.PI, 1.0),
            )
    }


    @ParameterizedTest
    @MethodSource("provideTestData")
    fun `cos test`(x: Double, excepted: Double){
        assertEquals(excepted, cos.calculate(x), EPS)
    }
}
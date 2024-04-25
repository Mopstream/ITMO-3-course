package functionSystemTests

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito
import scuf.mopstream.functions.TrigonomPart
import scuf.mopstream.trigonom.Cos
import scuf.mopstream.trigonom.Cot
import scuf.mopstream.trigonom.Sec
import scuf.mopstream.trigonom.Tan

class TrigonomTest {
    companion object {
        private const val EPS = 0.01
        private val sec = Mockito.mock(Sec::class.java)
        private val cos = Mockito.mock(Cos::class.java)
        private val cot = Mockito.mock(Cot::class.java)
        private val tan = Mockito.mock(Tan::class.java)

        private lateinit var trig: TrigonomPart

        @BeforeAll
        @JvmStatic
        fun setUp() {
            Mockito.`when`(sec.calculate(0.0)).thenReturn(1.0)
            Mockito.`when`(sec.calculate(-Math.PI / 6)).thenReturn(1.154700538)
            Mockito.`when`(sec.calculate(-Math.PI / 4)).thenReturn(1.414213562)
            Mockito.`when`(sec.calculate(-Math.PI / 3)).thenReturn(2.0)
            Mockito.`when`(sec.calculate(-Math.PI / 2)).thenThrow(IllegalArgumentException::class.java)
            Mockito.`when`(sec.calculate(-2 * Math.PI / 3)).thenReturn(-2.0)
            Mockito.`when`(sec.calculate(-3 * Math.PI / 4)).thenReturn(-1.414213562)
            Mockito.`when`(sec.calculate(-5 * Math.PI / 6)).thenReturn(-1.154700538)
            Mockito.`when`(sec.calculate(-Math.PI)).thenReturn(-1.0)
            Mockito.`when`(sec.calculate(-7 * Math.PI / 6)).thenReturn(-1.154700538)
            Mockito.`when`(sec.calculate(-5 * Math.PI / 4)).thenReturn(-1.414213562)
            Mockito.`when`(sec.calculate(-4 * Math.PI / 3)).thenReturn(-2.0)
            Mockito.`when`(sec.calculate(-3 * Math.PI / 2)).thenThrow(IllegalArgumentException::class.java)
            Mockito.`when`(sec.calculate(-5 * Math.PI / 3)).thenReturn(2.0)
            Mockito.`when`(sec.calculate(-7 * Math.PI / 4)).thenReturn(1.414213562)
            Mockito.`when`(sec.calculate(-11 * Math.PI / 6)).thenReturn(1.154700538)
            Mockito.`when`(sec.calculate(Double.NaN)).thenReturn(Double.NaN)
            Mockito.`when`(sec.calculate(Double.POSITIVE_INFINITY)).thenReturn(Double.NaN)
            Mockito.`when`(sec.calculate(Double.NEGATIVE_INFINITY)).thenReturn(Double.NaN)

            Mockito.`when`(cos.calculate(0.0)).thenReturn(1.0)
            Mockito.`when`(cos.calculate(-Math.PI / 6)).thenReturn(0.866025404)
            Mockito.`when`(cos.calculate(-Math.PI / 4)).thenReturn(0.707106781)
            Mockito.`when`(cos.calculate(-Math.PI / 3)).thenReturn(0.5)
            Mockito.`when`(cos.calculate(-Math.PI / 2)).thenReturn(0.0)
            Mockito.`when`(cos.calculate(-2 * Math.PI / 3)).thenReturn(-0.5)
            Mockito.`when`(cos.calculate(-3 * Math.PI / 4)).thenReturn(-0.707106781)
            Mockito.`when`(cos.calculate(-5 * Math.PI / 6)).thenReturn(-0.866025404)
            Mockito.`when`(cos.calculate(-Math.PI)).thenReturn(-1.0)
            Mockito.`when`(cos.calculate(-7 * Math.PI / 6)).thenReturn(-0.866025404)
            Mockito.`when`(cos.calculate(-5 * Math.PI / 4)).thenReturn(-0.707106781)
            Mockito.`when`(cos.calculate(-4 * Math.PI / 3)).thenReturn(-0.5)
            Mockito.`when`(cos.calculate(-3 * Math.PI / 2)).thenReturn(0.0)
            Mockito.`when`(cos.calculate(-5 * Math.PI / 3)).thenReturn(0.5)
            Mockito.`when`(cos.calculate(-7 * Math.PI / 4)).thenReturn(0.707106781)
            Mockito.`when`(cos.calculate(-11 * Math.PI / 6)).thenReturn(0.866025404)
            Mockito.`when`(cos.calculate(Double.NaN)).thenReturn(Double.NaN)
            Mockito.`when`(cos.calculate(Double.POSITIVE_INFINITY)).thenReturn(Double.NaN)
            Mockito.`when`(cos.calculate(Double.NEGATIVE_INFINITY)).thenReturn(Double.NaN)

            Mockito.`when`(cot.calculate(0.0)).thenThrow(IllegalArgumentException::class.java)
            Mockito.`when`(cot.calculate(-Math.PI / 6)).thenReturn(-1.732050808)
            Mockito.`when`(cot.calculate(-Math.PI / 4)).thenReturn(-1.0)
            Mockito.`when`(cot.calculate(-Math.PI / 3)).thenReturn(-0.577350269)
            Mockito.`when`(cot.calculate(-Math.PI / 2)).thenReturn(0.0)
            Mockito.`when`(cot.calculate(-2 * Math.PI / 3)).thenReturn(0.577350269)
            Mockito.`when`(cot.calculate(-3 * Math.PI / 4)).thenReturn(1.0)
            Mockito.`when`(cot.calculate(-5 * Math.PI / 6)).thenReturn(1.732050808)
            Mockito.`when`(cot.calculate(-Math.PI)).thenThrow(IllegalArgumentException::class.java)
            Mockito.`when`(cot.calculate(-7 * Math.PI / 6)).thenReturn(-1.732050808)
            Mockito.`when`(cot.calculate(-5 * Math.PI / 4)).thenReturn(-1.0)
            Mockito.`when`(cot.calculate(-4 * Math.PI / 3)).thenReturn(-0.577350269)
            Mockito.`when`(cot.calculate(-3 * Math.PI / 2)).thenReturn(0.0)
            Mockito.`when`(cot.calculate(-5 * Math.PI / 3)).thenReturn(0.577350269)
            Mockito.`when`(cot.calculate(-7 * Math.PI / 4)).thenReturn(1.0)
            Mockito.`when`(cot.calculate(-11 * Math.PI / 6)).thenReturn(1.732050808)
            Mockito.`when`(cot.calculate(Double.NaN)).thenReturn(Double.NaN)
            Mockito.`when`(cot.calculate(Double.POSITIVE_INFINITY)).thenReturn(Double.NaN)
            Mockito.`when`(cot.calculate(Double.NEGATIVE_INFINITY)).thenReturn(Double.NaN)

            Mockito.`when`(tan.calculate(0.0)).thenReturn(0.0)
            Mockito.`when`(tan.calculate(-Math.PI / 6)).thenReturn(-0.577350269)
            Mockito.`when`(tan.calculate(-Math.PI / 4)).thenReturn(-1.0)
            Mockito.`when`(tan.calculate(-Math.PI / 3)).thenReturn(-1.732050808)
            Mockito.`when`(tan.calculate(-Math.PI / 2)).thenThrow(IllegalArgumentException::class.java)
            Mockito.`when`(tan.calculate(-2 * Math.PI / 3)).thenReturn(1.732050808)
            Mockito.`when`(tan.calculate(-3 * Math.PI / 4)).thenReturn(1.0)
            Mockito.`when`(tan.calculate(-5 * Math.PI / 6)).thenReturn(0.577350269)
            Mockito.`when`(tan.calculate(-Math.PI)).thenReturn(0.0)
            Mockito.`when`(tan.calculate(-7 * Math.PI / 6)).thenReturn(-0.577350269)
            Mockito.`when`(tan.calculate(-5 * Math.PI / 4)).thenReturn(-1.0)
            Mockito.`when`(tan.calculate(-4 * Math.PI / 3)).thenReturn(-1.732050808)
            Mockito.`when`(tan.calculate(-3 * Math.PI / 2)).thenThrow(IllegalArgumentException::class.java)
            Mockito.`when`(tan.calculate(-5 * Math.PI / 3)).thenReturn(1.732050808)
            Mockito.`when`(tan.calculate(-7 * Math.PI / 4)).thenReturn(1.0)
            Mockito.`when`(tan.calculate(-11 * Math.PI / 6)).thenReturn(0.577350269)
            Mockito.`when`(tan.calculate(Double.NaN)).thenReturn(Double.NaN)
            Mockito.`when`(tan.calculate(Double.POSITIVE_INFINITY)).thenReturn(Double.NaN)
            Mockito.`when`(tan.calculate(Double.NEGATIVE_INFINITY)).thenReturn(Double.NaN)

            trig = TrigonomPart(sec, cos, cot, tan)
        }

        @JvmStatic
        fun provideTestData() =
            listOf(
                Arguments.of(-Math.PI / 6, 5.1366075),
                Arguments.of(-Math.PI / 4, 15.658639),
                Arguments.of(-Math.PI / 3, 466.25462),
                Arguments.of(-2 * Math.PI / 3, -594.3287),
                Arguments.of(-3 * Math.PI / 4, -65.658639),
                Arguments.of(-5 * Math.PI / 6, -101.803274),
                Arguments.of(-7 * Math.PI / 6, -101.803274),
                Arguments.of(-5 * Math.PI / 4, -65.658639),
                Arguments.of(-4 * Math.PI / 3, -594.3287),
                Arguments.of(-5 * Math.PI / 3, 466.25462),
                Arguments.of(-7 * Math.PI / 4, 15.658639),
                Arguments.of(-11 * Math.PI / 6, 5.1366075),
                Arguments.of(Double.NaN, Double.NaN),
                Arguments.of(Double.POSITIVE_INFINITY, Double.NaN),
                Arguments.of(Double.NEGATIVE_INFINITY, Double.NaN),
            )

    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    fun `trig part test`(x: Double, excepted: Double) {
        Assertions.assertEquals(excepted, trig.calculate(x), EPS)
    }

    @Test
    fun throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException::class.java) {
            trig.calculate(0.0)
        }

        assertThrows(IllegalArgumentException::class.java) {
            trig.calculate(-Math.PI / 2)
        }

        assertThrows(IllegalArgumentException::class.java) {
            trig.calculate(-Math.PI)
        }

        assertThrows(IllegalArgumentException::class.java) {
            trig.calculate(-3 * Math.PI / 2)
        }
    }
}
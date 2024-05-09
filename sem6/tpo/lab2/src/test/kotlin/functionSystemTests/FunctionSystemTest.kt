package functionSystemTests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito
import scuf.mopstream.functions.FuncSystem
import scuf.mopstream.functions.LogarithmPart
import scuf.mopstream.functions.TrigonomPart

class FunctionSystemTest {
    companion object {
        private const val eps = 0.001
        private val logPart = Mockito.mock(LogarithmPart::class.java)
        private val trigPart = Mockito.mock(TrigonomPart::class.java)
        private lateinit var res: FuncSystem

        @BeforeAll
        @JvmStatic
        fun setUp() {
            Mockito.`when`(logPart.calculate(0.5)).thenReturn(0.036753)
            Mockito.`when`(logPart.calculate(1.0)).thenThrow(java.lang.IllegalArgumentException::class.java)
            Mockito.`when`(logPart.calculate(2.0)).thenReturn(0.147249)
            Mockito.`when`(logPart.calculate(3.0)).thenReturn(1.887681)
            Mockito.`when`(logPart.calculate(4.0)).thenReturn(7.041052)
            Mockito.`when`(logPart.calculate(5.0)).thenReturn(16.492265)
            Mockito.`when`(logPart.calculate(10.0)).thenReturn(130.55357)
            Mockito.`when`(logPart.calculate(Double.NaN)).thenReturn(Double.NaN)
            Mockito.`when`(logPart.calculate(Double.POSITIVE_INFINITY)).thenReturn(Double.NaN)

            Mockito.doReturn(-1).`when`(logPart).calculate(Mockito.eq(12.0))

            Mockito.`when`(trigPart.calculate(0.0)).thenThrow(java.lang.IllegalArgumentException::class.java)
            Mockito.`when`(trigPart.calculate(-Math.PI / 6)).thenReturn(5.1366075)
            Mockito.`when`(trigPart.calculate(-Math.PI / 4)).thenReturn(15.658639)
            Mockito.`when`(trigPart.calculate(-Math.PI / 3)).thenReturn(466.25462)
            Mockito.`when`(trigPart.calculate(-Math.PI / 2)).thenThrow(java.lang.IllegalArgumentException::class.java)
            Mockito.`when`(trigPart.calculate(-2 * Math.PI / 3)).thenReturn(-594.3287)
            Mockito.`when`(trigPart.calculate(-3 * Math.PI / 4)).thenReturn(-65.658639)
            Mockito.`when`(trigPart.calculate(-5 * Math.PI / 6)).thenReturn(-101.803274)
            Mockito.`when`(trigPart.calculate(-Math.PI)).thenThrow(java.lang.IllegalArgumentException::class.java)
            Mockito.`when`(trigPart.calculate(-7 * Math.PI / 6)).thenReturn(-101.803274)
            Mockito.`when`(trigPart.calculate(-5 * Math.PI / 4)).thenReturn(-65.658639)
            Mockito.`when`(trigPart.calculate(-4 * Math.PI / 3)).thenReturn(-594.3287)
            Mockito.`when`(trigPart.calculate(-3 * Math.PI / 2)).thenThrow(java.lang.IllegalArgumentException::class.java)
            Mockito.`when`(trigPart.calculate(-5 * Math.PI / 3)).thenReturn(466.25462)
            Mockito.`when`(trigPart.calculate(-7 * Math.PI / 4)).thenReturn(15.658639)
            Mockito.`when`(trigPart.calculate(-11 * Math.PI / 6)).thenReturn(5.1366075)
            Mockito.`when`(trigPart.calculate(Double.NaN)).thenReturn(Double.NaN)
            Mockito.`when`(trigPart.calculate(Double.POSITIVE_INFINITY)).thenReturn(Double.NaN)
            Mockito.`when`(trigPart.calculate(Double.NEGATIVE_INFINITY)).thenReturn(Double.NaN)

            res = FuncSystem(trigPart, logPart)
        }

        @JvmStatic
        fun provideTestData() =
            listOf(
                Arguments.of(0.5, 0.036753),
                Arguments.of(2.0, 0.147249),
                Arguments.of(3.0, 1.887681),
                Arguments.of(4.0, 7.041052),
                Arguments.of(5.0, 16.492265),
                Arguments.of(10.0, 130.55357),

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
    fun `full test`(x: Double, expected: Double) {
        assertEquals(expected, res.calculate(x), eps)
    }

    @Test
    fun throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException::class.java) {
            res.calculate(1.0)
        }

        assertThrows(IllegalArgumentException::class.java) {
            res.calculate(0.0)
        }

        assertThrows(IllegalArgumentException::class.java) {
            res.calculate(-Math.PI / 2)
        }

        assertThrows(IllegalArgumentException::class.java) {
            res.calculate(-Math.PI)
        }

        assertThrows(IllegalArgumentException::class.java) {
            res.calculate(-3 * Math.PI / 2)
        }
    }
}
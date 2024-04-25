package scuf.mopstream.trigonom

import scuf.mopstream.MathFunc

class Tan(
    private val sinFunc: Sin,
    private val cosFunc: Cos
) : MathFunc {
    override fun calculate(x: Double): Double {
        val sinValue = sinFunc.calculate(x)
        val cosValue = cosFunc.calculate(x)
        require(cosValue != 0.0) { "Tan isn`t defined, when cos == 0" }
        return sinValue / cosValue
    }
}
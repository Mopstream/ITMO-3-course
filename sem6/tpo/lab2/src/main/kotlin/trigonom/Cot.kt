package scuf.mopstream.trigonom

import scuf.mopstream.MathFunc

class Cot(
    private val sinFunc: Sin,
    private val cosFunc: Cos
) : MathFunc {
    override fun calculate(x: Double): Double {
        val sinValue = sinFunc.calculate(x)
        val cosValue = cosFunc.calculate(x)
        require(sinValue != 0.0) { "Cot isn`t defined, when sin == 0" }
        return cosValue / sinValue
    }
}
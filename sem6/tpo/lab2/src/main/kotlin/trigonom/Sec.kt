package scuf.mopstream.trigonom

import scuf.mopstream.MathFunc

class Sec(private val cosFunc: Cos) : MathFunc {
    override fun calculate(x: Double): Double {
        val cosValue = cosFunc.calculate(x)
        require(cosValue != 0.0) { "secant isn`t defined, when cos == 0" }
        return 1 / cosValue
    }
}
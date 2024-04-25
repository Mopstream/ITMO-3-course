package scuf.mopstream.logarithm

import scuf.mopstream.MathFunc

class Log(
    private val lnFunc: Ln,
    private val base: Double
) : MathFunc {

    override fun calculate(x: Double): Double {
        return lnFunc.calculate(x) / lnFunc.calculate(base)
    }

}
package scuf.mopstream.trigonom

import scuf.mopstream.MathFunc


class Cos(private val sinFunc: Sin) : MathFunc {
    override fun calculate(x: Double): Double {
        return sinFunc.calculate(x + Math.PI/2)
    }
}
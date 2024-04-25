package scuf.mopstream.functions

import scuf.mopstream.MathFunc
import scuf.mopstream.trigonom.Cos
import scuf.mopstream.trigonom.Cot
import scuf.mopstream.trigonom.Sec
import scuf.mopstream.trigonom.Tan
import kotlin.math.pow

class TrigonomPart(
    private val secFunc: Sec,
    private val cosFunc: Cos,
    private val cotFunc: Cot,
    private val tanFunc: Tan
) : MathFunc {

    override fun calculate(x: Double): Double {
        return (
                (
                    (secFunc.calculate(x) * cosFunc.calculate(x) + secFunc.calculate(x) * secFunc.calculate(x))
                    /
                    secFunc.calculate(x)
                ).pow(3)
                +
                (
                    secFunc.calculate(x).pow(3) - (cotFunc.calculate(x) / tanFunc.calculate(x))
                ).pow(3)
               )
    }

}
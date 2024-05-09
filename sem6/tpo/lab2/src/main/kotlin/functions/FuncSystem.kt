package scuf.mopstream.functions

import scuf.mopstream.MathFunc

class FuncSystem(
    private val trigFunc: TrigonomPart,
    private val logFunc: LogarithmPart
) : MathFunc {

    override fun calculate(x: Double): Double =
        if (x <= 0) {
            trigFunc.calculate(x)
        } else {
            logFunc.calculate(x)
        }

}
package scuf.mopstream.functions

import scuf.mopstream.MathFunc
import scuf.mopstream.logarithm.Ln
import scuf.mopstream.logarithm.Log
import kotlin.math.pow

class LogarithmPart(
    private val log10: Log,
    private val log2: Log,
    private val log5: Log,
    private val log3: Log,
    private val ln: Ln
) : MathFunc {

    override fun calculate(x: Double): Double {
        require(x != 0.0) { "Unresolved value for logarithm" }
        require(x != 1.0) { "Divide by zero" }
        return (
                (
                    (
                        (
                            (log10.calculate(x).pow(2))
                            *
                            (log2.calculate(x) + log10.calculate(x))
                        )
                        -
                        (
                            log5.calculate(x)
                            *
                            (log3.calculate(x) - ln.calculate(x))
                        )
                    )
                    *
                    (
                        (ln.calculate(x) / log10.calculate(x))
                        /
                        log3.calculate(x)
                    )
                )
               ).pow(3)
    }
}


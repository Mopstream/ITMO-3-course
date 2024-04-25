package scuf.mopstream.trigonom

import scuf.mopstream.MathFunc
import kotlin.math.abs
import kotlin.math.pow

class Sin(private val eps: Double) : MathFunc {

    init {
        require(eps > 0) { "Accuracy must be positive" }
    }

    override fun calculate(x: Double): Double {
        if (x.isNaN() || x.isInfinite())
            return Double.NaN
        val xShortened = shortRange(x)
        var result = 0.0
        var current = 10.0
        var prev = 0.0
        var n = 0
        while (abs(prev - current) >= eps) {
            prev = current
            current = sinTailor(xShortened, n)
            result += current
            n++
        }
        return result
    }

    private fun sinTailor(x: Double, n: Int): Double {
        return (-1.0).pow(n) * x.pow(2 * n + 1) / factorial((2 * n + 1).toLong())
    }

    private tailrec fun factorial(n: Long, acc: Long = 1): Long {
        return if (n == 0.toLong()) {
            acc
        } else {
            factorial(n - 1, acc * n)
        }
    }

    private fun shortRange(x: Double): Double =
        if (x > Math.PI || x < -Math.PI) {
            (x % (2 * Math.PI)).let { k ->
                when {
                    k < -Math.PI -> k + 2 * Math.PI
                    k > Math.PI -> k - 2 * Math.PI
                    else -> k
                }
            }
        } else {
            x
        }
}
package scuf.mopstream.t1

import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.pow

object Acos {
    fun calc(x: Double, n: Int): Double {
        fun k(n: Int): Double {
            if (n == 0) return 1.0
            fun help(st: Int, acc: Double): Double {
                return when (st) {
                    n -> (2 * st - 1) * acc / (2 * st)
                    else -> help(st + 1, (2 * st - 1) * acc / (2 * st))
                }
            }
            return help(1, 1.0) / (2 * n + 1)
        }
        if (x.absoluteValue > 1) return Double.NaN
        if ((x + 1).absoluteValue < 0.00001) return PI
        if ((x - 1).absoluteValue < 0.00001) return .0

        return PI / 2 - (0..n).sumOf { k(it) * x.pow(2 * it + 1) }
    }
}
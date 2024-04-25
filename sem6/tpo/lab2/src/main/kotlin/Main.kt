package scuf.mopstream

import scuf.mopstream.functions.FuncSystem
import scuf.mopstream.functions.LogarithmPart
import scuf.mopstream.functions.TrigonomPart
import scuf.mopstream.logarithm.Ln
import scuf.mopstream.logarithm.Log
import scuf.mopstream.trigonom.*
import java.io.FileWriter
import java.io.IOException

fun writeToCsv(
    computeFunction: MathFunc,
    start: Double,
    end: Double,
    step: Double,
    path: String,
    funName: String,
) {
    require(start < end) { "Start must be less than End" }
    var x = start
    val data = mutableListOf<Pair<Double, Double>>()
    while (x <= end) {
        val y = computeFunction.calculate(x)
        data.add(x to y)
        x += step
    }

    try {
        FileWriter(path).use { writer ->
            writer.append("X,$funName\n")

            data.forEach { (value1, value2) ->
                writer.append("$value1,$value2\n")
            }

            println("Data writes to: $path")
        }
    } catch (e: IOException) {
        println("Write error: ${e.message}")
    }
}

fun main() {
    val eps = 0.001
    val sin = Sin(eps)
    val cos = Cos(sin)
    val cot = Cot(sin, cos)
    val tan = Tan(sin, cos)
    val sec = Sec(cos)
    val trigFunc = TrigonomPart(sec, cos, cot, tan)
    val ln = Ln(eps)
    val log2 = Log(ln, 2.0)
    val log3 = Log(ln, 3.0)
    val log5 = Log(ln, 5.0)
    val log10 = Log(ln, 10.0)
    val logFunc = LogarithmPart(log10, log2, log5, log3, ln)
    val system = FuncSystem(trigFunc, logFunc)

    writeToCsv(sin, -8.0, 1.0, 0.1, "src/main/resources/sin.csv", "sin")
    writeToCsv(cos, -8.0, 1.0, 0.1, "src/main/resources/cos.csv", "cos")
    writeToCsv(cot, -8.0, 1.0, 0.1, "src/main/resources/cot.csv", "cot")
    writeToCsv(tan, -8.0, 1.0, 0.1, "src/main/resources/tan.csv", "tan")
    writeToCsv(sec, -8.0, 1.0, 0.1, "src/main/resources/sec.csv", "sec")
    writeToCsv(ln, 0.0001, 101.0, 0.1, "src/main/resources/ln.csv", "ln")
    writeToCsv(log2, 0.0001, 101.0, 0.1, "src/main/resources/log2.csv", "log2")
    writeToCsv(log3, 0.0001, 101.0, 0.1, "src/main/resources/log3.csv", "log3")
    writeToCsv(log5, 0.0001, 101.0, 0.1, "src/main/resources/log5.csv", "log5")
    writeToCsv(log10, 0.0001, 101.0, 0.1, "src/main/resources/log10.csv", "log10")
    writeToCsv(logFunc, 0.0001, 101.0, 0.1, "src/main/resources/logFunc.csv", "logFunc")
    writeToCsv(trigFunc, -8.0, 1.0, 0.1, "src/main/resources/trigFunc.csv", "trigFun")
    writeToCsv(system, -8.0, 101.0, 0.1, "src/main/resources/system.csv", "system")
}
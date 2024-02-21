case class Matrix(data: List[List[Double]]) {
  private val nRows = data.length
  private val nColumns = data.head.length

  def transpose(): Matrix = {
    val newData = data.foldLeft(List(): List[List[Double]])((acc, str) =>
      acc match {
        case List() => str.map(List(_))
        case acc => acc.zip(str).map(p => p._1.prepended(p._2))
      }
    )
    Matrix(newData.map(_.reverse))
  }

  def *(other: Matrix): Matrix = {
    val l = for {
      str <- this.data
      col <- other.transpose().data

    } yield str.zip(col).map(p => p._1 * p._2).sum
    Matrix(l.grouped(other.nColumns).toList)
  }

  def inverse(): Matrix = {
    import org.apache.commons.math3.linear._
    val realMatrix = new Array2DRowRealMatrix(data.map(_.toArray).toArray)
    val inverted = new LUDecomposition(realMatrix).getSolver.getInverse.getData.map(_.toList).toList
    Matrix(inverted)
  }

  def print(): Unit = {
    println(s"rows = $nRows, columns = $nColumns")
    println(data.map(_.mkString(", ")).mkString("\n"))
  }

}

def sigmoid(x:Double): Double = 1 / (1 + math.exp(-x))

val x_train: List[List[Double]] = (List(List(1.0,2,3), List(4,5,6)))
val (n, features) = (x_train.length, x_train.head.length)
val y_train: List[Double] = List(7.0,8)
val bias = 0
val weights = List.fill(features)(.0)

Matrix(x_train).print()
Matrix(List(weights)).transpose().print()

val linear_predictions = (Matrix(x_train) * Matrix(List(weights)).transpose()).transpose().data.head.map(_ + bias)
val predictions: List[Double] = linear_predictions.map(sigmoid)

val dw = (Matrix(x_train).transpose() * Matrix(List(predictions.zip(y_train).map(x => x._1 - x._2))).transpose()).transpose().data.head.map(_ * (2/n))

predictions.zip(y_train).map(x => x._1 - x._2).sum
val db = (2 / n) * predictions.zip(y_train).map(x => x._1 - x._2).sum


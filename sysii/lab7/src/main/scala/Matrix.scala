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

  def flatten:List[Double] = this.transpose().data.head

  def print(): Unit = {
    println(s"rows = $nRows, columns = $nColumns")
    println(data.map(_.mkString(", ")).mkString("\n"))
  }

}

object Matrix {
  def Column(x:List[Double]):Matrix = Matrix(List(x)).transpose()
}
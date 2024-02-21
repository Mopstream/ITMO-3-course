case class Regression(train_size: Double, model: List[String], dataFrame: List[Map[String, Double]]) {
  private type Matrix_t = List[List[Double]]
  private val df: Matrix_t = dataFrame.map(_.filter(p => model.contains(p._1))).map(_.values.toList)

  private val r2_score: Double = {
    val (x_train: Matrix_t, x_test) = df.map(_.prepended(1.0)).splitAt((df.length * train_size).toInt)
    val (y_train, y_test) = dataFrame.map(p => p("median_house_value")).splitAt((dataFrame.length * train_size).toInt)

    val x: Matrix = Matrix(x_train)
    val y: Matrix = Matrix(List(y_train)).transpose()
    val `x'` = x.transpose()

    val theta = (`x'` * x).inverse() * `x'` * y

    val xTest = Matrix(x_test)

    val y_predict = (xTest * theta).transpose().data.head
    val res = y_test.zip(y_predict).map(p => math.pow(p._1 - p._2, 2)).sum
    val tot = y_test.map(x => math.pow(x - Statistics("", y_test).mean, 2)).sum

    1 - (res / tot)
  }

  def printScore(): Unit = {
    println("==== R2 SCORE ====")
    println(s"Model = ${model.mkString(",\n\t\t")}")
    println(s"train_size = $train_size")
    println(s"score = $r2_score")
    println("==================\n")
  }
}

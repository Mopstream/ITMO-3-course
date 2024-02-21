object Main {
  implicit class Preprocessor(val data: List[List[String]]) {
    def processNull: List[List[String]] = data.filterNot(_.init.exists(_ == "0"))
    def toDouble: List[List[Double]] = data.map(_.map(_.toDouble))

    def preprocess: List[List[Double]] = data.processNull.toDouble
  }

  private def getStatistics(implicit attrs: List[String], data: List[List[Double]]): List[Statistics] = {
    val attrValues: List[List[Double]] = data.foldLeft(List(): List[List[Double]])(
      (acc, str) => acc match {
        case List() => str.map(List(_))
        case acc => acc.zip(str).map(x => x._1.prepended(x._2))
      }
    )
    attrs.zip(attrValues).map(x => Statistics(x._1, x._2))
  }

  private def visualizeDataSet(implicit statistics: List[Statistics]): Unit =
    for {stat <- statistics} {
      stat.visualize()
      println("\n")
    }

  implicit class Normalizer(val data: List[List[Double]]) {
    def normalize(implicit stats: List[Statistics]): List[List[Double]] = {
      data.map(_.zip(stats)).map(_.map(p => p._2.normalize(p._1)))
    }
  }

  def main(args: Array[String]): Unit = {
    val f = io.Source.fromFile("diabetes.csv")
    val data = f.getLines().toList.map(_.split(',').toList)
    implicit val attrs: List[String] = data.head
    val dataSet: List[List[String]] = data.tail
    implicit val preprocessed: List[List[Double]] = dataSet.preprocess
    implicit val statistics: List[Statistics] = getStatistics
    visualizeDataSet

    val normalized = preprocessed.normalize

    val dataFrame: List[Map[String, Double]] = normalized.foldLeft(List(): List[Map[String, Double]])((acc, str) => acc.prepended(attrs.zip(str).toMap))

    for {
      n_iter <- List(100, 500, 800, 900, 1000, 1200, 1300, 1500, 2000, 3000, 5000, 10000)
      l_rate <- List(0.01, 0.001, 0.0001)
    } {
      LogisticRegression(0.75, dataFrame, n_iter, l_rate).printScore()
    }
  }

}
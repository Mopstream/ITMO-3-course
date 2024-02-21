object Main {
  implicit class Preprocessor(val data: List[List[String]]) {
    def processNull: List[List[String]] = data.filterNot(_.exists(_ == ""))

    def processCodeCategorical: List[List[Double]] = data.map(_.map(_.toDouble))

    def preprocess: List[List[Double]] = data.processNull.processCodeCategorical
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
    val f = io.Source.fromFile("WineDataset.csv")
    val data = f.getLines().toList.map(_.split(',').toList)

    implicit val attrs: List[String] = data.head.map(_.filter(_ != '\"'))
    val dataSet: List[List[String]] = data.tail
    implicit val preprocessed: List[List[Double]] = dataSet.preprocess

    implicit val statistics: List[Statistics] = getStatistics
    visualizeDataSet

    val normalized = preprocessed.normalize

    val dataFrame: List[Map[String, Double]] =
      scala.util.Random.shuffle(normalized.foldLeft(List(): List[Map[String, Double]])((acc, str) => acc.prepended(attrs.zip(str).toMap)))

    val model1 = List(
      "Alcohol",
      "Malic Acid",
      "Ash",
      "Alcalinity of ash",
      "Magnesium",
      "Total phenols",
      "Flavanoids",
      "Nonflavanoid phenols",
      "Proanthocyanins",
      "Color intensity",
      "Hue",
      "OD280/OD315 of diluted wines",
      "Proline"
    )

    val model2 = model1.map(x => if (scala.util.Random.nextDouble() > 0.5) x else "").filterNot(_ == "")

    for {k <- List(3, 5, 10, 15)} {
      KNN(0.75, model1, dataFrame, k).printScore()
      KNN(0.75, model2, dataFrame, k).printScore()
    }
  }
}
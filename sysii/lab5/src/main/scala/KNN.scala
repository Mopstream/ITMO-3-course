case class KNN(train_size: Double, model: List[String], dataFrame: List[Map[String, Double]], k: Int) {
  private val df = dataFrame.map(x => (x.filter(p => model.contains(p._1)).values.toList, x("Wine")))

  private val (train, test) = df.splitAt((df.length * train_size).toInt)

  private val x_test: List[List[Double]] = test.map(_._1)
  private val y_test: List[Double] = test.map(_._2)

  private val k_nn: List[(Double, Double)] = {
    def dist(a: List[Double], b: List[Double]): Double = {
      math.sqrt(a.zip(b).map(p => (p._1 - p._2) * (p._1 - p._2)).sum)
    }

    def take_k_nearest(value: List[Double]): List[Double] = {
      train.sortBy(p => dist(p._1, value)).take(k).map(_._2)
    }

    def predict(possible: List[Double]): Double = {
      possible.groupMapReduce(identity)(_ => 1)(_ + _).maxBy(_._2)._1
    }

    val pred = x_test.map(x => predict(take_k_nearest(x)))
    pred.zip(y_test)
  }

  private def f1_score(cl: Double): (Double, Double, Double, Double) = {
    k_nn.foldLeft((0: Double, 0: Double, 0: Double, 0: Double))((stats, y) => {
      val (tp, fp, tn, fn) = stats
      (y._1 == cl, y._2 == cl) match {
        case (true, true) => (tp + 1, fp, tn, fn)
        case (true, false) => (tp, fp + 1, tn, fn)
        case (false, false) => (tp, fp, tn + 1, fn)
        case (false, true) => (tp, fp, tn, fn + 1)
      }
    })
  }

  def printScore(): Unit = {
    println("=========== F1 SCORE ===========")
    println(s"k = $k")
    println(s"Model = ${model.mkString(",\n\t\t")}")
    println(s"train_size = $train_size")
    for {cl: Double <- y_test.toSet} {
      val (tp, fp, tn, fn) = f1_score(cl)
      val `class` = (cl * 2).toInt + 1
      println(s"\nclass = ${`class`}")
      println(s"tp = $tp\tfp = $fp\ntn = $tn\tfn = $fn")
      println(s"f1 score for ${`class`} = ${2 * tp / (2 * tp + fp + fn)}")
    }
    println("================================\n")
  }
}

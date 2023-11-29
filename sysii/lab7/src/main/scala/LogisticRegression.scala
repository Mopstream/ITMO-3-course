import scala.annotation.tailrec
import Matrix.Column

case class LogisticRegression(train_size: Double, dataFrame: List[Map[String, Double]], n_iter: Int, l_rate: Double) {
  private val df = dataFrame.map(x => (x.init.values.toList, x("Outcome")))

  private val (train, test) = df.splitAt((df.length * train_size).toInt)

  private val x_train: List[List[Double]] = train.map(_._1)
  private val y_train: List[Double] = train.map(_._2)

  private val x_train_M = Matrix(x_train)

  private val x_test: List[List[Double]] = test.map(_._1)
  private val y_test: List[Double] = test.map(_._2)

  private val x_test_M = Matrix(x_test)

  private val (n, features) = (x_train.length, x_train.head.length)

  private def sigmoid(x: Double): Double = 1 / (1 + math.exp(-x))

  private val (weights: List[Double], bias: Double) = {
    @tailrec def fit(n_iter: Int, weights: List[Double], bias: Double): (List[Double], Double) = {
      if (n_iter == 0) {
        (weights, bias)
      } else {
        val linear_predictions: List[Double] = (x_train_M * Column(weights)).flatten.map(_ + bias)
        val predictions: List[Double] = linear_predictions.map(sigmoid)

        val dw: List[Double] = (x_train_M.transpose() * Column(predictions.zip(y_train).map(x => x._1 - x._2))).flatten.map(_ * 2.0 / n)
        val db: Double = predictions.zip(y_train).map(x => x._1 - x._2).sum * 2.0 / n
        fit(n_iter - 1, weights.zip(dw).map(x => x._1 - l_rate * x._2), bias - l_rate * db)
      }
    }

    fit(n_iter, List.fill(features)(.0), 0)

  }

  private val predicted: List[Double] = (x_test_M * Column(weights)).flatten.map(_ + bias).map(sigmoid)

  private val (tp, fp, tn, fn) = predicted.zip(y_test).foldLeft((0: Double, 0: Double, 0: Double, 0: Double))((stats, y) => {
    val (tp, fp, tn, fn) = stats
    (y._1 > 0.45, y._2 == 1) match {
      case (true, true) => (tp + 1, fp, tn, fn)
      case (true, false) => (tp, fp + 1, tn, fn)
      case (false, false) => (tp, fp, tn + 1, fn)
      case (false, true) => (tp, fp, tn, fn + 1)
    }
  })
  private val (accuracy, precision, recall, f1_score) = ((tp + tn) / (tp + fp + tn + fn), tp / (tp + fp), tp / (tp + fn), 2 * tp / (2 * tp + fp + fn))

  def printScore(): Unit = {
    println("=========== F1 SCORE ===========")
    println(s"number of iterations = $n_iter")
    println(s"learning rate = $l_rate")
    println(s"train_size = $train_size")
    println(s"tp = $tp\t fp = $fp")
    println(s"tn = $tn\t fn = $fn")
    println(s"accuracy = $accuracy")
    println(s"precision = $precision")
    println(s"recall = $recall")
    println(s"f1_score = $f1_score")
    println("================================\n")
  }
}

import scala.annotation.tailrec

object Main {
  implicit class Preprocessor(val data: List[List[String]]) {
    def processNull: List[List[String]] = data.filterNot(_.exists(_ == "?"))

    def preprocess: List[List[String]] = data.processNull
  }

  def main(args: Array[String]): Unit = {
    val f = io.Source.fromFile("agaricus-lepiota.data")
    val dataSet = f.getLines().toList.map(_.split(',').toList)
    implicit val preprocessed: List[List[String]] = dataSet.preprocess


    val attrs = List(
      "cap-shape",
      "cap-surface",
      "cap-color",
      "bruises",
      "odor",
      "gill-attachment",
      "gill-spacing",
      "gill-size",
      "gill-color",
      "stalk-shape",
      "stalk-root",
      "stalk-surface-above-ring",
      "stalk-surface-below-ring",
      "stalk-color-above-ring",
      "stalk-color-below-ring",
      "veil-type",
      "veil-color",
      "ring-number",
      "ring-type",
      "spore-print-color",
      "population",
      "habitat"
    )
    val header = attrs.prepended("poisonous")
    val dataFrame: List[Map[String, String]] = preprocessed.map(x => header.zip(x).toMap)

    val model = scala.util.Random.shuffle(attrs).take(math.sqrt(attrs.length).toInt + 1).prepended("poisonous")
    println(s"model: ${model.mkString(", ")}")
    val filtered = dataFrame.map(_.toList.filter(x => model.contains(x._1)).toMap)

    val (train, test) = scala.util.Random.shuffle(filtered).splitAt((filtered.length * 0.75).toInt)
    val tree = Tree(train)

    @tailrec def get_res(elem: Map[String, String], tree: Tree): Double = {
      tree match {
        case Node(attr, children) => if (children.contains(elem(attr))) get_res(elem, children(elem(attr))) else 0
        case Leaf(v) => v
      }
    }

    val pred: List[(String, Double)] = test.map(elem => (elem("poisonous"), get_res(elem, tree)))

    def metrics(threshold: Double): (Double, Double, Double, Double) = {
      pred.foldLeft((0: Double, 0: Double, 0: Double, 0: Double))((stats, y) => {
        val (tp, fp, tn, fn) = stats
        (y._1 == "e", y._2 >= threshold) match {
          case (true, true) => (tp + 1, fp, tn, fn)
          case (true, false) => (tp, fp, tn, fn + 1)
          case (false, false) => (tp, fp, tn + 1, fn)
          case (false, true) => (tp, fp + 1, tn, fn)
        }
      })
    }

    val (tp, fp, tn, fn) = metrics(0.5)

    println("DEFAULT (0.5)")
    println(s"accuracy = ${(tp + tn) / (tp + tn + fp + fn)}")
    println(s"precision = ${tp / (tp + fp)}")
    println(s"recall = ${tp / (tp + fn)}")


    import xyz.devfortress.splot._

    val ROC = Figure(
      title = "ROC curve",
      xLabel = "False Positive Rate",
      yLabel = "True Positive Rate",
      showGrid = true
    )

    val roc_curve = pred.sortBy(_._2).map(x => {
      val (tp, fp, tn, fn) = metrics(x._2)
      (fp / (fp + tn), tp / (tp + fn))
    })
    ROC.plot(roc_curve, color = "blue", lw = 2)
    ROC.show(900, 600)


    val PR = Figure(
      title = "PR curve",
      xLabel = "Recall",
      yLabel = "Precision",
      showGrid = true
    )

    val pr_curve = pred.sortBy(_._2).map(x => {
      val (tp, fp, tn, fn) = metrics(x._2)
      (tp / (tp + fn), tp / (tp + fp))
    })
    PR.plot(pr_curve, color = "blue", lw = 2)
    PR.show(900, 600)

  }
}
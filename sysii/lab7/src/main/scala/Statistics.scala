case class Statistics(name: String, values: List[Double]) {

  private val variationSeries = values.sorted
  private val len: Int = variationSeries.length
  private val mean: Double = expectedValue(variationSeries)
  private val max: Double = variationSeries.last
  private val min: Double = variationSeries.head
  private val standardDeviation: Double = expectedValue(variationSeries.map(x => x * x)) - mean * mean

  def normalize(x: Double): Double = if (max != min) {
    (x - min) / (max - min)
  } else 1

  private def expectedValue(l: List[Double]) = l.sum / l.length

  private def quantile(q: Double) = variationSeries(((len-1) * q).toInt)

  def visualize(): Unit = {
    println(s"==== ${name.toLowerCase()} ====")
    println(s"count = $len")
    println(s"mean = $mean")
    println(s"max = $max")
    println(s"min = $min")
    println(s"deviation = $standardDeviation")
    println(s"quantile 0.25 = ${quantile(0.25)}")
    println(s"quantile 0.5 = ${quantile(0.5)}")
    println(s"quantile 0.75 = ${quantile(0.75)}")
  }
}
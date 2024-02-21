sealed trait Tree


case class Node(attr: String,
                children: Map[String, Tree]) extends Tree

case class Leaf(cat: Double) extends Tree

object Tree {

  def info(T: List[Map[String, String]]): Double = {
    -T.map(_("poisonous")).groupMapReduce(identity)(_ => 1)(_ + _).toList.map(_._2).map(freq => {
      val card: Double = T.length
      freq / card * math.log(freq / card) / math.log(2)
    }).sum
  }

  def info_x_split(T: List[Map[String, String]]): Map[String, (Double, Double)] = {
    val attrs = T.head.keys.toList
    attrs.map(x => x -> {
      T.groupBy(_(x)).map(pair => {
        val t_i: Double = pair._2.length
        val t = T.length
        (t_i / t * info(pair._2), t_i / t * math.log(t_i / t) / math.log(2))
      }).reduce((l, r) => (l._1 + r._1, l._2 + r._2))
    }).toMap
  }


  def gain(T: List[Map[String, String]]): Map[String, Double] = {
    val info_t = info(T)
    info_x_split(T).map(x => (x._1, (info_t - x._2._1) / x._2._2)) - "poisonous"
  }


  def apply(T: List[Map[String, String]]): Tree = {
    if (T.head.toList.length > 1) {
      val name = gain(T).maxBy(_._2)._1
      Node(name, T.groupBy(_(name)).map(x => x._1 -> Tree(x._2.map(_ - name))))
    } else {
      val x = T.map(_("poisonous")).groupMapReduce(identity)(_ => 1: Double)(_ + _)
      Leaf(
        if (x.contains("e"))
          if (x.contains("p"))
            x("e") / x("p")
          else 1
        else 0)
    }
  }
}
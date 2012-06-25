
abstract class Exp

case class ListExp(list: List[Exp]) extends Exp {
  override def toString: String =
    "(" + list.map(_ toString).reduceLeft(_+" "+_) + ")"
}

case class AtomExp(atom: String) extends Exp {
  override def toString: String = atom
}

case class StringExp(string: String) extends Exp {
  override def toString: String =
    "\"" + string.replaceAllLiterally("\"", "\\\"") + "\""
}

case class NumberExp(number: Double) extends Exp {
  override def toString: String = number.toString
}


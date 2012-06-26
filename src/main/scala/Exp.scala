
case class InvalidCoercion(msg: String) extends Exception {
  override def toString: String = "InvalidCoercion: " + msg
}


abstract class Exp {
  def transform[A](transformer: ExpTransformer[A]): A

  def coerceAtom: String = throw InvalidCoercion("this is not an atom")
  def coerceString: String = throw InvalidCoercion("this is not a string")
}

abstract class ExpTransformer[A] {
  def transformList(list: List[Exp], results: List[A]): A
  def transformAtom(atom: String): A
  def transformString(string: String): A
  def transformNumber(number: Double): A
  def transformBoolean(boolean: Boolean): A
}

case class ListExp(list: List[Exp]) extends Exp {
  override def toString: String =
    "(" + list.map(_ toString).reduceLeft(_+" "+_) + ")"

  override def transform[A](transformer: ExpTransformer[A]): A =
    transformer.transformList(list, list.map(_.transform(transformer)))
}

case class AtomExp(atom: String) extends Exp {
  override def toString: String = atom

  override def coerceAtom: String = atom

  override def transform[A](transformer: ExpTransformer[A]): A =
    transformer.transformAtom(atom)
}

case class StringExp(string: String) extends Exp {
  override def toString: String =
    "\"" + string.replaceAllLiterally("\"", "\\\"") + "\""

  override def coerceString: String = string

  override def transform[A](transformer: ExpTransformer[A]): A =
    transformer.transformString(string)
}

case class NumberExp(number: Double) extends Exp {
  override def toString: String = number.toString
  
  override def transform[A](transformer: ExpTransformer[A]): A =
    transformer.transformNumber(number)
}

case class BooleanExp(boolean: Boolean) extends Exp {
  override def toString: String = if(boolean) { "#t" } else { "#f" }

  override def transform[A](transformer: ExpTransformer[A]): A =
    transformer.transformBoolean(boolean)
}



case class EvaluationException(msg: String) extends Exception {
  override def toString: String = "EvaluationException: " + msg
}

object Evaluator {
  val builtins: Map[String, List[Exp] => Exp] = Map(
    "string->symbol" -> ((args: List[Exp]) =>
      args match {
        case List(StringExp(x)) => AtomExp(x)
        case _ => throw EvaluationException("invalid arguments to string->symbol")
      }),
    "string-append" -> ((args: List[Exp]) =>
      StringExp(args.map(_.coerceString).reduceLeft(_+_)))
  )

  def evalFunc(name: String, args: List[Exp], env: Map[String, Exp]): Exp = {
    if(!builtins.contains(name))
      throw EvaluationException("`"+name+"' is not a function or form")
    else
      builtins(name)(args.map(apply(_, env)))
  }

  def evalQuasiquote(body: List[Exp], env: Map[String, Exp]): Exp = {
    null
  }

  def evalLet(body: List[Exp], env: Map[String, Exp]): Exp = {
    null
  }

  def apply(exp: Exp, env: Map[String, Exp]): Exp = exp match {
    case ListExp(AtomExp("quasiquote") :: body) => evalQuasiquote(body, env)
    case ListExp(AtomExp("let") :: body) => evalLet(body, env)
    case ListExp(AtomExp(func) :: args) => evalFunc(func, args, env)
    // Self-evaluating forms
    case AtomExp(_) => exp
    case StringExp(_) => exp
    case BooleanExp(_) => exp
    case NumberExp(_) => exp
    case _ => throw EvaluationException("invalid expression")
  }
}

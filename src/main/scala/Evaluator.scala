
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

  private class QuasiquoterTransformer(val env: Map[String, Exp])
      extends ExpTransformer[Exp] {
    
    override def transformList(list: List[Exp], results: List[Exp]): Exp = {
      results match {
        case List(AtomExp("unquote"), body) => apply(body, env)
        case _ => ListExp(results)
      }
    }

    override def transformAtom(atom: String): Exp = AtomExp(atom)
    override def transformString(string: String): Exp = StringExp(string)
    override def transformNumber(number: Double): Exp = NumberExp(number)
    override def transformBoolean(boolean: Boolean): Exp = BooleanExp(boolean)
  }

  def evalQuasiquote(body: List[Exp], env: Map[String, Exp]): Exp =
    body(0).transform(new QuasiquoterTransformer(env))

  def evalLet(body: List[Exp], env: Map[String, Exp]): Exp = {
    def evalDecl(decl: Exp): (String, Exp) = decl match {
      case ListExp(List(AtomExp(name), value)) => name -> apply(value, env)
      case _ => throw EvaluationException("invalid let syntax")
    }

    body match {
      case List(ListExp(decls), exp) => {
        val newEnv = env ++ decls.map(evalDecl)
        apply(exp, newEnv)
      }
      case _ => throw EvaluationException("invalid let syntax")
    }
  }

  def apply(exp: Exp, env: Map[String, Exp]): Exp = exp match {
    case ListExp(AtomExp("quasiquote") :: body) => evalQuasiquote(body, env)
    case ListExp(AtomExp("let") :: body) => evalLet(body, env)
    case ListExp(AtomExp(func) :: args) => evalFunc(func, args, env)
    case AtomExp(name) => env(name)
    // Self-evaluating forms
    case StringExp(_) => exp
    case BooleanExp(_) => exp
    case NumberExp(_) => exp
    case _ => throw EvaluationException("invalid expression")
  }
}

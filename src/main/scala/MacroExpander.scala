
object MacroExpander {
  private class ExpanderTransformer(val macros: Map[String, Macro])
      extends ExpTransformer[Exp] {

    override def transformList(list: List[Exp], results: List[Exp]): Exp = {
      if(results.size > 0 && (results(0) match {
          case AtomExp(name) => macros.contains(name)
          case _ => false
        })) {

        val macro = macros(results(0) match { case AtomExp(name) => name })
        val env = Map(macro.formals.zip(results.tail): _*)
        Evaluator(macro.body, env)
      } else ListExp(results)
    }

    override def transformAtom(atom: String): Exp = AtomExp(atom)
    override def transformString(string: String): Exp = StringExp(string)
    override def transformNumber(number: Double): Exp = NumberExp(number)
    override def transformBoolean(boolean: Boolean): Exp = BooleanExp(boolean)
  }

  def apply(exp: Exp, macros: Map[String, Macro]): Exp =
    exp.transform(new ExpanderTransformer(macros))
}


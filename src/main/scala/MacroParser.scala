
case class Macro(name: String, formals: List[String], body: Exp)

object MacroParser {
  private def stringFromAtom(exp: Exp) = exp match { case AtomExp(x) => x }

  def apply(macroExps: List[Exp]): Map[String, Macro] =
    Map(macroExps.map(macroExp => {
      macroExp match {
        case ListExp(List(AtomExp("Macro"),
                          ListExp(AtomExp(name) :: formals),
                          body)) =>
          name -> Macro(name, formals.map(stringFromAtom), body)
        case _ => throw ParseFailedException("expected (macro) clause")
      }
    }): _*)
}


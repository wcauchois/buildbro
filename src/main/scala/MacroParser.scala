
case class Macro(name: String, formals: List[String], body: Exp)

object MacroParser {
  def apply(macroExps: List[Exp]): Map[String, Macro] =
    Map(macroExps.map(macroExp => {
      macroExp match {
        case ListExp(List(AtomExp("macro"),
                          ListExp(AtomExp(name) :: formals),
                          body)) =>
          name -> Macro(name, formals.map(_.coerceAtom), body)
        case _ => throw ParseFailedException("expected (macro) clause")
      }
    }): _*)
}


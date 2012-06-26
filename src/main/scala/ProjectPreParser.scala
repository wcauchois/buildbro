import scala.collection.mutable.ListBuffer

object ProjectPreParser {
  def apply(exp: Exp): (List[Exp], Exp) = {
    exp match {
      case ListExp((project @ AtomExp("project")) ::
                   (name @ AtomExp(_)) ::
                   (description @ StringExp(_)) ::
                   body) => {
        var targets = new ListBuffer[Exp]
        var macros = new ListBuffer[Exp]
        for(clause <- body) {
          clause match {
            case macro @ ListExp(AtomExp("macro") :: _) => macros += macro
            case target => targets += target
          }
        }
        (macros.toList, ListExp(project :: name :: description :: targets.toList))
      }
      case _ => throw ParseFailedException("expected (project) clause")
    }
  }
}

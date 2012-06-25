import scala.collection.mutable.ListBuffer

case class ParseFailedException(msg: String) extends Exception {
  override def toString: String = "ParseFailedException: " + msg
}

object ProjectParser {
  def parseCommand(exp: Exp): Command = exp match {
    case ListExp(List(AtomExp("!"), StringExp(cmdLine))) => ExecCommand(cmdLine)
    case ListExp(List(AtomExp("mv"), StringExp(from), StringExp(to))) =>
      MoveCommand(from, to)
    case _ => throw ParseFailedException("expected command")
  }
  def parseTarget(exp: Exp): Target = exp match {
    case ListExp(AtomExp("target") ::
                 AtomExp(name) ::
                 ListExp(AtomExp("depends") :: depends) ::
                 ListExp(AtomExp("creates") :: creates) ::
                 commandExps) => {
      var dependsFilesBuffer = new ListBuffer[String]
      var dependsTargetsBuffer = new ListBuffer[String]
      for(dependency <- depends) {
        dependency match {
          case StringExp(string) => dependsFilesBuffer += string
          case AtomExp(atom) => dependsTargetsBuffer += atom
          case _ =>
            throw ParseFailedException("expected string or atom in (depends) clause")
        }
      }

      var createsFilesBuffer = new ListBuffer[String]
      for(created <- creates) {
        created match {
          case StringExp(string) => createsFilesBuffer += string
          case _ => throw ParseFailedException("expected string in (creates) clause")
        }
      }

      val dependsFiles = dependsFilesBuffer.toList
      val dependsTargets = dependsTargetsBuffer.toList
      val createsFiles = createsFilesBuffer.toList

      val commands = commandExps.map(parseCommand)

      Target(name, dependsTargets, dependsFiles, createsFiles, commands)
    }
    case _ => throw ParseFailedException("expected (target) clause")
  }
  def parseProject(exp: Exp): Project = exp match {
    case ListExp(AtomExp("project") ::
                 AtomExp(name) ::
                 StringExp(description) ::
                 targetExps
                ) => Project(name, description,
                       Map(targetExps.map((exp) => {
                         val target = parseTarget(exp)
                         target.name -> target
                       }): _*))
    case _ => throw ParseFailedException("expected (project) clause")
  }
  def apply(exp: Exp): Project = parseProject(exp)
}


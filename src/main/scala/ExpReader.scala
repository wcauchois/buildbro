import scala.util.parsing.combinator._

object ExpReader extends RegexParsers {
  override def skipWhitespace: Boolean = false

  def delim: Parser[Any] = """[ \t\n]*""".r

  def leftParen: Parser[Any] = "(" | "[" | "{"

  def rightParen: Parser[Any] = ")" | "]" | "}"

  def list: Parser[Exp] =
    (leftParen ~ delim) ~> (repsep(exp, delim) ^^ { ListExp(_) }) <~ (delim ~ rightParen)

  def atom: Parser[Exp] =
    """[a-zA-Z!$%&*/:<=>?^_~][a-zA-Z!$%&*/:<=>?^_~0-9\-]*""".r ^^ { AtomExp(_) }

  def string: Parser[Exp] = "\"(\\\\\"|[^\"])*\"".r ^^
    { (x: String) => StringExp(x.init.tail.replaceAllLiterally("\\\"", "\"")) }

  def number: Parser[Exp] = """\d+(\.\d*)?""".r ^^
    { (x: String) => NumberExp(x.toDouble) }
  
  def unquotedExp: Parser[Exp] =
    "," ~> exp ^^ { (x: Exp) => ListExp(List(AtomExp("unquote"), x)) }

  def quasiquotedExp: Parser[Exp] =
    "`" ~> exp ^^ { (x: Exp) => ListExp(List(AtomExp("quasiquote"), x)) }

  def exp: Parser[Exp] = list | atom | string | number | unquotedExp | quasiquotedExp

  def apply(input: String): Exp = parseAll(exp, input) match {
    case Success(result, _) => result
    case failure: NoSuccess => scala.sys.error(failure.msg)
  }
}

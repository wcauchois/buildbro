
import scala.io.Source

object BuildBro {
  def main(args: Array[String]) {
    if(args.length < 1) {
      println("usage: buildbro [-v|-d|-vd] TARGET")
      return
    }
    
    var targetName: String = null
    var verbose: Boolean = false
    var dryRun: Boolean = false
    if(args(0).startsWith("-")) {
      val flags = args(0).tail
      if(flags.contains("v"))
        verbose = true
      if(flags.contains("d"))
        dryRun = true
      targetName = args(1)
    } else
      targetName = args(0)

    val contents = Source.fromFile("build.bro").mkString
    val exp = ExpReader(contents.trim)
    if(verbose) { println(exp); println() }
    val project = ProjectParser(exp)
    if(verbose) { println(project); println() }
    project.exec(targetName, dryRun)
  }
}


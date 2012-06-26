
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
    } else {
      targetName = args(0)
    }

    // 1. Read the contents of the build.bro file
    val contents = Source.fromFile("build.bro").mkString
    // 2. Read in the contents as s-expressions
    val exp = ExpReader(contents.trim)
    if(verbose) { println(exp); println() }
    // 3. Pre-parse the expression to get the macros out
    val (macroExps, projectExp) = ProjectPreParser(exp)
    // 4. Parse the macros
    val macros = MacroParser(macroExps)
    // 5. Expand the macros within the expression
    val expandedProjectExp = MacroExpander(projectExp, macros)
    if(verbose) { println(expandedProjectExp); println() }
    // 6. Parse the project definition
    val project = ProjectParser(expandedProjectExp)
    if(verbose) { println(project); println() }
    // 7. Execute the project
    project.exec(targetName, dryRun)
  }
}


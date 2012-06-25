import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Queue
import java.io.File
import sys.process._

case class ExecFailedException(msg: String) extends Exception {
  override def toString: String = "ExecFailedException: " + msg
}

case class Project(
  name: String,
  description: String,
  targets: Map[String, Target]
) {
  private case class Node(target: Target, depends: List[Target])

  private def getDependencies(target: Target): List[Target] = {
    def loop(current: Target, visited: List[Target]): ListBuffer[Target] = {
      if(visited.contains(current))
        throw ExecFailedException("there is a cycle in the dependency graph")
      var dependencies = ListBuffer(current)
      val newVisited = current :: visited
      for(dependencyName <- current.dependsTargets) {
        for(subDependency <- loop(targets(dependencyName), newVisited)) {
          if(!dependencies.contains(subDependency))
            dependencies += subDependency
        }
      }
      dependencies
    }
    loop(target, Nil).toList
  }

  private def nodesFromTargets(targets: List[Target]): List[Node] = {
    for(target <- targets)
      yield Node(target, target.dependsTargets.map(this.targets(_)))
  }

  private def getExecSequence(target: Target): List[Target] = {
    var execSequence = new ListBuffer[Target]
    var toProcess = Queue(nodesFromTargets(getDependencies(target)): _*)
    var prevSize = toProcess.size
    while(!toProcess.isEmpty) {
      for(node <- toProcess.toList) {
        if(node.depends.forall(execSequence.contains)) {
          execSequence += node.target
          toProcess.dequeueAll(_ == node)
        }
      }
      if(toProcess.size == prevSize) {
        // This means we have a cycle in the dependency graph
        throw ExecFailedException("there is a cycle in the dependency graph")
      }
      prevSize = toProcess.size
    }
    execSequence.toList
  }

  def exec(targetName: String, dryRun: Boolean) {
    val target = targets(targetName)
    for(step <- getExecSequence(target)) {
      if(step.isUpToDate)
        println("`" + step.name + "' is up to date")
      else
        step.exec(dryRun)
    }
  }
}

case class Target(
  name: String,
  dependsTargets: List[String],
  dependsFiles: List[String],
  createsFiles: List[String],
  commands: List[Command]
) {
  def isUpToDate: Boolean = {
    if(dependsFiles.isEmpty)
      return !createsFiles.isEmpty

    var earliestModified = dependsFiles.map(dependedFileName => {
      val dependedFile = new File(dependedFileName)
      if(!dependedFile.exists)
        throw ExecFailedException("depended on file `"+dependedFileName+"' does not exist")
      dependedFile.lastModified
    }).min
    createsFiles.forall(createdFileName => {
      val createdFile = new File(createdFileName)
      createdFile.exists && createdFile.lastModified >= earliestModified
    })
  }

  def exec(dryRun: Boolean) {
    for(command <- commands)
      command.exec(dryRun)
  }
}

abstract class Command {
  def exec(dryRun: Boolean)
}

case class ExecCommand(cmdLine: String) extends Command {
  override def exec(dryRun: Boolean) {
    if(dryRun) {
      println("! " + cmdLine)
    } else {
      cmdLine.!
    }
  }
}

case class MoveCommand(from: String, to: String) extends Command {
  override def exec(dryRun: Boolean) {
    if(dryRun) {
      println("mv " + from + " " + to)
    } else {
      new File(from).renameTo(new File(to))
    }
  }
}


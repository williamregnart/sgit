import java.io.File

import commands._
import files.DirectoryHandler

object Main extends App {

  val actual_directory = new File(System.getProperty("user.dir"))
  val directory = new DirectoryHandler(actual_directory)
  val exist_sgit = directory.existsSubDirectory(".sgit")

  if (args.length == 0) println("You must specify an action")
  else execute(args)

  def execute(command: Array[String]): Unit = {
    if(command.isEmpty) println(Console.RED+"sgit should have arguments"+Console.WHITE)
    else {
      command.head match {
        case "init" => init(command.tail)
        case "add" => add(command.tail)
        case "remove" => remove(command.tail)
        case "commit" => commit(command.tail)
        case "log" => log(command.tail)
        case "status" => status(command.tail)
        case "tag" => tag(command.tail)
        case "branch" => branch(command.tail)
        case "checkout" => checkout(command.tail)
        case _ => println(command.head + " doesn't exist")
      }
    }
  }

  def init(command: Array[String]): Unit = {
    if(command.isEmpty) Init.createSgit(actual_directory)
    else println("sgit init has no option")
  }

  def add(command: Array[String]): Unit = {
    @scala.annotation.tailrec
    def apply(command: Array[String], firstOption:Boolean): Unit = {
      if (command.isEmpty && firstOption) {
        println("Nothing specified, nothing added.")
      }
      else {
        if (!command.isEmpty){
          command(0) match {
            case "." =>
              Add.addFilesToIndex(actual_directory.getPath)
            case _ =>
              Add.addFileToIndex(command(0),actual_directory.getPath)
          }
          apply(command.tail,firstOption = false)
        }
      }
    }
    if(exist_sgit) apply(command,firstOption = true)
    else println(Console.RED+"ERROR : not a sgit repository (use \"sgit init\" to create one)"+Console.WHITE)
  }

  def remove(command: Array[String]): Unit = {
    @scala.annotation.tailrec
    def apply(command: Array[String], firstOption:Boolean): Unit = {
      if (command.isEmpty && firstOption) {
        println("Nothing specified, nothing added.")
      }
      else {
        if (!command.isEmpty){
          Add.removeFileToIndex("/"+command(0),actual_directory.getPath)
          apply(command.tail,firstOption = false)
        }
      }
    }
    if(exist_sgit) apply(command,firstOption = true)
    else println(Console.RED+"ERROR : not a sgit repository (use \"sgit init\" to create one)"+Console.WHITE)
  }

  def commit(command:Array[String]):Unit = {
    if(!exist_sgit) println(Console.RED+"ERROR : not a sgit repository (use \"sgit init\" to create one)"+Console.WHITE)
    else if(command.isEmpty){
      Commit.commit(actual_directory.getPath)
    }
    else println("sgit commit has no option")
  }

  def log(command:Array[String]):Unit = {
    if(!exist_sgit) println(Console.RED+"ERROR : not a sgit repository (use \"sgit init\" to create one)"+Console.WHITE)
    else if(command.isEmpty){
      Log.printLog(actual_directory.getPath, p = false, stat = false)
    }
    else if(command.tail.nonEmpty) println(Console.RED+"sgit log has only one argument (-p or -stat)"+Console.WHITE)

    else{
      command.head match {
        case "-p" => Log.printLog(actual_directory.getPath, p = true, stat = false)
        case "--stat" => Log.printLog(actual_directory.getPath,p = false, stat = true)
        case _ =>println(Console.RED+"sgit log "+command(0)+" doesn't exist"+Console.WHITE)
      }
      println("sgit log has no option")
    }
  }

  def status(command:Array[String]):Unit = {
    if(!exist_sgit) println(Console.RED+"ERROR : not a sgit repository (use \"sgit init\" to create one)"+Console.WHITE)
    else if(command.nonEmpty) println(Console.RED+"sgit status has no option"+Console.WHITE)
    else{
      Status.printUntrackedFiles(actual_directory.getPath)
      println()
      Status.printUncommittedFiles(actual_directory.getPath)
    }
  }

  def tag(command:Array[String]):Unit = {
    if(!exist_sgit) println(Console.RED+"ERROR : not a sgit repository (use \"sgit init\" to create one)"+Console.WHITE)
    else if(command.isEmpty) println(Console.RED+"sgit tag should have 1 argument (tag name)"+Console.WHITE)
    else if(command.tail.nonEmpty) println(Console.RED+"sgit tag should have only one tag name in argument"+Console.WHITE)
    else Tag.addTag(command(0),actual_directory.getPath)
  }

  def branch(command:Array[String]):Unit = {
    if(!exist_sgit) println(Console.RED+"ERROR : not a sgit repository (use \"sgit init\" to create one)"+Console.WHITE)
    else if(command.isEmpty) println(Console.RED+"sgit branch should have 1 argument (a branch name to create a new branch, or \"-av\" to print list of branches and tags)"+Console.WHITE)
    else if(command.tail.nonEmpty) println(Console.RED+"sgit branch should have 0 (list of branches) or 1 (create branch) argument"+Console.WHITE)
    else if(command.head == "-av") {
      val branches_list = Branch.getAllBranches(actual_directory.getPath)
      println("Branches : ")
      Branch.printAllBranches(branches_list)
      println()
      println("Tags : ")
      Tag.printAllTags(actual_directory.getPath)
    }
    else Branch.executeBranchCommand(command.head,actual_directory.getPath)
  }

  def checkout(command:Array[String]):Unit = {
    if(!exist_sgit) println(Console.RED+"ERROR : not a sgit repository (use \"sgit init\" to create one)"+Console.WHITE)
    else if(command.isEmpty) println(Console.RED+"sgit checkout should have a branch/tag/commit in argument"+Console.WHITE)
    else if(command.tail.nonEmpty) println(Console.RED+"sgit checkout should have only 1 argument (branch/tag/commit name)"+Console.WHITE)
    else CheckOut.executeCheckOutCommand(command.head,actual_directory.getPath)
  }
}

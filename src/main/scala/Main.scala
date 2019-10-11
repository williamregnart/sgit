import java.io.File

object Main extends App {

  val actual_directory = new File(System.getProperty("user.dir"))

  if (args.length == 0) println("You must specify an action")
  else execute(args)

  def execute(command: Array[String]): Unit = {
    command(0) match {
      case "init" => init(command.tail)
      case "add" => add(command.tail)
      case "commit" => commit(command.tail)
      case _ => println(command.head+" doesn't exist")
    }
  }

  def init(command: Array[String]): Unit = {
    if(command.isEmpty) Init.createSgit(actual_directory)
    else println("sgit init has no option")
  }

  def add(command: Array[String]): Unit = {
    def apply(command: Array[String],firstOption:Boolean): Unit = {
      if (command.isEmpty && firstOption) {
        println("Nothing specified, nothing added.")
      }
      else {
        if (!command.isEmpty){
          command(0) match {
            case "." =>
              Add.addFilesToIndex(actual_directory)
            case _ =>
              Add.addFileToIndex(command(0),actual_directory)
          }
          apply(command.tail,firstOption = false)
        }
      }
    }
    apply(command,firstOption = true)
  }

  def commit(command:Array[String]):Unit = {
    if(command.isEmpty){
      Commit.commit(actual_directory)
    }
    else println("sgit commit has no option")
  }
}

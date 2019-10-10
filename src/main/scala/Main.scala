import java.io.File

object Main extends App {
  println(args)
  if (args.length == 0) {
    println("You must specify an action")
  }
  else {
    execute(args)
  }

  def execute(command: Array[String]): Unit = command(0) match {
    case "init" => init(command)
  }

  def init(command: Array[String]): Unit = command match {
    case Array("init") => {
      Init.createSgit(new File(System.getProperty("user.dir")))
    }
    case default => println("Command sgit init has no option")
  }
}

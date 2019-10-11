import java.io.File

case class DirectoryHandler(f:File) {
  private val dir = f

  def getAllFilesPath:Array[String] = {
    def apply(files:Array[File],files_path:Array[String]):Array[String] = {
      if (files.isEmpty) files_path
      else {
        if (files.head.isDirectory && files.head.getName == ".sgit") {
          apply(files.tail, files_path)
        }
        else {
          if (files.head.isDirectory) {
            apply(files.tail, files_path ++: apply(files.head.listFiles(), Array[String]()))
          }
          else {
            println(FileHandler(files.head).getPathFromActualDir)
            apply(files.tail, files_path :+ FileHandler(files.head).getPathFromActualDir)
          }
        }
      }
    }
    apply(dir.listFiles(),Array[String]())
  }

  def getFilesName:Array[String] = {
    dir.listFiles().filter(file=>file.isFile).map(file=>file.getName)
  }

  def showFilesName:Boolean = {
    def apply(listFiles:Array[String]):Boolean={
      if(listFiles.isEmpty) true
      else{
        println(listFiles.head)
        apply(listFiles.tail)
      }
    }
    apply(getFilesName)
  }




}

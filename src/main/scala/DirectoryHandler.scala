import java.io.File

class DirectoryHandler(f:File) {
  private val dir = f

  /**
    * function getAllFilesPath
    * @return paths of files in the directory and all subdirectories, except in .sgit
    */
  def getAllFilesPath:List[String] = {
    def apply(files:Array[File],files_path:List[String]):List[String] = {
      if (files.isEmpty) files_path
      else {
        //if file is .sgit directory, ignore it
        if (files.head.isDirectory && files.head.getName == ".sgit") {
          apply(files.tail, files_path)
        }
        else {
          //if file is a directory, add all it paths files by applying the same function
          if (files.head.isDirectory) {
            apply(files.tail, files_path ++: apply(files.head.listFiles(), List[String]()))
          }
            //if file is a file, add it path from actual directory to the list of results
          else {
            apply(files.tail, files_path :+ FileHandler(files.head).getPathFromDir(dir))
          }
        }
      }
    }
    apply(dir.listFiles(),List[String]())
  }

  /**
    * function getFilesName
    * @return list of name of files in directory
    */
  def getFilesName:Array[String] = {
    dir.listFiles().filter(file=>file.isFile).map(file=>file.getName)
  }





}

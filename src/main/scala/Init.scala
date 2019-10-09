import java.io._
object Init {

  def createSgit(actualDirectory:File): Unit = {

    //if  the directory doesn't have a .git directory, create it
    if (!existsDirectory(actualDirectory.listFiles(),".sgit")){
      createDirectory(actualDirectory.getPath,".sgit")
    }

    val gitPath=actualDirectory.getPath+"/.sgit"
    createDirectory(gitPath,"objects")

    val gitObjectPath=gitPath+"/objects"
    createDirectory(gitObjectPath,"commits")
    createDirectory(gitObjectPath,"blobs")
    createDirectory(gitObjectPath,"trees")

    createDirectory(gitPath,"refs")

    val gitRefsPath=gitPath+"/refs"
    createDirectory(gitRefsPath,"tags")
    createDirectory(gitRefsPath,"heads")

    createDirectories(gitPath,Array[String]("logs","refs","heads"))

    new File(gitPath+"/logs/refs/heads/master").createNewFile()

    new File(gitPath+"/INDEX").createNewFile()

    val head_file = FileHandler(new File(gitPath+"/HEAD"))
    head_file.createFile()
    head_file.addContent("master",appendContent = false)

    new File(gitRefsPath+"/heads/master").createNewFile()

  }


  def createDirectory(actualPath:String,directoryName:String):Unit = {
    new File(actualPath+"/"+directoryName).mkdirs();
  }

  def createDirectories(actualPath: String, directories:Array[String]):Unit = {
    if(!directories.isEmpty){
      createDirectory(actualPath,directories.head)
      createDirectories(actualPath+"/"+directories.head,directories.tail)
    }
  }

  def existsDirectory(fileList:Array[File],directoryName:String):Boolean = {
    if (fileList.isEmpty) false
    else {
      if (fileList.head.isDirectory) {
        if (fileList.head.getName() == directoryName) true
        else existsDirectory(fileList.tail, directoryName)
      }
      else existsDirectory(fileList.tail, directoryName)
    }
  }
}

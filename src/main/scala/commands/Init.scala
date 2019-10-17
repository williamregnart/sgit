package commands

import java.io.File

import files.FileHandler

object Init {

  /**
    * function createSgit
    * @param actualDirectory : the directory from which we create the sgit repo
    * @return true if the sgit has been created, else false
    */
  def createSgit(actualDirectory:File): Boolean = {

    //if  the directory doesn't have a .sgit directory, create it
    if (!existsDirectory(actualDirectory.listFiles(),".sgit")) {
      createDirectory(actualDirectory.getPath, ".sgit")
      val gitPath = actualDirectory.getPath + "/.sgit"
      createDirectory(gitPath, "objects")

      val gitObjectPath = gitPath + "/objects"
      createDirectory(gitObjectPath, "commits")
      createDirectory(gitObjectPath, "blobs")
      createDirectory(gitObjectPath, "trees")

      createDirectory(gitPath, "refs")

      val gitRefsPath = gitPath + "/refs"
      createDirectory(gitRefsPath, "tags")
      createDirectory(gitRefsPath, "heads")

      createDirectories(gitPath, Array[String]("logs", "refs", "heads"))

      new File(gitPath + "/logs/refs/heads/master").createNewFile()

      new File(gitPath + "/INDEX").createNewFile()

      val head_file = new FileHandler(new File(gitPath + "/HEAD"))
      head_file.createFile()
      head_file.addContent("master", appendContent = false)

      new File(gitRefsPath + "/heads/master").createNewFile()

      true
    }
    else false
  }

  /**
    * function createDirectory
    * @param actualPath : the path from where we create the directory
    * @param directoryName : the directory name we create
    */
  def createDirectory(actualPath:String,directoryName:String):Unit = {
    new File(actualPath+"/"+directoryName).mkdirs()
  }

  /**
    * function createDirectories : create directories level by level (each directory is created in the last one)
    * @param actualPath : the path from where we create the directory
    * @param directories : the list of directories name to create
    */
  def createDirectories(actualPath: String, directories:Array[String]):Unit = {
    if(!directories.isEmpty){
      createDirectory(actualPath,directories.head)
      createDirectories(actualPath+"/"+directories.head,directories.tail)
    }
  }

  /**
    * function existsDirectory
    * @param fileList : the list of File (directories or files) where we search directory
    * @param directoryName : the name of directory we are looking for
    * @return true if directory is in the list, else false
    */
  def existsDirectory(fileList:Array[File],directoryName:String):Boolean = {
    if (fileList.isEmpty) false
    else {
      if (fileList.head.isDirectory) {
        if (fileList.head.getName == directoryName) true
        else existsDirectory(fileList.tail, directoryName)
      }
      else existsDirectory(fileList.tail, directoryName)
    }
  }
}

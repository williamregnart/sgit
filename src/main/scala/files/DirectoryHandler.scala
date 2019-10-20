package files

import java.io.File

import scala.reflect.io.Directory

class DirectoryHandler(f:File) {
  private val dir = f

  def getPath:String = f.getPath

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
            apply(files.tail, files_path ++ apply(files.head.listFiles(), List[String]()))
          }
            //if file is a file, add it path from actual directory to the list of results
          else {
            apply(files.tail, files_path :+ new FileHandler(files.head).getPathFromDir(dir.getPath))
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
  def getFilesName:List[String] = {
    dir.listFiles().filter(file=>file.isFile).map(file=>file.getName).toList
  }

  def getFiles:List[File] = {
    f.listFiles().toList
  }

  def getDirectories:List[String] = {
    @scala.annotation.tailrec
    def apply(list_files:List[File], result : List[String]):List[String] = {
      if(list_files.isEmpty) result
      else if(list_files.head.isFile) apply(list_files.tail,result)
      else apply(list_files.tail,result:+list_files.head.getName)
    }
    apply(getFiles,List())
  }

  def existsSubDirectory(subdirectory:String):Boolean = {
    getDirectories.contains(subdirectory)
  }

  /**
    * function removeFilesExcept : delete all files in directories except ones specified
    * @param files_to_keep : files and directories to keep
    */
  def deleteFilesExcept(files_to_keep:List[String]):Unit = {
    def apply(directory_files:List[File]):Boolean = {
      if(directory_files.isEmpty) true

      //if file or directory has to be keep
      else if(files_to_keep.contains(directory_files.head.getName)) apply(directory_files.tail)

      //if it has to be remove
      else{
        //if it is a file, delete it
        if(directory_files.head.isFile) directory_files.head.delete()

        else{
          //if it is a directory, delete it with all it subfiles and subdirectories
          val directory = new Directory(directory_files.head)
          directory.deleteRecursively()
        }
        apply(directory_files.tail)
      }
    }
    apply(getFiles)
  }





}

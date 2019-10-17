package commands

import java.io.File

import files.{DirectoryHandler, FileHandler}

object Tag {

  def printAllTags(actual_directory:File):Unit = {
    def apply(tags:List[String]):Boolean = {
      if(tags.isEmpty) true
      else{
        println(tags.head)
        apply(tags.tail)
      }
    }
    val tag_directory = new DirectoryHandler(new File(actual_directory.getPath+"/.sgit/refs/tags"))
    apply(tag_directory.getFilesName)
  }

  def existTag(tag:String,tags:List[String]):Boolean = {
    tags.contains(tag)
  }

  def addTag(tag:String,actual_directory:File): Unit = {
    val tag_directory = new DirectoryHandler(new File(actual_directory.getPath+"/.sgit/refs/tags"))

    //if tag already exists
    if(existTag(tag,tag_directory.getFilesName)) println(Console.RED+"ERROR : tag " + tag + " already exists!")

    else{
      //get the last commit of actual branch
      val actual_branch = Branch.getActualBranch(actual_directory)
      val last_commit = Commit.getLastCommitFromBranch(actual_branch,actual_directory)

      //create tag file with last commit in content
      val tag_file = new FileHandler(new File(actual_directory.getPath+"/.sgit/refs/tags/"+tag))
      tag_file.createFile()
      tag_file.addContent(last_commit,appendContent = false)

      println(Console.GREEN+"Tag " + tag + " created with success, reference commit " + last_commit)
    }
    print(Console.WHITE)
  }
}

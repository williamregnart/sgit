package commands

import java.io.File

import files.{DirectoryHandler, FileHandler}

import scala.reflect.io.Directory

object Init {


  /**
    * function createSgit
    * @param actual_directory : the directory from which we create the sgit repo
    * @return true if the sgit has been created, else false
    */
  def createSgit(actual_directory:File): Boolean = {

    val directory = new DirectoryHandler(actual_directory)
    val exist_sgit = directory.existsSubDirectory(".sgit")

    //if  the directory doesn't have a .sgit directory, create it
    if (!exist_sgit) {
      new File(directory.getPath+"/.sgit").mkdir()

      new File(directory.getPath+"/.sgit/objects").mkdir()
      new File(directory.getPath+"/.sgit/objects/commits").mkdir()
      new File(directory.getPath+"/.sgit/objects/trees").mkdir()
      new File(directory.getPath+"/.sgit/objects/blobs").mkdir()

      new File(directory.getPath+"/.sgit/refs").mkdir()
      new File(directory.getPath+"/.sgit/refs/tags").mkdir()
      new File(directory.getPath+"/.sgit/refs/heads").mkdir()
      new File(directory.getPath+"/.sgit/refs/heads/master").createNewFile()

      new File(directory.getPath+"/.sgit/INDEX").createNewFile()

      val head_file = new FileHandler(new File(directory.getPath+"/.sgit/HEAD"))
      head_file.createFile()
      head_file.addContent("master", appendContent = false)
      true
    }
    else{
      println(Console.RED+"sgit repository already exists!"+Console.WHITE)
      false
    }
  }
}

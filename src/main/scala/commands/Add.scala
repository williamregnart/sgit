package commands

import java.io.File

import files.{DirectoryHandler, FileHandler, IndexHandler}
import others.Encryption

object Add{


  /**
    * function createBlob
    * @param file_content : file content from which we create blob
    * @param actual_directory_path : sgit repository path
    */
  def createBlob(file_content:String,actual_directory_path:String):Unit ={
    val blobs_path = new File(actual_directory_path+"/.sgit/objects/blobs")
    //blob_name is file content encrypted
    val blob_name = Encryption.sha1(file_content)
    val blob_file = new FileHandler(new File(blobs_path+"/"+blob_name))
    blob_file.createFile()
    blob_file.addContent(file_content,appendContent = false)
  }

  /**
    * function addFileToIndex
    * @param file_path_from_dir : path of file we want to add to index
    * @param actual_directory_path : sgit repo
    * @return true if file add to index, else false
    */
  def addFileToIndex(file_path_from_dir:String, actual_directory_path:String):Boolean = {
    val indexFile = new FileHandler(new File(actual_directory_path+"/.sgit/INDEX"))
    val fileToAdd = new FileHandler(new File(actual_directory_path+"/"+file_path_from_dir))

    if(fileToAdd.existFile()) {
      //file should not be empty to be add to index

      createBlob(fileToAdd.getContent, actual_directory_path)

      val lineToAdd = fileToAdd.getPathFromDir(actual_directory_path) + " " + fileToAdd.getUniqueKey

      // if file path exists in index, replace the blob
      if (indexFile.getLineWithPattern(fileToAdd.getPathFromDir(actual_directory_path)).isDefined) {
        indexFile.replaceLineByContent(indexFile.getLineWithPattern(fileToAdd.getPathFromDir(actual_directory_path)).get, lineToAdd)
      }
      //if file with the right blob doesn't exist in index, add it
      if (!indexFile.existPatternInFile(lineToAdd)) {
        indexFile.addContent(lineToAdd + "\n", appendContent = true)
      }
        true
    }
      //if file not found
    else{
      println("fatal: pathspec "+file_path_from_dir+" did not match any files")
      false
    }
  }

  /**
    * function addFilesToIndex (case sgit add .)
    * @param actual_directory_path : sgit repo
    * @return true
    */
  def addFilesToIndex(actual_directory_path:String):Boolean={
    @scala.annotation.tailrec
    def apply(files_path:List[String], actual_directory_path:String):Boolean= {
      if (files_path.isEmpty) true
      else {
        addFileToIndex(files_path.head.replaceAll("\\\\", "/"), actual_directory_path)
        apply(files_path.tail, actual_directory_path)
      }
    }
    val index_file = new FileHandler(new File(actual_directory_path+"/.sgit/INDEX"))
    //all files are add, index has to be overwrite
    index_file.clearContent()
    //create the directoryHandler of sgit repo to have all filesPath
    val directoryHandler = new DirectoryHandler(new File(actual_directory_path))
    apply(directoryHandler.getAllFilesPath,actual_directory_path)
  }

  def removeFileToIndex(file_path_from_dir:String, actual_directory_path:String):Boolean = {
    val index_file = new IndexHandler(new File(actual_directory_path+"/.sgit/INDEX"))
    index_file.addContent(index_file.getLinesWithoutPath(file_path_from_dir),appendContent = false)
    true
  }
}

import java.io.{BufferedReader, File, PrintWriter}

object Add{


  /**
    * function createBlob
    * @param file : file from which we create blob
    * @param actual_directory : sgit repository
    */
  def createBlob(file:FileHandler,actual_directory:File):Unit ={
    val blobs_path = new File(actual_directory.getPath+"/.sgit/objects/blobs")
    //blob_name is file content encrypted
    val blob_file = FileHandler(new File(blobs_path+"/"+file.getUniqueKey))
    blob_file.createFile()
    blob_file.addContent(file.getContent,appendContent = false)
  }

  /**
    * function addFileToIndex
    * @param file_path_from_dir : path of file we want to add to index
    * @param actual_directory : sgit repo
    * @return true if file add to index, else false
    */
  def addFileToIndex(file_path_from_dir:String,actual_directory:File):Boolean = {
    val indexFile = FileHandler(new File(actual_directory.getPath+"/.sgit/INDEX"))
    val fileToAdd = FileHandler(new File(actual_directory.getPath+"/"+file_path_from_dir))

    if(fileToAdd.existFile()) {
      //file should not be empty to be add to index
      if (!(fileToAdd.getContent == "")) {

        createBlob(fileToAdd, actual_directory)

        val lineToAdd = fileToAdd.getPathFromDir(actual_directory) + " " + fileToAdd.getUniqueKey

        // if file path exists in index, replace the blob
        if (indexFile.getLineWithPattern(fileToAdd.getPathFromDir(actual_directory)).isDefined) {
          indexFile.replaceLineByContent(indexFile.getLineWithPattern(fileToAdd.getPathFromDir(actual_directory)).get, lineToAdd)
        }
        //if file with the right blob doesn't exist in index, add it
        if (!indexFile.existPatternInFile(lineToAdd)) {
          indexFile.addContent(lineToAdd + "\n", appendContent = true)
        }
        true
      }
      else false
    }
      //if file not found
    else{
      println("fatal: pathspec "+file_path_from_dir+" did not match any files")
      false
    }
  }

  /**
    * function addFilesToIndex (case sgit add .)
    * @param files_path : paths of files to add to index
    * @param actual_directory : sgit repo
    * @return true
    */
  def addFilesToIndex(actual_directory:File):Boolean={
    def apply(files_path:List[String],actual_directory:File):Boolean= {
      if (files_path.isEmpty) true
      else {
        addFileToIndex(files_path.head.replaceAll("\\\\", "/"), actual_directory)
        apply(files_path.tail, actual_directory)
      }
    }
    val index_file = FileHandler(new File(actual_directory.getPath+"/.sgit/INDEX"))
    //all files are add, index has to be overwrite
    index_file.clearContent()
    //create the directoryHandler of sgit repo to have all filesPath
    val directoryHandler = new DirectoryHandler(actual_directory)
    apply(directoryHandler.getAllFilesPath,actual_directory)
  }
}

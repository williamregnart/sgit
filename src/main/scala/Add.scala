import java.io.{BufferedReader, File, PrintWriter}

object Add{


  def createBlob(file:FileHandler,actual_directory:File):Unit ={
    val blobs_path = new File(actual_directory.getPath+"/.sgit/objects/blobs")
    val blob_file = FileHandler(new File(blobs_path+"/"+file.getUniqueKey))
    blob_file.createFile()
    blob_file.addContent(file.getContent,appendContent = false)
  }

  def addFileToIndex(file_path_from_dir:String,actual_directory:File):Boolean = {
    val indexFile = FileHandler(new File(actual_directory.getPath+"/.sgit/INDEX"))
    val fileToAdd = FileHandler(new File(actual_directory.getPath+"/"+file_path_from_dir))

    if(fileToAdd.existFile()) {
      if (!(fileToAdd.getContent == "")) {
        createBlob(fileToAdd, actual_directory)

        val lineToAdd = fileToAdd.getPathFromActualDir + " " + fileToAdd.getUniqueKey

        if (indexFile.getLineWithPattern(fileToAdd.getPathFromActualDir).isDefined) {
          indexFile.replaceLineByContent(indexFile.getLineWithPattern(fileToAdd.getPathFromActualDir).get, lineToAdd)
        }
        if (!indexFile.existPatternInFile(lineToAdd)) {
          indexFile.addContent(lineToAdd + "\n", appendContent = true)
        }
        true
      }
      else false
    }
    else{
      println("fatal: pathspec "+file_path_from_dir+" did not match any files")
      false
    }
  }

  def addFilesToIndex(files_path:Array[String],actual_directory:File):Boolean={
    def apply(files_path:Array[String],actual_directory:File):Boolean= {
      if (files_path.isEmpty) true
      else {
        addFileToIndex(files_path.head.replaceAll("\\\\", "/"), actual_directory)
        apply(files_path.tail, actual_directory)
      }
    }
    val index_file = FileHandler(new File(actual_directory.getPath+"/.sgit/INDEX"))
    index_file.clearContent()
    apply(files_path,actual_directory)
  }

  def addAllFilesToIndex(actual_directory:File):Boolean = {
    val directoryHandler = DirectoryHandler(actual_directory)
    addFilesToIndex(directoryHandler.getAllFilesPath,actual_directory)
  }
}

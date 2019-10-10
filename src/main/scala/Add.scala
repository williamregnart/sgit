import java.io.{BufferedReader, File, PrintWriter}

object Add{


  def createBlob(file:FileHandler,actual_directory:File):Unit ={
    val blobs_path = new File(actual_directory.getPath+"/.sgit/objects/blobs")
    val blob_file = FileHandler(new File(blobs_path+"/"+file.getUniqueKey))
    blob_file.createFile()
    blob_file.addContent(file.getContent,appendContent = false)
  }

  def addFileToIndex(file_path:String,actual_directory:File):Unit = {
    val indexFile = FileHandler(new File(actual_directory.getPath+"/.sgit/INDEX"))

    val fileToAdd = FileHandler(new File(file_path))
    if(!(fileToAdd.getContent == "")) {
      createBlob(fileToAdd, actual_directory)

      val lineToAdd = fileToAdd.getPathFromActualDir + " " + fileToAdd.getUniqueKey

      if (indexFile.getLineWithPattern(fileToAdd.getPathFromActualDir).isDefined) {
        indexFile.replaceLineByContent(indexFile.getLineWithPattern(fileToAdd.getPathFromActualDir).get, lineToAdd)
      }
      if (!indexFile.existPatternInFile(lineToAdd)) {
        indexFile.addContent(lineToAdd + "\n", appendContent = true)
      }
    }
  }

  def addFilesToIndex(files_path:Array[String],actual_directory:File):Boolean={
    if (files_path.isEmpty) true
    else{
      addFileToIndex(files_path.head.replaceAll("\\\\","/"),actual_directory)
      addFilesToIndex(files_path.tail,actual_directory)
    }
  }



}

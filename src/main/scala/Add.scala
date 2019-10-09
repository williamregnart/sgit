import java.io.{BufferedReader, File, PrintWriter}

object Add{

  val actualDirectory = new File(System.getProperty("user.dir"))
  val gitPath = actualDirectory.getPath+"/.sgit"

  def createBlob(file:FileHandler):Unit ={
    val blobs_path = new File(System.getProperty("user.dir")).getPath+"/.sgit/objects/blobs"
    val blob_file = new FileHandler(new File(blobs_path+"/"+file.getUniqueKey))
    blob_file.createFile()
    blob_file.addContent(file.getContent,appendContent = false)
  }

  def addFileToIndex(file_path:String):Unit = {
    val indexFile = new FileHandler(new File(gitPath+"/INDEX"))

    val fileToAdd = new FileHandler(new File(file_path))
    createBlob(fileToAdd)

    val lineToAdd = fileToAdd.getPathFromActualDir+" "+fileToAdd.getUniqueKey

    if(indexFile.getLineWithPattern(fileToAdd.getPathFromActualDir).isDefined){
      indexFile.replaceLineByContent(indexFile.getLineWithPattern(fileToAdd.getPathFromActualDir).get,lineToAdd)
    }
    if(!indexFile.existPatternInFile(lineToAdd)){
      indexFile.addContent(lineToAdd+"\n",appendContent = true)
    }
  }

  def addFilesToIndex(files_path:Array[String]):Boolean={
    if (files_path.isEmpty) true
    else{
      addFileToIndex(files_path.head.replaceAll("\\\\","/"))
      addFilesToIndex(files_path.tail)
    }
  }


  val directory = new DirectoryHandler(actualDirectory)
  val indexFile = new FileHandler(new File(gitPath+"/INDEX"))
  indexFile.cleanContent()
  addFilesToIndex(directory.getAllFilesPath)

  directory.showFilesName


}

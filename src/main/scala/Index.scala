import java.io.File

object Index{
  val actualDirectory = new File(System.getProperty("user.dir"))
  val gitPath = actualDirectory.getPath+"/.sgit"
  val index_file = new FileHandler(new File(gitPath+"/INDEX"))

  def getAllFilesPath(file:FileHandler):List[String]={
    file.linesList.map(e=>e.split(" ")(0))
  }

  def getElementsFromPath(path:String,listPath:List[String],listElements:List[String]):List[String] ={
    if(listPath.isEmpty) listElements
    else{
      //if the actual path file contains the path
      if((path.r findFirstIn  listPath.head).isDefined){
        val filePathFromDirPath = listPath.head.replace(path,"")
        //if it is a file
        if(filePathFromDirPath.split("/").length==2){
          getElementsFromPath(path,listPath.tail,listElements:+listPath.head)
        }
        else getElementsFromPath(path,listPath.tail,listElements)
      }
      else getElementsFromPath(path,listPath.tail,listElements)
    }
  }

  def getFilesFromPath(path:String):List[String]={
    getElementsFromPath(path,getAllFilesPath(index_file),List[String]())
  }

  def getDirPathFromPath(path:String):List[String]={
    getElementsFromPath(path,getAllPathAndSubPath(getAllFilesPath(index_file)),List[String]())
  }

  def concatPath(path:List[String],index:Int):String = {
    if(index==0) ""
    else path.head.concat("/"+concatPath(path.tail,index-1))
  }

  def getPathAndSubPath(path:String):List[String] = {
    def apply(pathSplit:List[String],listPath:List[String],splitIndex:Int):List[String] = {
      if(splitIndex==pathSplit.length) listPath
      else apply(pathSplit,listPath:+concatPath(pathSplit,splitIndex),splitIndex+1)
    }
    apply(path.split("/").toList,List[String](),0)
  }

  def getAllPathAndSubPath(listPath:List[String]):List[String]={
    def apply(listPath: List[String],listAllPath:List[String]):List[String]={
      if(listPath.isEmpty) listAllPath.distinct
      else apply(listPath.tail,listAllPath++getPathAndSubPath(listPath.head))
    }
    apply(listPath,List[String]())
  }

  def getBlobFromFilePath(path:String):String={
    val line_blob = index_file.getLineWithPattern(path)
    if(line_blob.isDefined){
      line_blob.get.split(" ")(1)
    }
    else ""
  }






}

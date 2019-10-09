import java.io.File

import Index.{actualDirectory, getAllPathAndSubPath, getElementsFromPath}

class IndexHandler(override val f:File) extends FileHandler(f) {
  val file = f
  val gitPath = actualDirectory.getPath+"/.sgit"
  def getAllFilesPath:List[String]={
    linesList.map(e=>e.split(" ")(0))
  }

  def getFilesFromPath(path:String):List[String]={
    getElementsFromPath(path,getAllFilesPath,List[String]())
  }

  def getDirPathFromPath(path:String):List[String]={
    getElementsFromPath(path,getAllPathAndSubPath(getAllFilesPath),List[String]())
  }
  def getBlobFromFilePath(path:String):String={
    val line_blob = getLineWithPattern(path)
    if(line_blob.isDefined){
      line_blob.get.split(" ")(1)
    }
    else ""
  }

  def getBlobLineToInsert(path_file:String):String={
    "\n"+"blob "+getBlobFromFilePath(path_file)+" "+path_file.split("/").last
  }

  def getTreeLineToInsert(path_dir:String):String={
    "\n"+"tree "+getTree(path_dir.substring(0,path_dir.length-1))
  }

  def insertAllBlobs(actual_path:String,list_files:List[String],contentTree:String):String={
    if(list_files.isEmpty) contentTree
    else insertAllBlobs(actual_path,list_files.tail,contentTree+getBlobLineToInsert(list_files.head))
  }

  def insertAllTrees(actual_path:String,list_dir:List[String],contentTree:String):String={
    if(list_dir.isEmpty) contentTree
    else insertAllTrees(actual_path,list_dir.tail,contentTree+getTreeLineToInsert(list_dir.head))
  }


  def getTree(path:String):String={
    val blobs_content = insertAllBlobs(path,getFilesFromPath(path),"")
    val trees_content = insertAllTrees(path,getDirPathFromPath(path),"")
    val tree_name = Encryption.sha1(blobs_content+trees_content)
    val tree_file = FileHandler(new File(gitPath+"/objects/trees/"+tree_name))
    tree_file.createFile()
    tree_file.addContent(blobs_content+trees_content,appendContent = false)
    tree_name
  }

}

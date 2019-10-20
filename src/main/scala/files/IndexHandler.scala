package files

import java.io.File

import others._

class IndexHandler(f:File) extends FileHandler(f) {

  /**
    * function getAllFilesPath
    * @return the list of all file paths written in Index file
    */
  def getAllFilesPath:List[String]={
    getLinesList.map(e=>e.split(" ")(0))
  }

  /**
    * function getFilesFromPath
    * @param path : the path we want all the files in
    * @return a list of path : all the files path in path in param
    */
  def getFilesFromPath(path:String):List[String]={
    Path.getElementsFromPath(path,getAllFilesPath,List[String]())
  }

  /**
    * function getDirPathFromPath
    * @param path : the path we want all the directories in
    * @return a list of path : all the directories path in path in param
    */
  def getDirPathFromPath(path:String):List[String]={
    Path.getElementsFromPath(path,Path.getAllPathAndSubPath(getAllFilesPath),List[String]())
  }

  def getPathAndBlob:Map[String,String]={
    @scala.annotation.tailrec
    def apply(lines:List[String], result:Map[String,String]):Map[String,String] = {
      if (lines.isEmpty) result
      else{
        val path = lines.last.split(" ")(0)
        val blob = lines.last.split(" ")(1)
        apply(lines.init,result+(path -> blob))
      }
    }
    apply(getLinesList,Map())
  }

  /**
    * function getBlobFromFilePath
    * @param path : the file path we want the blob
    * @return the blob_name (file content encrypted) which is on the same line of file path in index file
    */
  def getBlobFromFilePath(path:String):String={
    //get the first line in index file where the path file is written
    val line_blob = getLineWithPattern(path)
    //if the line exists, retrieve the part of the line which contains blob name
    if(line_blob.isDefined){
      line_blob.get.split(" ")(1)
    }
    else ""
  }

  def getFileFromPath(path:String):Option[String]={
    val files_list =getAllFilesPath.filter(element => element == path)
    files_list.headOption
  }

  def getLinesWithoutPath(path:String):String = {
    @scala.annotation.tailrec
    def apply(lines:List[String], result:String):String = {
      if(lines.isEmpty) result
      else if(lines.head.split(" ")(0)==path) apply(lines.tail,result)
      else apply(lines.tail,result+lines.head+"\n")
    }
    apply(getLinesList,"")
  }

  /**
    * function getBlobFileByName
    * @param name : the name of blob file we are looking for
    * @param actual_directory_path : sgit repo path
    * @return None if the file does'nt exist, else Some(<blob_file>)
    */
  def getBlobFileByName(name:String, actual_directory_path:String): Option[FileHandler] = {
    val blob_file = new FileHandler(new File(actual_directory_path+"/.sgit/objects/blobs/"+name))
    if(blob_file.existFile()) Some(blob_file)
    else None
  }

  /**
    * function getBlobLineToInsert
    * @param path_file : the path of file we want to insert in tree
    * @return the line to insert in a tree, format : blob <blob_name> <file_name>
    */
  def getBlobLineToInsert(path_file:String):String={
    "\n"+"blob "+getBlobFromFilePath(path_file)+" "+path_file.split("/").last
  }

  /**
    * function getTreeLineToInsert
    * @param path_dir : the path of directory (which will be a tree) we want to insert in tree
    * @param actual_directory_path : the repository of sgit
    * @return the line to insert in a tree, format : tree <tree_name>
    */
  def getTreeLineToInsert(path_dir:String, actual_directory_path:String):String={
    "\n"+"tree "+getTree(path_dir.substring(0,path_dir.length-1),actual_directory_path)+" "+path_dir
  }

  /**
    * function getAllBlobsLineToInsert
    * @param list_files : list of path files to add with blobs in tree
    * @param contentTree : the blobs content in the tree which have to be returned
    * @return the blob lines in tree to insert
    */
  def getAllBlobsLineToInsert(list_files:List[String],contentTree:String):String={
    if(list_files.isEmpty) contentTree
    else getAllBlobsLineToInsert(list_files.tail,contentTree+getBlobLineToInsert(list_files.head))
  }

  /**
    * function getAllTreesLineToInsert
    * @param list_dir : all paths of directories to insert (as tree) in tree
    * @param contentTree : the trees content in the tree which have to be returned
    * @param actual_directory_path : the repository of sgit
    * @return
    */
  def getAllTreesLineToInsert(list_dir:List[String],contentTree:String,actual_directory_path:String):String={
    if(list_dir.isEmpty) contentTree
    else getAllTreesLineToInsert(list_dir.tail,contentTree+getTreeLineToInsert(list_dir.head,actual_directory_path),actual_directory_path)
  }

  /**
    * function getTree
    * @param path : the path from which we want to get the tree
    * @param actual_directory_path : the repository of sgit
    * @return the tree name which is it content encryption
    */
  def getTree(path:String, actual_directory_path:String):String={
    //get the blobs lines to insert in tree
    val blobs_content = getAllBlobsLineToInsert(getFilesFromPath(path),"")


    //get the trees lines to insert in tree
    val trees_content = getAllTreesLineToInsert(getDirPathFromPath(path),"",actual_directory_path)

    //encrypt the content for tree naming
    val tree_name = Encryption.sha1(blobs_content+trees_content)
    //create the tree file in .sgit/objects/trees
    val tree_file = new FileHandler(new File(actual_directory_path+"/.sgit/objects/trees/"+tree_name))

    if(!tree_file.existFile()) {
      tree_file.createFile()
      tree_file.addContent(blobs_content + trees_content, appendContent = false)
    }
    tree_name
  }

}

import java.io.File

object Index{


  /**
    * function getElementsFromPath
    * @param path : the path we want the elements (directories and files)
    * @param listPath : the list of paths where we are looking the path
    * @param listElements : the elements which have to be returned
    * @return : the list of elements found in path
    */
  def getElementsFromPath(path:String,listPath:List[String],listElements:List[String]):List[String] ={
    if(listPath.isEmpty) listElements
    else{
      //if the actual path file contains the path
      if(((path+"/").r findFirstIn  listPath.head).isDefined){
        val filePathFromDirPath = listPath.head.replace(path,"")

        if(filePathFromDirPath.split("/").length==2 ){
          getElementsFromPath(path,listPath.tail,listElements:+listPath.head)
        }
        else getElementsFromPath(path,listPath.tail,listElements)
      }
      else getElementsFromPath(path,listPath.tail,listElements)
    }
  }

  /**
    * function concatPath
    * @param path : a list of path we want to concat
    * @param index : an integer to see when we stop the concatenation
    * @return : the path concatenated
    */
  def concatPath(path:List[String],index:Int):String = {
    if(index==0) ""
    else path.head.concat("/"+concatPath(path.tail,index-1))
  }

  /**
    * function getPathAndSubPath
    * @param path : the path we want all sub paths
    * @return a list of paths : the path and its sub paths
    */
  def getPathAndSubPath(path:String):List[String] = {
    def apply(pathSplit:List[String],listPath:List[String],splitIndex:Int):List[String] = {
      if(splitIndex==pathSplit.length) listPath
      else apply(pathSplit,listPath:+concatPath(pathSplit,splitIndex),splitIndex+1)
    }
    apply(path.split("/").toList,List[String](),1)
  }

  /**
    *
    * @param listPath : the list of path from which we want all sub paths
    * @return list of paths : all the sub paths from paths of the list
    */
  def getAllPathAndSubPath(listPath:List[String]):List[String]={
    def apply(listPath: List[String],listAllPath:List[String]):List[String]={
      if(listPath.isEmpty) listAllPath.distinct
      else apply(listPath.tail,listAllPath++getPathAndSubPath(listPath.head))
    }
    apply(listPath,List[String]())
  }






}

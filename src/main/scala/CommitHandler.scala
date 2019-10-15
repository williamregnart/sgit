import java.io.File

class CommitHandler(override val f:File) extends FileHandler(f) {

  /**
    * function getTree
    * @return tree_name of the commit which is on the line starting with "tree"
    */
  def getTree:String={
    val line = getLineWithPattern("tree").get
    line.split(" ")(1).replace("\n","")
  }

  /**
    * function getParentTree
    * @return parentTree_name of the commit which is on the line starting with "parentTree"
    */
  def getParentTree:Option[String]={
    val line = getLineWithPattern("parentTree").get
    val parentTree = line.split(" ")(1).replace("\n","")
    if(parentTree=="None") None
    else Some(parentTree)
  }

  /**
    * function getDate
    * @return date of the commit which is on the line starting with "date"
    */
  def getDate:String={
    val line = getLineWithPattern("date").get
    line.split(" ")(1).replace("\n","")
  }


}

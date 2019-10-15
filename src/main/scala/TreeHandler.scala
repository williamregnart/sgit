import java.io.File


class TreeHandler(override val f:File) extends FileHandler(f) {

  def getIndex(actual_path:String,actual_directory:File):String = {

    def apply(lines_tree:List[(String,String,String)],result:String):String = {
      if(lines_tree.isEmpty) result

      else if(lines_tree.head._1 == "blob"){
        val line_to_add = actual_path+lines_tree.head._3+" "+lines_tree.head._2

        if(lines_tree.tail.isEmpty) {
          if (line_to_add.split("/").length == 1) apply(lines_tree.tail, result + "/" + line_to_add)
          else apply(lines_tree.tail, result + line_to_add)
        }
        else{
          if (line_to_add.split("/").length == 1) apply(lines_tree.tail, result + "/" + line_to_add+"\n")
          else apply(lines_tree.tail, result + line_to_add+"\n")
        }
      }
      else{
        val tree_name = lines_tree.head._2
        val path_of_tree = lines_tree.head._3
        val tree_file = new TreeHandler(new File(actual_directory.getPath+"/.sgit/objects/trees/"+tree_name))
        apply(lines_tree.tail,result+tree_file.getIndex(actual_path+path_of_tree,actual_directory))
      }
    }
    apply(getLinesTree,"")
  }

  def getLinesTree:List[(String,String,String)] = {
    def apply(listLines: List[String], result: List[(String, String, String)]): List[(String, String, String)] = {
      if (listLines.isEmpty) result
      else{
        println("----------------------")
        println(listLines.head)
        val split_line = listLines.head.split(" ")
        apply(listLines.tail,result:+(split_line(0),split_line(1),split_line(2)))
      }
    }
    println("LIST----------------------"+getLinesList)
    apply(getLinesList.tail,List[(String, String, String)]())
  }
}

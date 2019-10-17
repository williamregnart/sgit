package files

import java.io.File


class TreeHandler(f:File) extends FileHandler(f) {

  /**
    * function getIndex
    * @param actual_path : path of the actual tree
    * @param actual_directory : sgit repo
    * @return the content of tree transformed in index
    */
  def getIndex(actual_path:String,actual_directory:File):String = {

    def apply(lines_tree:List[(String,String,String)],result:String):String = {
      if(lines_tree.isEmpty) result

      //if the line of the tree is a blob
      else if(lines_tree.head._1 == "blob"){
        //line_to_add = "file_path blob_name
        val line_to_add = actual_path+lines_tree.head._3+" "+lines_tree.head._2

        //if it is the last line, the result has no \n
        if(lines_tree.tail.isEmpty) {
          //if the file is not in a subdirectory from repo, add a "/" before it
          if (line_to_add.split("/").length == 1) apply(lines_tree.tail, result + "/" + line_to_add)
          else apply(lines_tree.tail, result + line_to_add)
        }
        //if it is not the last line
        else{
          if (line_to_add.split("/").length == 1) apply(lines_tree.tail, result + "/" + line_to_add+"\n")
          else apply(lines_tree.tail, result + line_to_add+"\n")
        }
      }
      //if the line of the tree is a tree
      else{
        //get the subtree file
        val tree_name = lines_tree.head._2
        val path_of_tree = lines_tree.head._3
        val tree_file = new TreeHandler(new File(actual_directory.getPath+"/.sgit/objects/trees/"+tree_name))

        //we call apply on next line and we add to result the transformation of subtree into index
        apply(lines_tree.tail,result+tree_file.getIndex(actual_path+path_of_tree,actual_directory))
      }
    }
    apply(getLinesTree,"")
  }

  /**
    * function getLinesTree
    * @return the list of lines split into 3 strings, one for element type, one for element name, one for element file path
    */
  def getLinesTree:List[(String,String,String)] = {
    def apply(listLines: List[String], result: List[(String, String, String)]): List[(String, String, String)] = {
      if (listLines.isEmpty) result
      else{
        val split_line = listLines.head.split(" ")
        apply(listLines.tail,result:+(split_line(0),split_line(1),split_line(2)))
      }
    }
    apply(getLinesList.tail,List[(String, String, String)]())
  }

  /**
    * function createDirectoryFromTree : recreate the repo based on tree
    * @param actual_path : path of actual tree
    * @param actual_directory : sgit repo
    */
  def createDirectoryFromTree(actual_path:String, actual_directory:File):Unit = {
    def apply(lines_tree:List[(String,String,String)]): Boolean = {
      if(lines_tree.isEmpty) true

      //if the line of the tree is a blob
      else if(lines_tree.head._1 == "blob") {

        //get the entire path of the file
        val file_path = actual_path +"/" +lines_tree.head._3
        //get it content
        val blob_file = new FileHandler(new File(actual_directory.getPath + "/.sgit/objects/blobs/" + lines_tree.head._2))
        val file_content = blob_file.getContent.replace("\n", "")

        //create the file with it content
        val file = new FileHandler(new File(file_path))
        file.createFile()
        file.addContent(file_content, appendContent = false)

        //go to next line of tree
        apply(lines_tree.tail)
      }

      //if the line of the tree is a tree
      else{
        //create the directory
        val dir_path = actual_path + lines_tree.head._3
        new File(dir_path).mkdir()

        //get the subtree file
        val tree_name = lines_tree.head._2
        val tree_file = new TreeHandler(new File(actual_directory.getPath+"/.sgit/objects/trees/"+tree_name))

        //create the new subdirectory
        tree_file.createDirectoryFromTree(dir_path,actual_directory)

        apply(lines_tree.tail)
      }
    }
    apply(getLinesTree)
  }
}

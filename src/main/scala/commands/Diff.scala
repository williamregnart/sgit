package commands

import java.io.File

import files.{CommitHandler, FileHandler, IndexHandler, TreeHandler}

import scala.collection.immutable.TreeMap

object Diff {

  /**
    * function getDiffTab
    * @param file1_lines : the file we want the missing lines compare to file 2
    * @param file2_lines : the file we compare the lines from file 1
    * @return a matrix of integer which shows the lines in file 2 missing in file 1
    */
  def getDiffTab(file1_lines:List[String],file2_lines:List[String]):Map[(Int,Int),Int] = {

    @scala.annotation.tailrec
    def apply(line:Int, column:Int, map_result:Map[(Int,Int),Int]): Map[(Int,Int),Int] = {

      //if all the lines of file 1 have been visited, the matrix can be return
      if(line == file1_lines.length+1) map_result

      //if all the lines of file 2 have been visited, we start again with next line of file 1
      else if(column == file2_lines.length+1) apply(line + 1,0,map_result)

      //to avoid out of bound, first line and column of the matrix are 0
      else if(line == 0 || column == 0){
        apply(line,column+1,map_result+((line,column)->0))
      }

      //if actual line of file 1 matches actual line of file 2, the cell takes value cell at index (line -1,column-1)
      else if(file1_lines(line-1) == file2_lines(column-1)){

        val cell = map_result((line-1,column-1)) +1
        apply(line,column+1,map_result+((line,column)->cell))
      }
      //else, cell takes value of maximum between cell at index(line-1,column) and cell at index(line,column-1)
      else{
        val cell = Integer.max(map_result(line-1,column),map_result(line,column-1))
        apply(line,column+1,map_result+((line,column)->cell))
      }
    }
    val result = apply(0,0,Map[(Int,Int),Int]())
    TreeMap(result.toSeq:_*)
  }

  def getLineTab(tab:Map[(Int,Int),Int],line:Int):List[Int]= {
    def apply(tab:Map[(Int,Int),Int],result: List[Int]): List[Int] = {
      if (tab.isEmpty) result
      else if (tab.head._1._1 == line) apply(tab.tail, result :+ tab.head._2)
      else apply(tab.tail, result)
    }
    apply(tab,List[Int]())
  }

  /**
    * function getAddedLines
    * @param file1_lines : lines of file 1
    * @param file2_lines lines of file 2
    * @return the lines in file 1 which are not in file 2
    */
  def getAddedLines(file1_lines:List[String],file2_lines:List[String]):List[(Int,String)]={
    def apply(tab:Map[(Int,Int),Int],line:Int,result:List[(Int,String)]):List[(Int,String)] ={

      //if we have visited all the file 1 lines, return result
      if(line == file1_lines.length+1) result

      //a line is added on file 1 if the tab line is equals to tab line-1
      else if(getLineTab(tab,line)==getLineTab(tab,line-1)){
        val line_added = (line,file1_lines(line-1))
        apply(tab,line+1,result:+line_added)
      }
      else apply(tab,line+1,result)
    }
    val tab = getDiffTab(file1_lines,file2_lines)
    //begin at line 1 of tab because line 0 contains only O values
    apply(tab,1,List[(Int,String)]())
  }

  def getAddedLinesWithoutIndexLine(file1_lines:List[String],file2_lines:List[String]):List[String]={
    getAddedLines(file1_lines,file2_lines).map(e => e._2)
  }

  def getDiffBetweenFiles(new_file:Option[FileHandler], new_file_name:String, old_file:Option[FileHandler], old_file_name:String):List[String] = {

    def apply(diff_lines_file:List[(Int,String)],added:Boolean, result:List[String]):List[String] = {

      if(diff_lines_file.isEmpty) result

      else{
        if(added){
          val line_added = "+Line "+diff_lines_file.head._1+" : "+diff_lines_file.head._2
          apply(diff_lines_file.tail,added,result:+line_added)
        }
        else{
          val line_deleted = "-Line "+diff_lines_file.head._1+" : "+diff_lines_file.head._2
          apply(diff_lines_file.tail,added,result:+line_deleted)
        }
      }
    }


    if(new_file.isDefined && old_file.isDefined){
      val lines_added = apply(getAddedLines(new_file.get.getLinesList,old_file.get.getLinesList),added = true,List[String]())
      val lines_deleted = apply(getAddedLines(old_file.get.getLinesList,new_file.get.getLinesList),added = false,List[String]())
      List[String]("--- a"+old_file_name,"+++b"+new_file_name,"")++lines_deleted++List[String]("")++lines_added
    }
    else if(new_file.isDefined){
      val lines_added = apply(getAddedLines(new_file.get.getLinesList,List[String]("")),added = true,List[String]())
      List[String]("--- a/null","+++b"+new_file_name,"")++lines_added
    }
    else if(old_file.isDefined){
      val lines_deleted = apply(getAddedLines(old_file.get.getLinesList,List[String]("")),added = false,List[String]())
      List[String]("--- a"+old_file_name,"+++b/null","")++lines_deleted
    }
    else{
      Nil
    }
  }

  def getStatDiffBetweenFiles(new_file:Option[FileHandler], new_file_name:String, old_file:Option[FileHandler], old_file_name:String): String = {
    if(new_file.isDefined && old_file.isDefined){
      val lines_added = getAddedLines(new_file.get.getLinesList,old_file.get.getLinesList)
      val lines_deleted = getAddedLines(old_file.get.getLinesList,new_file.get.getLinesList)
      new_file_name+" | "+lines_added.length+" (+) , "+lines_deleted.length+" (-)"
    }
    else if(new_file.isDefined){
      val lines_added = getAddedLines(new_file.get.getLinesList,List[String](""))
      new_file_name+" | "+lines_added.length+" (+) , 0 (-)"
    }
    else if(old_file.isDefined){
      val lines_deleted = getAddedLines(old_file.get.getLinesList,List[String](""))
      new_file_name+" | 0 (+) , "+lines_deleted.length+" (-)"
    }
    else{
      ""
    }
  }


  /**
    * function getDiffBetweenIndexes
    * @param new_index : actual index file
    * @param old_index : old index file
    * @param actual_directory : sgit repo
    * @return list of modifications between old and new index
    */
  def getDiffBetweenIndexes(new_index:IndexHandler,old_index:IndexHandler,actual_directory:File,stat:Boolean):List[String]={

    //retrieve the modified,added, and deleted lines between old and new index
    val lines_modified_new_index = Diff.getAddedLinesWithoutIndexLine(new_index.getLinesList,old_index.getLinesList)
    val lines_modified_old_index = Diff.getAddedLinesWithoutIndexLine(old_index.getLinesList,new_index.getLinesList)

    /**
      * function getAddedAndModifiedFiles
      *
      * @param lines_modified_new_index : lines in new index which not exists or have been modified in old index
      * @param result : the result
      * @return list of addition and modification between new and old index
      */
    def getAddedAndModifiedFiles(lines_modified_new_index:List[String],result : List[String]):List[String] = {

      if(lines_modified_new_index.isEmpty) result
      else{

        //the file path of the line in new index
        val file_in_new_index = lines_modified_new_index.head.split(" ")(0)
        //blob name of this file
        val blob_name_in_new_index = lines_modified_new_index.head.split(" ")(1)
        //blob file of this file
        val blob_file_new_index = new_index.getBlobFileByName(blob_name_in_new_index,actual_directory)

        //if file is not added but modified (exist in old index)
        if(old_index.getFileFromPath(file_in_new_index).isDefined){
          //get the blob of file in old index
          val blob_name_in_old_index = old_index.getBlobFromFilePath(file_in_new_index)
          val blob_file_old_index = old_index.getBlobFileByName(blob_name_in_old_index,actual_directory)

          //get the modifications between the blobs from new and old index

          if(stat){
            val modification = Diff.getStatDiffBetweenFiles(blob_file_new_index,file_in_new_index,blob_file_old_index,file_in_new_index)

            getAddedAndModifiedFiles(lines_modified_new_index.tail,result++List[String](modification,""))
          }
          else {
            val modification = Diff.getDiffBetweenFiles(blob_file_new_index, file_in_new_index, blob_file_old_index, file_in_new_index)
            getAddedAndModifiedFiles(lines_modified_new_index.tail, result ++ modification :+ "")
          }
        }
        //if file is added (not exists in old index)
        else{
          if(stat){
            val modification = Diff.getStatDiffBetweenFiles(blob_file_new_index,file_in_new_index,None,"null")
            getAddedAndModifiedFiles(lines_modified_new_index.tail,result++List[String](modification,""))
          }
          else{
            val modification = Diff.getDiffBetweenFiles(blob_file_new_index,file_in_new_index,None,"null")
            getAddedAndModifiedFiles(lines_modified_new_index.tail,result++modification:+"")
          }
        }
      }
    }

    /**
      * function getDeletedFiles
      * @param lines_modified_old_index : lines in old index which not exists or have been modified in new index
      * @param result : the result
      * @return list of suppression between new and old index
      */
    def getDeletedFiles(lines_modified_old_index:List[String],result:List[String]) : List[String] = {
      if (lines_modified_old_index.isEmpty) result
      else {
        val file_in_old_index = lines_modified_old_index.head.split(" ")(0)
        //if file in old index exists in new index, it has been modified and is not handled by the function
        if (new_index.getLineWithPattern(file_in_old_index).isDefined) getDeletedFiles(lines_modified_old_index.tail, result)

        else {
          val blob_name_in_old_index = lines_modified_old_index.head.split(" ")(1)
          //blob file of this file
          val blob_file_old_index = old_index.getBlobFileByName(blob_name_in_old_index, actual_directory)

          if(stat){
            val suppression = Diff.getStatDiffBetweenFiles(None, "null", blob_file_old_index, file_in_old_index)
            getDeletedFiles(lines_modified_old_index.tail, result ++ List[String](suppression,""))
          }
          val suppression = Diff.getDiffBetweenFiles(None, "null", blob_file_old_index, file_in_old_index)
          getDeletedFiles(lines_modified_old_index.tail, result ++ suppression :+ "")
        }
      }
    }

    val addition_and_modifications = getAddedAndModifiedFiles(lines_modified_new_index,List[String]())
    val suppressions = getDeletedFiles(lines_modified_old_index,List[String]())

    addition_and_modifications++suppressions
  }

  /**
    * function getDiffBetweenCommits
    * @param new_commit : new commit file
    * @param old_commit : old commit file
    * @param actual_directory : sgit repo
    * @return the list of modifications between the two commits
    */
  def getDiffBetweenCommits(new_commit: CommitHandler,old_commit:CommitHandler,actual_directory:File,stat:Boolean):List[String]={

    //get the trees of new and last commits
    val new_tree_name = new_commit.getTree
    val new_tree_file = new TreeHandler(new File(actual_directory.getPath+"/.sgit/objects/trees/"+new_tree_name))
    val old_tree_name = old_commit.getTree
    val old_tree_file = new TreeHandler(new File(actual_directory.getPath+"/.sgit/objects/trees/"+old_tree_name))

    //get the index content of trees
    val new_index_content = new_tree_file.getIndex("",actual_directory)
    val old_index_content = old_tree_file.getIndex("",actual_directory)

    //create new and old index files with their content
    val new_index_file = new IndexHandler(new File(actual_directory.getPath+"/.sgit/NEW_INDEX"))
    new_index_file.createFile()
    new_index_file.addContent(new_index_content,appendContent = false)

    val old_index_file = new IndexHandler(new File(actual_directory.getPath+"/.sgit/OLD_INDEX"))
    old_index_file.addContent(old_index_content,appendContent = false)
    old_index_file.createFile()

    //get the diff between indexes
    val diff = getDiffBetweenIndexes(new_index_file,old_index_file,actual_directory,stat)


    new_index_file.deleteFile()
    old_index_file.deleteFile()

    diff
  }
}

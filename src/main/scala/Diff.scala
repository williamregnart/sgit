import scala.collection.immutable.TreeMap
import scala.collection.mutable

object Diff {

  /**
    * function getDiffTab
    * @param file1_lines : the file we want the missing lines compare to file 2
    * @param file2_lines
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

  def getDiffBetweenFiles(file1:Option[FileHandler],file2:Option[FileHandler]):List[String] = {

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
    if(file1.isDefined && file2.isDefined){
      val lines_added = apply(getAddedLines(file1.get.getLinesList,file2.get.getLinesList),added = true,List[String]())
      val lines_deleted = apply(getAddedLines(file2.get.getLinesList,file1.get.getLinesList),added = false,List[String]())
      List[String]("")
    }
  }
}

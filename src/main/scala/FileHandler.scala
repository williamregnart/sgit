

import java.io._
import java.security.MessageDigest

import javax.xml.bind.DatatypeConverter

case class FileHandler(f: File) {
  private val file = f

  def getName:String={
    f.getName
  }

  def getPath:String={
    f.getPath
  }

  def createFile():Unit={
    file.createNewFile()
  }

  def existFile():Boolean={
    file.exists()
  }

  def deleteFile():Unit={
    file.delete()
  }

  def getPathFromDir(actual_directory:File):String={
    getPath.replace(actual_directory.getPath,"").replace("\\","/")
  }

  def getUniqueKey: String = {
    val msdDigest = MessageDigest.getInstance("SHA-1")
    msdDigest.update(getContent.getBytes("UTF-8"), 0, getContent.length)
    val sha1 = DatatypeConverter.printHexBinary(msdDigest.digest)
    sha1
  }
  def getContent:String={
    @scala.annotation.tailrec
    def apply(bufferedReader: BufferedReader, content:String):String= {
      val line = bufferedReader.readLine()
      if (line == null) content
      else apply(bufferedReader, content.concat(line + "\n"))
    }
    val file_reader = new FileReader(file)
    val buffered_reader = new BufferedReader(file_reader)
    val result = apply(buffered_reader,"")
    buffered_reader.close()
    file_reader.close()
    result
  }

  def cleanContent(): Unit ={
    val writer = new FileWriter(file,false)
    writer.write("")
    writer.close()
  }

  def addContent(content:String,appendContent:Boolean):Unit={
    val writer = new FileWriter(file,appendContent)
    writer.write(content)
    writer.close()
  }

  def clearContent():Unit={
    addContent("",appendContent = false)
  }

  def existPatternInFile(pattern:String):Boolean = {
    (pattern.r findFirstIn getContent).isDefined
  }

  def getLinesList:List[String] = {
    @scala.annotation.tailrec
    def apply(bufferedReader: BufferedReader, list:List[String]):List[String]= {
      val line = bufferedReader.readLine()
      if (line == null) list
      else apply(bufferedReader, list:+line)
    }
    val fileReader = new FileReader(file)
    val bufferedReader = new BufferedReader(fileReader)
    val result = apply(bufferedReader,List[String]())
    bufferedReader.close()
    fileReader.close()
    result
  }

  def getLineWithPattern(pattern:String):Option[String] = {
    @scala.annotation.tailrec
    def apply(bufferedReader: BufferedReader, pattern:String):Option[String] ={
      val line = bufferedReader.readLine()
      if(line == null) None
      else{
        if ((pattern.r findFirstIn line).isDefined)
          Some(line)
        else apply(bufferedReader,pattern)
      }
    }
    val fileReader = new FileReader(file)
    val bufferedReader = new BufferedReader(fileReader)
    val result = apply(bufferedReader,pattern)
    bufferedReader.close()
    fileReader.close()
    result
  }

  def replaceLineByContent(line:String,content:String):Unit={
    val newContent = getContent.replaceFirst(line,content)
    addContent(newContent,appendContent = false)
  }

}


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

  def getPathFromActualDir:String={
    f.getPath.replace(new File(System.getProperty("user.dir")).getPath,"").replace("\\","/")
  }

  def getBufferedReader:BufferedReader = {
    new BufferedReader(new FileReader(file))
  }

  def getBufferedWriter:BufferedWriter = {
    new BufferedWriter(new FileWriter(file))
  }

  def getUniqueKey: String = {
    val msdDigest = MessageDigest.getInstance("SHA-1")
    msdDigest.update(getContent.getBytes("UTF-8"), 0, getContent.length)
    val sha1 = DatatypeConverter.printHexBinary(msdDigest.digest)
    sha1
  }
  def getContent:String={
    def apply(bufferedReader: BufferedReader,content:String):String= {
      val line = bufferedReader.readLine()
      if (line == null) content
      else apply(bufferedReader, content.concat(line + "\n"))
    }
    apply(getBufferedReader,"")
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

  def linesList:List[String] = {
    def apply(bufferedReader: BufferedReader,list:List[String]):List[String]= {
      val line = bufferedReader.readLine()
      if (line == null) list
      else apply(bufferedReader, list:+line)
    }
    apply(getBufferedReader,List[String]())
  }

  def getLineWithPattern(pattern:String):Option[String] = {
    def apply(bufferedReader: BufferedReader,pattern:String):Option[String] ={
      val line = bufferedReader.readLine()
      if(line == null) None
      else{
        if ((pattern.r findFirstIn line).isDefined)
          Some(line)
        else apply(bufferedReader,pattern)
      }
    }
    apply(getBufferedReader,pattern)
  }

  def replaceLineByContent(line:String,content:String):Unit={
    val newContent = getContent.replaceFirst(line,content)
    addContent(newContent,appendContent = false)
  }

}
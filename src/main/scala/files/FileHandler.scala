package files

import java.io.{BufferedReader, File, FileReader, FileWriter}
import java.security.MessageDigest
import java.util.Scanner

import javax.xml.bind.DatatypeConverter

class FileHandler(f: File) {
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

  def getPathFromDir(actual_directory_path:String):String={
    getPath.replace(actual_directory_path,"").replace("\\","/")
  }

  def getUniqueKey: String = {
    val msdDigest = MessageDigest.getInstance("SHA-1")
    if(getContent!="") {
      msdDigest.update(getContent.getBytes("UTF-8"), 0, getContent.length)
    }
    else{
      msdDigest.update(" ".getBytes("UTF-8"), 0, 1)
    }
    val sha1 = DatatypeConverter.printHexBinary(msdDigest.digest)
    sha1
  }

  def getContent:String={
    @scala.annotation.tailrec
    def apply(scanner: Scanner, content:String):String= {
      if (!scanner.hasNextLine) content
      else apply(scanner, content.concat(scanner.nextLine() + "\n"))
    }
    val scanner = new Scanner(file)
    val result = apply(scanner,"")
    scanner.close()
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
    def apply(scanner: Scanner, list:List[String]):List[String]= {
      if (!scanner.hasNextLine) list
      else apply(scanner, list:+scanner.nextLine())
    }
    val scanner = new Scanner(file)
    val result = apply(scanner,List[String]())
    scanner.close()
    result
  }

  def getLineWithPattern(pattern:String):Option[String] = {
    @scala.annotation.tailrec
    def apply(scanner:Scanner, pattern:String):Option[String] ={
      if(!scanner.hasNextLine) None
      else{
        val line = scanner.nextLine()
        if ((pattern.r findFirstIn line).isDefined)
          Some(line)
        else apply(scanner,pattern)
      }
    }
    val scanner = new Scanner(file)
    val result = apply(scanner,pattern)
    scanner.close()
    result
  }

  def replaceLineByContent(line:String,content:String):Unit={
    val newContent = getContent.replaceFirst(line,content)
    addContent(newContent,appendContent = false)
  }

}

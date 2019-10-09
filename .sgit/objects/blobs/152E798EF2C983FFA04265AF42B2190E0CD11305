import java.io.{BufferedReader, File, FileReader}

class Blob(key:String) {
  private val blob_name = key
  private val file = new FileHandler(new File(System.getProperty("user.dir")+"/.git/objects/blobs/"+blob_name))

  def getContent:String ={
    file.getContent
  }

  def getContent(bufferedReader: BufferedReader,content:String):String={
    val line = bufferedReader.readLine()
    if (line==null) content
    else getContent(bufferedReader,content.concat(line+"/n"))
  }

  def existsBlob(fileList:Array[File],blob_name:String): Boolean = {
      if (fileList.isEmpty) false
      else {
        if (fileList.head.getName() == blob_name) true
        else existsBlob(fileList.tail, blob_name)
    }
  }
}

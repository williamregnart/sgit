import java.io.File

import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

import scala.reflect.io.Directory

class AddTest extends FunSpec with Matchers with BeforeAndAfter {

  val test_directory = new File(System.getProperty("user.dir")+"/test_directory")
  val index_file = FileHandler(new File(test_directory.getPath+"/.sgit/INDEX"))
  val blobs_directory = new File(test_directory.getPath+"/.sgit/objects/blobs")

  val directory1 = new File(test_directory.getPath+"/directory1")
  val file1 = FileHandler(new File(test_directory.getPath+"/file1"))
  val file2 = FileHandler(new File(test_directory.getPath+"/file2"))
  val file3 = FileHandler(new File(test_directory.getPath+"/directory1/file3"))

  before{
    Init.createSgit(test_directory)
    directory1.mkdir()
    file1.createFile()
    file2.createFile()
    file3.createFile()
  }
  after{
    new Directory(new File(test_directory.getPath+"/.sgit")).deleteRecursively()
    file1.deleteFile()
    file2.deleteFile()
    file3.deleteFile()
    directory1.delete()
  }

  describe("an add process") {
    it("should have initially an empty INDEX") {
      index_file.getContent shouldBe ""
    }
    it("should not createBlob of file1 and not add it to INDEX file because file1 has no content"){
      Add.addFileToIndex(file1.getPath,test_directory)
      blobs_directory.listFiles() shouldBe Array[String]()
    }
    it("should createBlob of file1 and add it to INDEX file"){
      file1.addContent("hello darkness my old friend",appendContent = true)
      val blob_expected = new File(blobs_directory.getPath+"/"+file1.getUniqueKey)
      Add.addFileToIndex(file1.getPath,test_directory)

      blobs_directory.list() should have size 1

      blob_expected.exists() shouldBe true

      index_file.getContent shouldBe file1.getPathFromActualDir+" "+file1.getUniqueKey+"\n"
    }
    it("should create a new blob and modify INDEX when file1 is added, modified, and added again"){

      file1.addContent("hello darkness",appendContent = true)
      val blob_expected1 = new File(blobs_directory.getPath+"/"+file1.getUniqueKey)
      Add.addFileToIndex(file1.getPath,test_directory)

      index_file.getContent shouldBe file1.getPathFromActualDir+" "+file1.getUniqueKey+"\n"
      //append content to file1
      file1.addContent(" my old friend",appendContent = true)
      //a new blob is expected
      val blob_expected2 = new File(blobs_directory.getPath+"/"+file1.getUniqueKey)
      Add.addFileToIndex(file1.getPath,test_directory)
      //we expect two blobs, one for file1 first version, one for it second version
      blobs_directory.list() should have size 2

      blob_expected1.exists() shouldBe true
      blob_expected2.exists() shouldBe true
      //index file should have been modified
      index_file.getContent shouldBe file1.getPathFromActualDir+" "+file1.getUniqueKey+"\n"
    }

    it("should add all files"){
      file1.addContent("hello darkness",appendContent = true)
      file2.addContent("my old",appendContent = true)
      file3.addContent("friend",appendContent = true)

      val files = Array[String](file1.getPath,file2.getPath,file3.getPath)

      Add.addFilesToIndex(files,test_directory)

      blobs_directory.list() should have size 3

      val index_file_line_1_expected = file1.getPathFromActualDir+" "+file1.getUniqueKey
      val index_file_line_2_expected = file2.getPathFromActualDir+" "+file2.getUniqueKey
      val index_file_line_3_expected = file3.getPathFromActualDir+" "+file3.getUniqueKey

      index_file.getContent shouldBe index_file_line_1_expected+"\n"+index_file_line_2_expected+"\n"+index_file_line_3_expected+"\n"
    }

    it("should remove file1 from index when deleted but keep the blob"){
      file1.addContent("hello darkness",appendContent = true)
      file2.addContent("my old",appendContent = true)
      file3.addContent("friend",appendContent = true)

      file1.deleteFile()

      //we add all files except file1 because it doesn't exist anymore
      val files = Array[String](file2.getPath,file3.getPath)

      Add.addFilesToIndex(files,test_directory)

    }
  }
}
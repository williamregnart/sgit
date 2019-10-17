package commandsTest

import java.io.File

import commands.{Add, Init}
import files.FileHandler
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

import scala.reflect.io.Directory

class AddTest extends FunSpec with Matchers with BeforeAndAfter {

  val test_directory = new File(System.getProperty("user.dir")+"/test_directory")
  val index_file = new FileHandler(new File(test_directory.getPath+"/.sgit/INDEX"))
  val blobs_directory = new File(test_directory.getPath+"/.sgit/objects/blobs")

  val directory1 = new File(test_directory.getPath+"/directory1")
  val file1 = new FileHandler(new File(test_directory.getPath+"/directory1/file1"))
  val file2 = new FileHandler(new File(test_directory.getPath+"/file2"))
  val file3 = new FileHandler(new File(test_directory.getPath+"/file3"))

  /**
    * before executing tests :
    * - make an init on the test_directory
    * - create 3 files with 1 in a subdirectory
    */
  before{
    test_directory.mkdir()
    Init.createSgit(test_directory)
    directory1.mkdir()
    file1.createFile()
    file2.createFile()
    file3.createFile()
  }

  /**
    * after executing tests :
    * - delete .sgit directory
    * - delete all files and subdirectories
    */
  after{
    new Directory(test_directory).deleteRecursively()
  }

  describe("an add process") {
    it("should have initially an empty INDEX") {
      index_file.getContent shouldBe ""
    }

    it("should not add a file with a wrong path"){
      Add.addFileToIndex("wrong/path",test_directory)
    }

    it("should not createBlob of file1 and not add it to INDEX file because file1 has no content"){
      Add.addFileToIndex("directory1/file1",test_directory)

      blobs_directory.listFiles() shouldBe Array[String]()
      index_file.getContent shouldBe ""
    }

    it("should createBlob of file1 and add it to INDEX file"){
      file1.addContent("hello darkness my old friend",appendContent = true)
      val blob_expected = new File(blobs_directory.getPath+"/"+file1.getUniqueKey)
      Add.addFileToIndex("directory1/file1",test_directory)

      blobs_directory.list() should have size 1
      blob_expected.exists() shouldBe true
      index_file.getContent shouldBe file1.getPathFromDir(test_directory)+" "+file1.getUniqueKey+"\n"
    }

    it("should create a new blob and modify INDEX when file1 is added, modified, and added again"){

      //write content in file1 and add it

      file1.addContent("hello darkness",appendContent = true)
      val blob_expected1 = new File(blobs_directory.getPath+"/"+file1.getUniqueKey)
      Add.addFileToIndex("directory1/file1",test_directory)

      index_file.getContent shouldBe file1.getPathFromDir(test_directory)+" "+file1.getUniqueKey+"\n"


      //append content to file1 and add it
      file1.addContent(" my old friend",appendContent = true)
      //a new blob is expected
      val blob_expected2 = new File(blobs_directory.getPath+"/"+file1.getUniqueKey)
      Add.addFileToIndex("directory1/file1",test_directory)

      //we expect two blobs, one for file1 first version, one for it second version
      blobs_directory.list() should have size 2
      blob_expected1.exists() shouldBe true
      blob_expected2.exists() shouldBe true

      //index file should have been modified
      index_file.getContent shouldBe file1.getPathFromDir(test_directory)+" "+file1.getUniqueKey+"\n"
    }

    it("should add all files"){

      //write in files 1,2,3 and add all of them
      file1.addContent("hello darkness",appendContent = true)
      file2.addContent("my old",appendContent = true)
      file3.addContent("friend",appendContent = true)
      Add.addFilesToIndex(test_directory)

      //we expect 3 blobs
      blobs_directory.list() should have size 3

      val index_file_line_1_expected = file1.getPathFromDir(test_directory)+" "+file1.getUniqueKey
      val index_file_line_2_expected = file2.getPathFromDir(test_directory)+" "+file2.getUniqueKey
      val index_file_line_3_expected = file3.getPathFromDir(test_directory)+" "+file3.getUniqueKey

      index_file.getContent shouldBe index_file_line_1_expected+"\n"+index_file_line_2_expected+"\n"+index_file_line_3_expected+"\n"
    }

    it("should remove file1 from index when deleted but keep the blob"){

      //write in file 1,2,3 and add all of them
      file1.addContent("hello darkness",appendContent = true)
      file2.addContent("my old",appendContent = true)
      file3.addContent("friend",appendContent = true)

      Add.addFilesToIndex(test_directory)

      //delete file from test_directory
      file1.deleteFile()

      //we add all file to have the file1 delete from index

      Add.addFilesToIndex(test_directory)

      //blob for file1 should always exist
      blobs_directory.list() should have size 3

      val index_file_line_1_expected = file2.getPathFromDir(test_directory)+" "+file2.getUniqueKey
      val index_file_line_2_expected = file3.getPathFromDir(test_directory)+" "+file3.getUniqueKey


      //index file should have only file 2 and 3
      index_file.getContent shouldBe index_file_line_1_expected+"\n"+index_file_line_2_expected+"\n"
    }
  }
  describe("remove function"){
    it("should remove file1 from index when deleted but keep the blob") {

      //write in file 1,2,3 and add all of them
      file1.addContent("hello darkness", appendContent = true)
      file2.addContent("my old", appendContent = true)
      file3.addContent("friend", appendContent = true)

      Add.addFilesToIndex(test_directory)

      Add.removeFileToIndex(file2.getPathFromDir(test_directory), test_directory)

      val index_file_line_1_expected = file1.getPathFromDir(test_directory) + " " + file1.getUniqueKey
      val index_file_line_2_expected = file3.getPathFromDir(test_directory) + " " + file3.getUniqueKey

      index_file.getContent shouldBe index_file_line_1_expected + "\n" + index_file_line_2_expected + "\n"
    }
  }
}
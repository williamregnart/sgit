package filesTest

import java.io.File

import files.{DirectoryHandler, FileHandler}
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

import scala.reflect.io.Directory

class DirectoryHandlerTest extends FunSpec with Matchers with BeforeAndAfter {
  val test_directory = new File(System.getProperty("user.dir")+"/test_directory")
  val test_directory_handler = new DirectoryHandler(test_directory)
  val sgit_directory = new File(test_directory.getPath+"/.sgit")
  val directory1 = new File(test_directory.getPath+"/directory1")
  val directory2 = new File(test_directory.getPath+"/directory2")

  val file1 = new FileHandler(new File(test_directory.getPath+"/directory1/file1"))
  val file2 = new FileHandler(new File(test_directory.getPath+"/directory2/file2"))
  val file3 = new FileHandler(new File(test_directory.getPath+"/file3"))
  val file4 = new FileHandler(new File(test_directory.getPath+"/file4"))

  before{
    test_directory.mkdir()
    sgit_directory.mkdir()
    directory1.mkdir()
    directory2.mkdir()

    file1.createFile()
    file1.addContent("hello",appendContent = false)
    file2.createFile()
    file2.addContent("darkness",appendContent = false)
    file3.createFile()
    file3.addContent("my old",appendContent = false)
    file4.createFile()
    file4.addContent("friend",appendContent = false)
  }

  after{
    new Directory(test_directory).deleteRecursively()
  }

  describe("deleteFilesExcept function"){
    it("should delete all files and directories in test_directory"){

      test_directory.listFiles() should have size 5

      //delete all in test_directory
      test_directory_handler.deleteFilesExcept(List())

      test_directory.listFiles() should have size 0
    }
    it("should delete all files and directories in test_directory except .sgit"){
      test_directory_handler.deleteFilesExcept(List[String](".sgit"))

      test_directory.listFiles() should have size 1
      test_directory.listFiles().head shouldBe sgit_directory
    }
  }
}

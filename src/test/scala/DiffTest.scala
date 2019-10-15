import java.io.File

import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

import scala.reflect.io.Directory

class DiffTest extends FunSpec with Matchers with BeforeAndAfter{

  val test_directory = new File(System.getProperty("user.dir")+"/test_directory")
  val file1 = FileHandler(new File(test_directory.getPath+"/file1"))
  val file2 = FileHandler(new File(test_directory.getPath+"/file2"))
  val file3 = FileHandler(new File(test_directory.getPath+"/file3"))
  val file4 = FileHandler(new File(test_directory.getPath+"/file4"))

  before{
    test_directory.mkdir()
    file1.createFile()
    file1.addContent("hello\ndarkness\nmy\nold\nfriend",appendContent = false)

    file2.createFile()
    file2.addContent("hello\ndarkness\nmy\nfriend\nI\nlove",appendContent = false)
  }
  after{
    new Directory(test_directory).deleteRecursively()
  }

  describe("getTabFilesDiff function"){
    it("should give the good matrix of int for the difference between file 1 and 2"){
      Diff.getAddedLines(file1.getLinesList,file2.getLinesList) shouldBe List((4,"old"))
      Diff.getAddedLines(file2.getLinesList,file1.getLinesList) shouldBe List((5,"I"),(6,"love"))
    }
  }
}

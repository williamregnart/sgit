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
  describe("getDiffBetweenFiles function"){
    it("should give the lines deleted from old file and lines added from new file"){
      val old_file = Some(file2)
      val new_file = Some(file1)
      val result = Diff.getDiffBetweenFiles(new_file,"/file1",old_file,"/file2")
      val expected_result = List[String]("--- a/file2","+++b/file1","","-Line 5 : I","-Line 6 : love","","+Line 4 : old")

      result shouldBe expected_result
    }
    it("should give the lines deleted from old file because new file doesn't exist"){
      file1.addContent("hello\ndarkness",appendContent = false)
      val old_file = Some(file1)
      val new_file = None
      val result = Diff.getDiffBetweenFiles(new_file,"/null",old_file,"/file1")
      val expected_result = List[String]("--- a/file1","+++b/null","","-Line 1 : hello","-Line 2 : darkness")

      result shouldBe expected_result
    }

    it("should give the lines added from new file because old file doesn't exist"){
      file1.addContent("hello\ndarkness",appendContent = false)
      val new_file = Some(file1)
      val old_file = None
      val result = Diff.getDiffBetweenFiles(new_file,"/file1",old_file,"/null")
      val expected_result = List[String]("--- a/null","+++b/file1","","+Line 1 : hello","+Line 2 : darkness")

      result shouldBe expected_result
    }
  }
}

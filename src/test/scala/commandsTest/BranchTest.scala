package commandsTest

import java.io.File

import commands.Branch._
import commands.{Add, Commit, Init}
import files.FileHandler
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

import scala.reflect.io.Directory

class BranchTest extends FunSpec with Matchers with BeforeAndAfter{

  val test_directory = new File(System.getProperty("user.dir")+"/test_directory")

  val branches_directory = new File(test_directory.getPath+"/.sgit/refs/heads")

  val file1 = new FileHandler(new File(test_directory.getPath+"/file1"))

  /**
    * before executing tests :
    * - make an init on the test_directory
    * - create 3 files with 1 in a subdirectory
    */
  before{
    test_directory.mkdir()
    Init.createSgit(test_directory)
    file1.createFile()
    file1.addContent("hello darkness my old friend",appendContent = false)
    Add.addFilesToIndex(test_directory.getPath)
    Commit.commit(test_directory.getPath,"message")
  }

  /**
    * after executing tests :
    * - delete .sgit directory
    * - delete all files and subdirectories
    */
  after{
    new Directory(test_directory).deleteRecursively()
  }

  describe("before branchCommand function"){
    it("should exist one branch in /.sgit/refs/heads which is master"){
      branches_directory.listFiles() should have size 1
      branches_directory.list() shouldBe List[String]("master")
    }
  }

  describe("after branchCommand function"){
    it("should exist two branches in /.sgit/refs/heads with the right name"){
      executeBranchCommand("darkness",test_directory.getPath)
      branches_directory.listFiles() should have size 2
      branches_directory.list() shouldBe List[String]("darkness","master")
    }
    it("file of new branch should have the commit of actual branch (here nothing)"){
      executeBranchCommand("darkness",test_directory.getPath)

      val actual_branch_file = new FileHandler(new File(branches_directory.getPath+"/master"))
      val new_branch_file = new FileHandler(new File(branches_directory.getPath+"/darkness"))

      new_branch_file.getContent shouldBe actual_branch_file.getContent
    }
    it("file of new branch should have the commit of actual branch (after a first commit)"){
      Commit.commit(test_directory.getPath,"message")
      executeBranchCommand("darkness",test_directory.getPath)

      val actual_branch_file = new FileHandler(new File(branches_directory.getPath+"/master"))
      val new_branch_file = new FileHandler(new File(branches_directory.getPath+"/darkness"))

      new_branch_file.getContent shouldBe actual_branch_file.getContent
    }

  }
}

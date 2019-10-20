package commandsTest

import java.io.File

import commands._
import files.{DirectoryHandler, FileHandler, IndexHandler}
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

import scala.reflect.io.Directory

class CheckOutTest extends FunSpec with Matchers with BeforeAndAfter {
  val test_directory = new File(System.getProperty("user.dir")+"/test_directory")
  val test_directory_handler = new DirectoryHandler(test_directory)
  val head_file = new IndexHandler(new File(test_directory.getPath+"/.sgit/HEAD"))
  val branch_master_file = new FileHandler(new File(test_directory.getPath+"/.sgit/refs/heads/master"))
  val commits_directory = new File(test_directory.getPath+"/.sgit/objects/commits")
  val trees_directory = new File(test_directory.getPath+"/.sgit/objects/trees")

  val directory1 = new File(test_directory.getPath+"/directory1")
  val file1 = new FileHandler(new File(test_directory.getPath+"/directory1/file1"))
  val file2 = new FileHandler(new File(test_directory.getPath+"/file2"))
  val file3 = new FileHandler(new File(test_directory.getPath+"/file3"))

  /**
    * before executing tests :
    * - make an init on the test_directory
    * - create 3 files with 1 in a subdirectory
    * - make an add with all files
    */
  before{
    test_directory.mkdir()
    Init.createSgit(test_directory)
    directory1.mkdir()
    file1.createFile()
    file1.addContent("hello",appendContent = false)
    file2.createFile()
    file2.addContent("darkness",appendContent = false)
    file3.createFile()
    file3.addContent("my old friend",appendContent = false)
    Add.addFilesToIndex(test_directory.getPath)
    Commit.commit(test_directory.getPath)
    Branch.executeBranchCommand("slave",test_directory.getPath)
  }

  /**
    * after executing tests :
    * - delete .sgit directory
    * - delete all files and subdirectories
    */
  after{
    new Directory(test_directory).deleteRecursively()
  }

  describe("changeBranch function"){
    it("should switch in branch slave and the repo should be unchanged"){
      val actual_branch = head_file.getContent
      actual_branch shouldBe "master\n"

      val files_in_branch_master = test_directory_handler.getAllFilesPath
      val content_file_2 = "darkness\n"
      CheckOut.changeBranch("slave",test_directory.getPath)

      head_file.getContent shouldBe "slave\n"
      test_directory_handler.getAllFilesPath shouldBe files_in_branch_master
      file2.getContent shouldBe content_file_2
    }

    it("should create a new file in master branch and when we go on slave branch, it doesn't exist anymore"){
      val file4 = new FileHandler(new File(test_directory.getPath+"/file4"))
      file4.addContent("I've come to talk with you again",appendContent = false)

      Add.addFileToIndex("file4",test_directory.getPath)
      Commit.commit(test_directory.getPath)
      val files_in_branch_master = test_directory_handler.getAllFilesPath

      file4.existFile() shouldBe true
      CheckOut.changeBranch("slave",test_directory.getPath)
      file4.existFile() shouldBe false

      files_in_branch_master should have size 4
      test_directory_handler.getAllFilesPath should have size 3

      CheckOut.changeBranch("master",test_directory.getPath)
      file4.existFile() shouldBe true
      file4.getContent shouldBe "I've come to talk with you again\n"
    }

  }
  describe("executeCheckOutCommand function"){
    it("should checkout on the commit referenced by the tag"){
      Tag.addTag("first_commit",test_directory.getPath)

      val file4 = new FileHandler(new File(test_directory.getPath+"/file4"))
      file4.addContent("I've come to talk with you again",appendContent = false)

      Add.addFileToIndex("file4",test_directory.getPath)
      Commit.commit(test_directory.getPath)

      file4.existFile() shouldBe true

      CheckOut.executeCheckOutCommand("first_commit",test_directory.getPath)

      val actual_branch = Branch.getActualBranch(test_directory.getPath)

      actual_branch shouldBe "detached"

      file4.existFile() shouldBe false
    }
    it("should checkout on the commit given"){
      val first_commit = Commit.getLastCommitFromBranch("master",test_directory.getPath)

      val file4 = new FileHandler(new File(test_directory.getPath+"/file4"))
      file4.addContent("I've come to talk with you again",appendContent = false)

      Add.addFileToIndex("file4",test_directory.getPath)
      Commit.commit(test_directory.getPath)

      file4.existFile() shouldBe true

      val detached_branch = new File(test_directory.getPath+"/.sgit/refs/heads/detached")

      detached_branch.exists() shouldBe false

      CheckOut.executeCheckOutCommand(first_commit,test_directory.getPath)

      detached_branch.exists() shouldBe true

      val actual_branch = Branch.getActualBranch(test_directory.getPath)

      actual_branch shouldBe "detached"

      file4.existFile() shouldBe false

      CheckOut.executeCheckOutCommand("master",test_directory.getPath)

      detached_branch.exists() shouldBe false

    }

    it("should forbid the checkout with uncommitted files"){

      val actual_branch = head_file.getContent
      val file4 = new FileHandler(new File(test_directory.getPath+"/file4"))
      file4.addContent("I've come to talk with you again",appendContent = false)

      Add.addFileToIndex("file4",test_directory.getPath)

      CheckOut.executeCheckOutCommand("slave",test_directory.getPath)

      head_file.getContent shouldBe actual_branch
    }
  }
}

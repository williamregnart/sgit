package commandsTest

import java.io.File

import commands.{Add, Commit, Init}
import files.{FileHandler, IndexHandler}
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

import scala.reflect.io.Directory

class CommitTest extends FunSpec with Matchers with BeforeAndAfter{
  val test_directory = new File(System.getProperty("user.dir")+"/test_directory")
  val index_file = new IndexHandler(new File(test_directory.getPath+"/.sgit/INDEX"))
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
  }

  /**
  * after executing tests :
    * - delete .sgit directory
    * - delete all files and subdirectories
  */
  after{
    new Directory(test_directory).deleteRecursively()
  }

  describe("the trees directory") {
    it("should have no tree  before first commit") {
      trees_directory.list() should have size 0
    }
    it("should have 2 trees after first commit one for directory1, one for test_directory") {
      Commit.commit(test_directory.getPath,"message")
      trees_directory.list() should have size 2
    }
    it("should have 4 trees after second commit"){
      Commit.commit(test_directory.getPath,"message")
      file1.addContent("dd",appendContent = false)
      Add.addFilesToIndex(test_directory.getPath)
      Commit.commit(test_directory.getPath,"message")
      trees_directory.list() should have size 4
    }
  }
  describe("the commits directory"){
    it("should have 1 commit after first commit"){
      Commit.commit(test_directory.getPath,"message")
      commits_directory.list() should have size 1
    }
    it("should have, after first commit a file with the right commit name"){
      Commit.commit(test_directory.getPath,"message")
      val commit_name = Commit.getLastCommitFromBranch("master",test_directory.getPath)
      new File(commits_directory.getPath+"/"+commit_name).exists() shouldBe true
    }
  }
  describe("branch file"){
    it("should have the last commit in content"){
      Commit.commit(test_directory.getPath,"message")
      branch_master_file.getContent shouldBe Commit.getLastCommitFromBranch("master",test_directory.getPath)+"\n"
    }
  }

  describe("the commit file"){
    it("should have the right content"){
      Commit.commit(test_directory.getPath,"message")
      val commit_name = Commit.getLastCommitFromBranch("master",test_directory.getPath)
      val commit_file = new FileHandler(new File(commits_directory.getPath+"/"+commit_name))
      val commit_content = commit_file.getContent

      val expected_commit_content = "tree "+index_file.getTree("",test_directory.getPath)+"\nparentTree None\ndate "+Commit.getDateToday+"\n message\n"
      commit_content shouldBe expected_commit_content
    }
  }
  describe("the second commit process"){
    it("should not create a new commit if nothing changed"){
      Commit.commit(test_directory.getPath,"message")
      commits_directory.listFiles() should have size 1
      Commit.commit(test_directory.getPath,"message")
      commits_directory.listFiles() should have size 1
    }


  }
}

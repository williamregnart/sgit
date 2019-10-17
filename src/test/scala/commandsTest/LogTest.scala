package commandsTest

import java.io.File

import commands._
import files.{CommitHandler, FileHandler, IndexHandler, TreeHandler}
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

import scala.reflect.io.Directory

class LogTest extends FunSpec with Matchers with BeforeAndAfter{
  val test_directory = new File(System.getProperty("user.dir")+"/test_directory")
  val index_file = new IndexHandler(new File(test_directory.getPath+"/.sgit/INDEX"))
  val commits_directory = new File(test_directory.getPath+"/.sgit/objects/commits")
  val trees_directory = new File(test_directory.getPath+"/.sgit/objects/trees")

  val directory1 = new File(test_directory.getPath+"/directory1")
  val file1 = new FileHandler(new File(test_directory.getPath+"/directory1/file1"))
  val file2 = new FileHandler(new File(test_directory.getPath+"/file2"))
  val file3 = new FileHandler(new File(test_directory.getPath+"/file3"))
  val file4 = new FileHandler(new File(test_directory.getPath+"/file4"))

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

    Add.addFilesToIndex(test_directory)
    Commit.commit(test_directory)
  }

  after{
    new Directory(test_directory).deleteRecursively()
  }

  describe("getLog function without option -p"){
    it("should have two commits on the result"){
      file4.createFile()
      file4.addContent("for new commit",appendContent = false)
      Add.addFilesToIndex(test_directory)
      Commit.commit(test_directory)
      val commit_size = 4
      val nb_commit = 2

      Log.getLog(test_directory,p = false, stat = false) should have size commit_size * nb_commit
    }
    it("should have at first a commit with a parentTree and at last a commit without parentTree"){
      file4.createFile()
      file4.addContent("for new commit",appendContent = false)
      Add.addFilesToIndex(test_directory)
      Commit.commit(test_directory)

      val log_result = Log.getLog(test_directory,p = false,stat = false)
      val commit_size = 4
      val line_parent_tree = 2
      val first_commit = log_result(line_parent_tree)
      val last_commit = log_result(line_parent_tree+commit_size)

      "parentTree None".r findFirstIn first_commit shouldBe None
      "parentTree None".r findFirstIn last_commit should not be None
    }
    it("should give the right logs"){

      val first_commit_name = Commit.getLastCommitFromBranch("master",test_directory)

      file4.createFile()
      file4.addContent("for new commit",appendContent = false)
      Add.addFilesToIndex(test_directory)
      Commit.commit(test_directory)

      val second_commit_name = Commit.getLastCommitFromBranch("master",test_directory)

      val first_commit_file = new FileHandler(new File(test_directory.getPath+"/.sgit/objects/commits/"+first_commit_name))
      val second_commit_file = new FileHandler(new File(test_directory.getPath+"/.sgit/objects/commits/"+second_commit_name))

      val first_commit_log = List[String]("commit "+first_commit_name)++first_commit_file.getLinesList
      val second_commit_log = List[String]("commit "+second_commit_name)++second_commit_file.getLinesList

      Log.getLog(test_directory,p = false, stat = false) shouldBe second_commit_log++first_commit_log
    }
  }

  describe("getDiffBetweenIndexes function"){
    it("should return the difference between files in old and new index"){
      file2.deleteFile()
      file1.addContent(" darkness\nmy old friend",appendContent = true)
      val file5 = new FileHandler(new File(test_directory.getPath+"/file5"))
      file5.createFile()
      file5.addContent("new\nfile",appendContent = true)
      Add.addFilesToIndex(test_directory)


      val commit_name = Commit.getLastCommitFromBranch("master",test_directory)
      val commit_file = new CommitHandler(new File(commits_directory.getPath+"/"+commit_name))
      val tree_name = commit_file.getTree
      val tree_file = new TreeHandler(new File(trees_directory.getPath+"/"+tree_name))
      val index_of_tree = new IndexHandler(new File(test_directory.getPath+"/NEWINDEX"))
      index_of_tree.createFile()
      index_of_tree.addContent(tree_file.getIndex("",test_directory),appendContent = true)

      val logp = Diff.getDiffBetweenIndexes(index_file,index_of_tree,test_directory, stat = false)

      val modif_file1_expected = List[String]("--- a/directory1/file1", "+++b/directory1/file1","","-Line 1 : hello","","+Line 1 : hello darkness","+Line 2 : my old friend","")
      val add_file5_expected = List[String]("--- a/null","+++b/file5","","+Line 1 : new","+Line 2 : file","")
      val delete_file2_expected = List[String]("--- a/file2","+++b/null","","-Line 1 : darkness","")

      logp shouldBe modif_file1_expected++add_file5_expected++delete_file2_expected
    }
  }

  describe("getLog function with option -p") {
    it("should have two commits on the result") {
      file4.createFile()
      file4.addContent("for new commit", appendContent = true)
      Add.addFilesToIndex(test_directory)
      Commit.commit(test_directory)

      println(Log.getLog(test_directory, p = true, stat = false))
      println(Log.getLog(test_directory, p = false, stat = true))

    }
  }
}

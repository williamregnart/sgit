package filesTest

import java.io.File

import commands.{Add, Commit, Diff, Init}
import files.{CommitHandler, FileHandler, IndexHandler, TreeHandler}
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

import scala.reflect.io.Directory

class TreeHandlerTest extends FunSpec with Matchers with BeforeAndAfter{
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
    Add.addFilesToIndex(test_directory)
    Commit.commit(test_directory)
  }

  /**
    * after executing tests :
    * - delete .sgit directory
    * - delete all files and subdirectories
    */
  after{
    new Directory(test_directory).deleteRecursively()
  }

  describe("getIndex function"){
    it("should return the same content as index file"){
      val commit_name = Commit.getLastCommitFromBranch("master",test_directory)
      val commit_file = new CommitHandler(new File(commits_directory.getPath+"/"+commit_name))
      val tree_name = commit_file.getTree
      val tree_file = new TreeHandler(new File(trees_directory.getPath+"/"+tree_name))
      val index_of_tree = new IndexHandler(new File(test_directory.getPath+"/NEWINDEX"))
      index_of_tree.createFile()
      index_of_tree.addContent(tree_file.getIndex("",test_directory),appendContent = true)
      Diff.getAddedLines(index_of_tree.getLinesList,index_file.getLinesList) should have size 0
      Diff.getAddedLines(index_file.getLinesList,index_of_tree.getLinesList) should have size 0
    }
  }
}

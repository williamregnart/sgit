package commandsTest

import java.io.File

import commands.{Add, Commit, Init, Status}
import files.{FileHandler, IndexHandler}
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

import scala.reflect.io.Directory

class StatusTest extends FunSpec with Matchers with BeforeAndAfter {

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

    Add.addFilesToIndex(test_directory.getPath)
    Commit.commit(test_directory.getPath)

    file3.createFile()
    file3.addContent("friend",appendContent = false)

    Add.addFileToIndex("file3",test_directory.getPath)
    file2.addContent("my old",appendContent = true)
    file1.deleteFile()

  }

  /**
    * after executing tests :
    * - delete all files and subdirectories
    */
  after{
    new Directory(test_directory).deleteRecursively()
  }

  describe("getNewFilesUntracked function"){
    it("should give the new files in repo which have not been added"){
      val repo_files = List[String]("/dir1/file1","/file2","/file3","/file4")
      val index_files = List[String]("/file2","/dir1/file1","/file5")
      Status.getNewFilesUntracked(repo_files,index_files) shouldBe List[String]("/file3","/file4")
    }
  }
  describe("printUntrackedFiles function"){
    it("should print in console the files untracked in repo which have not been added"){
      Status.printUntrackedFiles(test_directory.getPath)
      Status.printUncommittedFiles(test_directory.getPath)
    }
  }
}

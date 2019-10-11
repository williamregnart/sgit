import java.io.File

import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

import scala.reflect.io.Directory

class CommitTest extends FunSpec with Matchers with BeforeAndAfter{
  val test_directory = new File(System.getProperty("user.dir")+"/test_directory")
  val index_file = FileHandler(new File(test_directory.getPath+"/.sgit/INDEX"))
  val commits_directory = new File(test_directory.getPath+"/.sgit/objects/commits")
  val trees_directory = new File(test_directory.getPath+"/.sgit/objects/trees")

  val directory1 = new File(test_directory.getPath+"/directory1")
  val file1 = FileHandler(new File(test_directory.getPath+"/directory1/file1"))
  val file2 = FileHandler(new File(test_directory.getPath+"/file2"))
  val file3 = FileHandler(new File(test_directory.getPath+"/file3"))

  /**
    * before executing tests :
    * - make an init on the test_directory
    * - create 3 files with 1 in a subdirectory
    * - make an add with all files
    */
  before{
    Init.createSgit(test_directory)
    directory1.mkdir()
    file1.createFile()
    file2.createFile()
    file3.createFile()
    Add.addFilesToIndex(test_directory)
  }

  /**
  * after executing tests :
    * - delete .sgit directory
    * - delete all files and subdirectories
  */
  after{
    new Directory(new File(test_directory.getPath+"/.sgit")).deleteRecursively()
    file1.deleteFile()
    file2.deleteFile()
    file3.deleteFile()
    directory1.delete()
  }

  describe("the trees directory"){
    it("should have no tree  before first commit"){
      trees_directory.list() should have size 0
    }
    it("should have 2 trees after first commit one for directory1, one for test_directory"){
      Commit.commit(test_directory)
      trees_directory.list() should have size 2
    }
  }
}

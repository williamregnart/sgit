import java.io.File

import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

import scala.reflect.io.Directory

class LogTest extends FunSpec with Matchers with BeforeAndAfter{
  val test_directory = new File(System.getProperty("user.dir")+"/test_directory")
  val index_file = new IndexHandler(new File(test_directory.getPath+"/.sgit/INDEX"))
  val commits_directory = new File(test_directory.getPath+"/.sgit/objects/commits")
  val trees_directory = new File(test_directory.getPath+"/.sgit/objects/trees")

  val directory1 = new File(test_directory.getPath+"/directory1")
  val file1 = FileHandler(new File(test_directory.getPath+"/directory1/file1"))
  val file2 = FileHandler(new File(test_directory.getPath+"/file2"))
  val file3 = FileHandler(new File(test_directory.getPath+"/file3"))
  val file4 = FileHandler(new File(test_directory.getPath+"/file4"))

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

  describe("getSimpleLog function"){
    it("should have two commits on the result"){
      file4.createFile()
      file4.addContent("for new commit",appendContent = false)
      Add.addFilesToIndex(test_directory)
      Commit.commit(test_directory)

      Log.getSimpleLog(test_directory) should have size 2
    }
    it("should have at first a commit with a parentTree and at last a commit without parentTree"){
      file4.createFile()
      file4.addContent("for new commit",appendContent = false)
      Add.addFilesToIndex(test_directory)
      Commit.commit(test_directory)

      val log_result = Log.getSimpleLog(test_directory)
      val first_commit = log_result.head
      val last_commit = log_result.last

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

      val first_commit_file = FileHandler(new File(test_directory.getPath+"/.sgit/objects/commits/"+first_commit_name))
      val second_commit_file = FileHandler(new File(test_directory.getPath+"/.sgit/objects/commits/"+second_commit_name))

      val first_commit_log = "commit "+first_commit_name+"\n"+first_commit_file.getContent
      val second_commit_log = "commit "+second_commit_name+"\n"+second_commit_file.getContent

      Log.getSimpleLog(test_directory) shouldBe List[String](second_commit_log,first_commit_log)
    }
  }
}

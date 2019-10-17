package commandsTest

import java.io.File

import commands.Init
import files.FileHandler
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

import scala.reflect.io.Directory


class InitTest extends FunSpec with Matchers with BeforeAndAfter {

  val test_directory = new File(System.getProperty("user.dir")+"/test_directory")

  before{
    test_directory.mkdir()
  }
  after{
    new Directory(test_directory).deleteRecursively()
  }

  describe("an init process") {
    it("should create .sgit directory, create subdirectories, create files") {

      val sgit_directory = new File(test_directory.getPath+"/.sgit")

      sgit_directory.exists() shouldBe false

      Init.createSgit(test_directory)

      sgit_directory.exists() shouldBe true


      val trees_directory = new File(sgit_directory.getPath+"/objects/trees")
      val blobs_directory = new File(sgit_directory.getPath+"/objects/blobs")
      val commits_directory = new File(sgit_directory.getPath+"/objects/commits")
      trees_directory.exists() shouldBe true
      blobs_directory.exists() shouldBe true
      commits_directory.exists() shouldBe true

      val master_branch = new File(sgit_directory.getPath+"/refs/heads/master")
      master_branch.exists() shouldBe true

      val tags_directory = new File(sgit_directory.getPath+"/refs/tags")
      tags_directory.exists() shouldBe true

      val index_file = new File(sgit_directory.getPath+"/INDEX")
      index_file.exists() shouldBe true

      val head_file = new File(sgit_directory.getPath+"/HEAD")
      head_file.exists() shouldBe true
      new FileHandler(head_file).getContent shouldBe "master\n"
      }
    }
  }

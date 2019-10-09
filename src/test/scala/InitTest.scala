import java.io.File

import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}


class InitTest extends FunSpec with Matchers with BeforeAndAfter {

  val test_directory = new File(System.getProperty("user.dir")+"/test_directory")

  after{
    new File(test_directory.getPath+"/.sgit").delete()
  }

  describe("an init process") {
    it("should create .sgit directory") {


      val sgit_directory = new File(test_directory.getPath+"/.sgit")

      sgit_directory.exists() shouldBe false

      Init.createDirectory(sgit_directory.getPath,test_directory.getPath)

      sgit_directory.exists() shouldBe true

      sgit_directory.delete()

      }
    }
  }

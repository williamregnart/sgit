import java.io.File

import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}


class FileHandlerTest extends FunSpec with Matchers with BeforeAndAfter {

  val test_directory = new File(System.getProperty("user.dir")+"/test_directory")
  val test_file_handler = FileHandler(new File(test_directory.getPath+"/test_file"))

  before{
    test_file_handler.createFile()
    test_file_handler.addContent("hello\ndarkness\nmy\nold\nfriend",appendContent = false)
  }

  after{
    test_file_handler.deleteFile()
  }

  describe("a file handler") {
    it("should create file") {
      test_file_handler.deleteFile()
      test_file_handler.existFile() shouldBe false

      test_file_handler.createFile()
      test_file_handler.existFile() shouldBe true

    }
    it("should in order, in File, create it, write content, write at the end, overwrite, ") {

      test_file_handler.getContent shouldBe "hello\ndarkness\nmy\nold\nfriend\n"

      test_file_handler.addContent("\nhello", appendContent = true)
      test_file_handler.getContent shouldBe "hello\ndarkness\nmy\nold\nfriend\nhello\n"

      test_file_handler.addContent("new content", appendContent = false)
      test_file_handler.getContent shouldBe "new content\n"
    }

    it("should give the path from directory repository"){
      test_file_handler.getPathFromDir(test_directory) shouldBe "/test_file"
    }

    it("should give the unique key of file"){
      test_file_handler.addContent("sfvgqsdfvgd",appendContent = false)
      test_file_handler.getUniqueKey shouldBe "0CAEFC8D9F28E473392A8E024ADA2B74251B4710"
      test_file_handler.getUniqueKey should have size 40

      test_file_handler.addContent("hello\ndarkness\nmy\nold\nfriend",appendContent = false)
      test_file_handler.getUniqueKey should not be "0CAEFC8D9F28E473392A8E024ADA2B74251B4710"
      test_file_handler.getUniqueKey should have size 40
    }

    it("should replace line by content"){
      test_file_handler.replaceLineByContent("darkness","world")
      test_file_handler.getContent shouldBe "hello\nworld\nmy\nold\nfriend\n"

      test_file_handler.addContent("darkness\ndarkness\nmy\nold\nfriend",appendContent = false)
      test_file_handler.replaceLineByContent("darkness","hello")
      test_file_handler.getContent shouldBe "hello\ndarkness\nmy\nold\nfriend\n"
    }

    it("should give line with pattern in parameter"){
      test_file_handler.getLineWithPattern("dark") shouldBe Some("darkness")
      test_file_handler.getLineWithPattern("world") shouldBe None
    }
  }
}

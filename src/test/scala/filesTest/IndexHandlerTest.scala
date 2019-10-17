package filesTest

import java.io.File

import commands.Init
import files.IndexHandler
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}
import others.Encryption

import scala.reflect.io.Directory

class IndexHandlerTest extends FunSpec with Matchers with BeforeAndAfter {

  val test_directory = new File(System.getProperty("user.dir")+"/test_directory")
  val index_file = new IndexHandler(new File(test_directory.getPath+"/INDEX"))


  before{
    test_directory.mkdir()
    index_file.createFile()
    index_file.addContent("/file1 blob1\n",appendContent = true)
    index_file.addContent("/dir1/subdir1/file2 blob2\n",appendContent = true)
    index_file.addContent("/dir1/subdir1/file3 blob3\n",appendContent = true)
    index_file.addContent("/dir1/file4 blob4\n",appendContent = true)
    index_file.addContent("/dir1/file5 blob5\n",appendContent = true)
    index_file.getFilesFromPath("/nopath") should have size 0
  }
  after{
    new Directory(test_directory).deleteRecursively()
  }

  describe("getAllFilesPath function"){
    it("should give the paths of content added"){

      index_file.getAllFilesPath should have size 5
      index_file.getAllFilesPath shouldBe List[String]("/file1","/dir1/subdir1/file2","/dir1/subdir1/file3","/dir1/file4","/dir1/file5")
    }
  }

  describe("getFilesFromPath function"){

    it("should give empty list if path has no files"){
      index_file.getFilesFromPath("/nopath") should have size 0
    }
    it("should give a list of files path"){

      index_file.getFilesFromPath("/dir1") should have size 2
      index_file.getFilesFromPath("/dir1") shouldBe List[String]("/dir1/file4","/dir1/file5")

      index_file.getFilesFromPath("") should have size 1
      index_file.getFilesFromPath("") shouldBe List[String]("/file1")

      index_file.getFilesFromPath("/dir1/subdir1") should have size 2
      index_file.getFilesFromPath("/dir1/subdir1") shouldBe List[String]("/dir1/subdir1/file2","/dir1/subdir1/file3")
    }
  }

  describe("getDirPathFromPath function"){
    it("should give the directories in path given"){

      index_file.addContent("/dir2/file6 blob6\n",appendContent = true)
      index_file.getDirPathFromPath("") should have size 2
      index_file.getDirPathFromPath("") shouldBe List[String]("/dir1/","/dir2/")

      index_file.getDirPathFromPath("/dir1") shouldBe List[String]("/dir1/subdir1/")
    }
  }

  describe("getBlobFromFilePath function"){
    it("should give the blob of file path"){
      index_file.getBlobFromFilePath("/dir1/subdir1/file2") shouldBe "blob2"
    }
    it("should give empty string if path doesn't exist"){
      index_file.getBlobFromFilePath("no/existing/path") shouldBe ""
    }
  }

  describe("getBlobLineToInsert function"){
    it("should give blob line to insert in tree from path"){
      index_file.getBlobLineToInsert("/dir1/subdir1/file2") shouldBe "\nblob blob2 file2"
    }
  }
  describe("getAllBlobsLineToInsert function"){
    it("should give all blobs lines to insert"){
      val list_files = List[String]("/dir1/file4","/dir1/file5")
      val result_expected = "\nblob blob4 file4"+"\nblob blob5 file5"
      index_file.getAllBlobsLineToInsert(list_files,"") shouldBe result_expected
    }
  }
  describe("getTreeFunction return"){
    it("should give tree for a path without subtree"){
      Init.createSgit(test_directory)
      val content_tree_expected="\nblob blob2 file2\nblob blob3 file3"
      index_file.getTree("/dir1/subdir1",test_directory) shouldBe Encryption.sha1(content_tree_expected)
    }
    it("should give tree for a path with blobs and subtrees"){
      Init.createSgit(test_directory)
      val content_blob_expected="\nblob blob4 file4\nblob blob5 file5"
      val content_tree_expected="\ntree "+index_file.getTree("/dir1/subdir1",test_directory)+" /dir1/subdir1/"
      val content_expected = content_blob_expected+content_tree_expected
      index_file.getTree("/dir1",test_directory) shouldBe Encryption.sha1(content_expected)
    }
    it("should have create trees in .sgit/objects/trees"){
      Init.createSgit(test_directory)

      val content_expected_tree_2 = "\nblob blob2 file2\nblob blob3 file3"
      new File(test_directory.getPath+"/.sgit/objects/trees/"+Encryption.sha1(content_expected_tree_2)).exists() shouldBe false

      val content_blob_expected="\nblob blob4 file4\nblob blob5 file5"
      val content_tree_expected="\ntree "+index_file.getTree("/dir1/subdir1",test_directory)+" /dir1/subdir1/"
      val content_expected_tree_1 = content_blob_expected+content_tree_expected



      new File(test_directory.getPath+"/.sgit/objects/trees/"+Encryption.sha1(content_expected_tree_1)).exists() shouldBe false


      index_file.getTree("/dir1",test_directory)

      val trees_directory = new File(test_directory.getPath+"/.sgit/objects/trees")
      trees_directory.list() should have size 2
      new File(test_directory.getPath+"/.sgit/objects/trees/"+Encryption.sha1(content_expected_tree_1)).exists() shouldBe true
      new File(test_directory.getPath+"/.sgit/objects/trees/"+Encryption.sha1(content_expected_tree_2)).exists() shouldBe true
    }
  }
  describe("getLinesWithoutPath"){
    it("should return index content without path line"){
      index_file.getLinesWithoutPath("/dir1/file4") shouldBe "/file1 blob1\n/dir1/subdir1/file2 blob2\n/dir1/subdir1/file3 blob3\n/dir1/file5 blob5\n"
    }
  }
}

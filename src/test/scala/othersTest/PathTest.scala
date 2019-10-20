package othersTest

import org.scalatest.{FunSpec, Matchers}
import others.Path

class PathTest extends FunSpec with Matchers{

  describe("getPathAndSubPath function"){
    it("should give path and subpath of path given"){
      Path.getPathAndSubPath("/dir/to/file") should have size 3
      Path.getPathAndSubPath("/dir/to/file") shouldBe List[String]("/","/dir/","/dir/to/")
    }
  }
  describe("'getAllPathAndSubPath function"){
    it("should give all path and subpath of the list of paths"){
      val list_paths = List[String]("/dir/to/path/file1","/dir/to/path/file2","/dir2/to/file3","/dir2/to/path/file4")
      val list_paths_expected = List[String]("/","/dir/","/dir/to/","/dir/to/path/","/dir2/","/dir2/to/","/dir2/to/path/")

      Path.getAllPathAndSubPath(list_paths) should have size 7
      Path.getAllPathAndSubPath(list_paths) shouldBe list_paths_expected
    }
  }

  describe("getElementsFromPath"){
    it("should return all files in path given a list of path"){
      val list_paths = List[String]("/dir/to/path/file1","/dir/to/path/file2","/dir2/to/file3","/dir2/to/path/file4")

      Path.getElementsFromPath("/dir/to/path",list_paths,List[String]()) should have size 2
      Path.getElementsFromPath("/dir/to/path",list_paths,List[String]()) shouldBe List[String]("/dir/to/path/file1","/dir/to/path/file2")
    }
    it("should return all directories of a path given the a list of path and subpaths") {

      val list_paths = List[String]("/dir/to/path/file1", "/dir/to/path/file2", "/dir2/to/file3", "/dir2/to/path/file4")
      val path_and_subpaths = Path.getAllPathAndSubPath(list_paths)

      Path.getElementsFromPath("", path_and_subpaths, List[String]()) shouldBe List[String]("/dir/", "/dir2/")

      Path.getElementsFromPath("/dir",path_and_subpaths,List[String]()) shouldBe List[String]("/dir/to/")
    }
  }
}

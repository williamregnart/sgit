import java.io.File

import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

class IndexTest extends FunSpec with Matchers with BeforeAndAfter {
  val actualDirectory = new File(System.getProperty("user.dir"))
  val gitPath = actualDirectory.getPath+"/.sgit"
  val index_file = new FileHandler(new File(gitPath+"/INDEX"))
}

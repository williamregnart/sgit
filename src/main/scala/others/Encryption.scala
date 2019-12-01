package others

import java.security.MessageDigest

import javax.xml.bind.DatatypeConverter

object Encryption {

  def sha1(input: String): String = {
    var sha1 = ""
    val msdDigest = MessageDigest.getInstance("SHA-1")
    if(input!="") {
      msdDigest.update(input.getBytes("UTF-8"), 0, input.length)
    }
    else{
      msdDigest.update((input+" ").getBytes("UTF-8"), 0, input.length+1)
    }
    sha1 = DatatypeConverter.printHexBinary(msdDigest.digest)
    sha1
  }
}

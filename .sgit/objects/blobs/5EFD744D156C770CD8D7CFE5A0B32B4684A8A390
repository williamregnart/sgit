

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}

object Encryption {
  private val Algorithm = "AES/CBC/PKCS5Padding"
  private val Key = new SecretKeySpec(Base64.getDecoder.decode("DxVnlUlQSu3E5acRu7HPwg=="), "AES")
  private val IvSpec = new IvParameterSpec(new Array[Byte](16))

  val AdminKey = "Dx$V!nl%Ul^QS&u3*E5@acR-u7HPwg=="

  def encrypt(text: String): String = {
    val cipher = Cipher.getInstance(Algorithm)
    cipher.init(Cipher.ENCRYPT_MODE, Key, IvSpec)

    new String(Base64.getEncoder.encode(cipher.doFinal(text.getBytes("utf-8"))), "utf-8")
  }

  def decrypt(text: String): String = {
    val cipher = Cipher.getInstance(Algorithm)
    cipher.init(Cipher.DECRYPT_MODE, Key, IvSpec)

    new String(cipher.doFinal(Base64.getDecoder.decode(text.getBytes("utf-8"))), "utf-8")
  }

  import javax.xml.bind.DatatypeConverter
  import java.io.UnsupportedEncodingException
  import java.security.MessageDigest
  import java.security.NoSuchAlgorithmException

  def sha1(input: String): String = {
    var sha1 = ""
    val msdDigest = MessageDigest.getInstance("SHA-1")
    msdDigest.update(input.getBytes("UTF-8"), 0, input.length)
    sha1 = DatatypeConverter.printHexBinary(msdDigest.digest)
    sha1
  }
}


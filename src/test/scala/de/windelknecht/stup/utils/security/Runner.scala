package de.windelknecht.stup.utils.security

import java.nio.ByteBuffer
import java.security.Security
import de.windelknecht.stup.utils.coding.Implicits._
import de.windelknecht.stup.utils.security._

object Runner {
  def main(args: Array[String]) {
    val r1 = Security.getProviders
    val r2 = r1.flatMap { ps => ps.values().toArray }
    val r3 = r2.filter(_.toString.startsWith("AES"))

    val r4 = "lölöä".encrypt(KeyGenerator.make128BitAESKey("heiko", "blobnr"))
    val r5 = new String(r4)

    println("")
  }
}

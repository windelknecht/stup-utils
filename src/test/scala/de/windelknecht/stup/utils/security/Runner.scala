package de.windelknecht.stup.utils.security

import java.security.Security

object Runner {
  def main(args: Array[String]) {
    val r1 = Security.getProviders
    val r2 = r1.flatMap { ps => ps.values().toArray }
    val r3 = r2.filter(_.toString.startsWith("AES"))

    println("")
  }
}

package de.windelknecht.stup.utils

import java.io.{OutputStream, InputStream}
import java.nio.ByteBuffer
import javax.crypto.{Cipher, SecretKey}

import scala.io.Codec

package object security {
  implicit def toArray(in: ByteBuffer): Array[Byte] = {
    val out = new Array[Byte](in.remaining())
    in.get(out)
    in.flip()
    out
  }
  implicit def toByteBuffer(in: Array[Byte]): ByteBuffer = ByteBuffer.wrap(in)
  implicit def toString(in: Array[Byte])(implicit codec: Codec = Codec.UTF8): String = new String(in, codec.charSet)

  implicit class FromByteBuffer(
    in: ByteBuffer
    ) {
    /**
     * Decrypt the given input.
     *
     * @param key AES key
     * @return a new created byte buffer with encrypted output
     */
    def decrypt(key: SecretKey): ByteBuffer = AESTools.doCipher(Cipher.DECRYPT_MODE, key, in)

    /**
     * Encrypt given clear text with the given key.
     *
     * @param key AES key
     * @return a new created byte buffer with encrypted output
     */
    def encrypt(key: SecretKey): ByteBuffer = AESTools.doCipher(Cipher.ENCRYPT_MODE, key, in)
  }

  implicit class FromStream(
    in: InputStream
    ) {
    /**
     * Decrypt given clear text with the given key.
     *
     * @param key AES key
     * @param outStream file to write encrypted content into
     */
    def decrypt(key: SecretKey, outStream: OutputStream) = AESTools.doCipher(Cipher.DECRYPT_MODE, key, in, outStream)

    /**
     * Encrypt given clear text with the given key.
     *
     * @param key AES key
     * @param outStream file to write encrypted content into
     */
    def encrypt(key: SecretKey, outStream: OutputStream) = AESTools.doCipher(Cipher.ENCRYPT_MODE, key, in, outStream)
  }

  implicit class FromString(
    in: String
    ) {
    /**
     * Encrypt given clear text with the given key.
     *
     * @param key AES key
     * @return encrypted text as byte array
     */
    def encrypt(key: SecretKey): Array[Byte] = new FromByteBuffer(in.getBytes).encrypt(key)
  }
}

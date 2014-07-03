/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Heiko Blobner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.windelknecht.stup.utils.security

import java.io.{InputStream, OutputStream}
import java.nio.ByteBuffer
import java.nio.channels.Channels
import javax.crypto.spec.{PBEKeySpec, SecretKeySpec}
import javax.crypto.{Cipher, SecretKey, SecretKeyFactory}

import scala.language.implicitConversions

object AESTools {
  /**
   * Implicit convert from byte array into string.
   */
  implicit def toString(in: Array[Byte]): String = new String(in)

  private val CIPHER_METHOD = "AES/ECB/PKCS5Padding"

  /**
   * Calculated the maximum out put length.
   *
   * @param method either Cipher.DECRYPT_MODE or Cipher.ENCRYPT_MODE
   * @param key AES key
   * @param len len to de- or encrypt
   * @return max calculated length
   */
  private def calcOutputSize(
    method: Int,
    key: SecretKey,
    len: Int
    ) = {
    val aes = Cipher.getInstance(CIPHER_METHOD)

    aes.init(method, key)
    aes.getOutputSize(len)

  }
  private def calcDecryptOutputSize(key: SecretKey, len: Int) = calcOutputSize(Cipher.DECRYPT_MODE, key, len)
  private def calcEncryptOutputSize(key: SecretKey, len: Int) = calcOutputSize(Cipher.ENCRYPT_MODE, key, len)

  /**
   * Decrypt or encrypt the given byte buffer.
   *
   * @param method either Cipher.DECRYPT_MODE or Cipher.ENCRYPT_MODE
   * @param key AES key
   * @param in in byte buffer
   * @return decrypted or encrypted output
   */
  def doCipher(
    method: Int,
    key: SecretKey,
    in: ByteBuffer
    ): ByteBuffer = doCipher(method, key, in, ByteBuffer.allocateDirect(calcOutputSize(method, key, in.capacity())))

  /**
   * Decrypt or encrypt the given byte buffer.
   *
   * @param method either Cipher.DECRYPT_MODE or Cipher.ENCRYPT_MODE
   * @param key AES key
   * @param in in byte buffer
   * @param out out byte buffer
   * @return decrypted or encrypted output
   */
  def doCipher(
    method: Int,
    key: SecretKey,
    in: ByteBuffer,
    out: ByteBuffer
    ): ByteBuffer = {
    val aes = Cipher.getInstance(CIPHER_METHOD)

    aes.init(method, key)
    aes.doFinal(in, out)
    in.flip()
    out.flip()
    out
  }

  /**
   * Decrypt or encrypt the given byte array.
   *
   * @param method either Cipher.DECRYPT_MODE or Cipher.ENCRYPT_MODE
   * @param key AES key
   * @param cipherBytes in array
   * @return decrypted or encrypted output
   */
  def doCipher(
    method: Int,
    key: SecretKey,
    cipherBytes: Array[Byte]
    ): Array[Byte] = {
    val bb = doCipher(method, key, ByteBuffer.wrap(cipherBytes))

    val out = new Array[Byte](bb.remaining())
    bb.get(out)
    bb.flip()
    out
  }

  /**
   * Encrypt given clear text with the given key.
   *
   * @param key AES key
   * @param inStream VFS file to encrypt
   * @param outStream file to write encrypted content into
   */
  def doCipher(
    method: Int,
    key: SecretKey,
    inStream: InputStream,
    outStream: OutputStream
    ): Unit = {
    val inChannel = Channels.newChannel(inStream)
    val outChannel = Channels.newChannel(outStream)

    // copy bit wise
    val inBuffer = ByteBuffer.allocateDirect(1024 * 1024)

    while(inChannel.read(inBuffer) > 0) {
      outChannel.write(
        doCipher(method, key, inBuffer)
      )
    }

    inChannel.close()
    outChannel.close()
  }

  /**
   * Decrypt the given byte array.
   *
   * @param key AES key
   * @param cipherBytes bytes to decrypt
   * @return decrypted input as byte array
   */
  def decrypt(key: SecretKey, cipherBytes: Array[Byte]): Array[Byte] = doCipher(Cipher.DECRYPT_MODE, key, cipherBytes)

  /**
   * Decrypt the given input.
   *
   * @param key AES key
   * @param in in byte buffer
   * @return a new created byte buffer with encrypted output
   */
  def decrypt(key: SecretKey, in: ByteBuffer): ByteBuffer = doCipher(Cipher.DECRYPT_MODE, key, in)

  /**
   * Decrypt given clear text with the given key.
   *
   * @param key AES key
   * @param inStream VFS file to encrypt
   * @param outStream file to write encrypted content into
   */
  def decrypt(key: SecretKey, inStream: InputStream, outStream: OutputStream) = doCipher(Cipher.DECRYPT_MODE, key, inStream, outStream)

  /**
   * Encrypt given clear text with the given key.
   *
   * @param key AES key
   * @param clearBytes bytes to encrypt
   * @return encrypted input as byte array
   */
  def encrypt(key: SecretKey, clearBytes: Array[Byte]): Array[Byte] = doCipher(Cipher.ENCRYPT_MODE, key, clearBytes)

  /**
   * Encrypt given clear text with the given key.
   *
   * @param key AES key
   * @param clearText string to encrypt
   * @return encrypted text as byte array
   */
  def encrypt(key: SecretKey, clearText: String): Array[Byte] = encrypt(key, clearText.getBytes)

  /**
   * Encrypt given clear text with the given key.
   *
   * @param key AES key
   * @param in in byte buffer
   * @return a new created byte buffer with encrypted output
   */
  def encrypt(key: SecretKey, in: ByteBuffer): ByteBuffer = doCipher(Cipher.ENCRYPT_MODE, key, in)

  /**
   * Encrypt given clear text with the given key.
   *
   * @param key AES key
   * @param inStream VFS file to encrypt
   * @param outStream file to write encrypted content into
   */
  def encrypt(key: SecretKey, inStream: InputStream, outStream: OutputStream) = doCipher(Cipher.ENCRYPT_MODE, key, inStream, outStream)

  /**
   * Make a 128 bit AES key form the given passphrase and flavor it with the given salt.
   *
   * @param passphrase user pass phrase to generate key from
   * @param salt use this salt
   * @param iterations number to iterate key generation
   * @return AES key
   */
  def make128BitAESKey(
    passphrase: String,
    salt: String,
    iterations: Int = 10000
    ): SecretKey = {
    /*
    PBKDF2 is an algorithm specially designed for generating keys from passwords that is considered more secure than a simple SHA1 hash.
    The salt ensures your encryption won't match another encryption using the same key and cleartext and helps prevent dictionary attacks.
    The iterations value is an adjustable parameter. Higher values use more computing power, making brute force attacks more difficult.
     */
    val key = SecretKeyFactory
      .getInstance("PBKDF2WithHmacSHA1")
      .generateSecret(
        new PBEKeySpec(
          passphrase.toCharArray,
          salt.getBytes,
          iterations,
          128
        ))
      .getEncoded

    new SecretKeySpec(key, "AES")
  }
}

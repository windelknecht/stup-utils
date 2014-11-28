package de.windelknecht.stup.utils.security

import javax.crypto.spec.{SecretKeySpec, PBEKeySpec}
import javax.crypto.{SecretKeyFactory, SecretKey}

object KeyGenerator {
  private val _defaultAlgorithm = "PBKDF2WithHmacSHA1"
  private val _defaultIterations = 10000

  /**
   * Make a 128 bit AES key form the given passphrase and flavor it with the given salt.
   *
   * @param passphrase user pass phrase to generate key from
   * @param salt use this salt
   * @param iterations number to iterate key generation
   * @param algorithm default is 'PBKDF2WithHmacSHA1', see https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#SecretKeyFactory
   * @return AES key
   */
  def make128BitAESKey(
    passphrase: String,
    salt: String,
    iterations: Int = _defaultIterations,
    algorithm: String = _defaultAlgorithm
    ): SecretKey = makeAESKey(passphrase = passphrase, salt = salt, iterations = iterations, algorithm = algorithm, bitCount = 128)

  /**
   * Make a 192 bit AES key form the given passphrase and flavor it with the given salt.
   *
   * @param passphrase user pass phrase to generate key from
   * @param salt use this salt
   * @param iterations number to iterate key generation
   * @param algorithm default is 'PBKDF2WithHmacSHA1', see https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#SecretKeyFactory
   * @return AES key
   */
  def make192BitAESKey(
    passphrase: String,
    salt: String,
    iterations: Int = _defaultIterations,
    algorithm: String = _defaultAlgorithm
    ): SecretKey = makeAESKey(passphrase = passphrase, salt = salt, iterations = iterations, algorithm = algorithm, bitCount = 192)

  /**
   * Make a 256 bit AES key form the given passphrase and flavor it with the given salt.
   *
   * @param passphrase user pass phrase to generate key from
   * @param salt use this salt
   * @param iterations number to iterate key generation
   * @param algorithm default is 'PBKDF2WithHmacSHA1', see https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#SecretKeyFactory
   * @return AES key
   */
  def make256BitAESKey(
    passphrase: String,
    salt: String,
    iterations: Int = _defaultIterations,
    algorithm: String = _defaultAlgorithm
    ): SecretKey = makeAESKey(passphrase = passphrase, salt = salt, iterations = iterations, algorithm = algorithm, bitCount = 256)

  /**
   * Make a 128 bit AES key form the given passphrase and flavor it with the given salt.
   *
   * @param passphrase user pass phrase to generate key from
   * @param salt use this salt
   * @param iterations number to iterate key generation
   * @return AES key
   */
  private def makeAESKey(
    passphrase: String,
    salt: String,
    iterations: Int,
    algorithm: String,
    bitCount: Int
    ): SecretKey = {
    /*
    PBKDF2 is an algorithm specially designed for generating keys from passwords that is considered more secure than a simple SHA1 hash.
    The salt ensures your encryption won't match another encryption using the same key and cleartext and helps prevent dictionary attacks.
    The iterations value is an adjustable parameter. Higher values use more computing power, making brute force attacks more difficult.
     */
    val key = SecretKeyFactory
      .getInstance(algorithm)
      .generateSecret(
        new PBEKeySpec(
          passphrase.toCharArray,
          salt.getBytes,
          iterations,
          bitCount
        ))
      .getEncoded

    new SecretKeySpec(key, "AES")
  }
}

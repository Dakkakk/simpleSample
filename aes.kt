import java.io.UnsupportedEncodingException
import java.security.GeneralSecurityException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private const val AES_MODE = "AES/CBC/PKCS5Padding"
private const val CHARSET = "UTF-8"
private const val CIPHER = "AES"
private const val HASH_ALGORITHM = "SHA-256"
private val decoder= Base64.getDecoder()
private val encoder= Base64.getEncoder()
val String.base64:String
    get() = encoder.encodeToString(encodeToByteArray())
private val IV_BYTES = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
/**
 * Generates SHA256 hash of the password which is used as key
 *
 * @param password used to generated key
 * @return SHA256 of the password
 */
@Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class)
private fun generateKey(password: String): SecretKeySpec {
    val digest = MessageDigest.getInstance(HASH_ALGORITHM)
    val bytes = password.toByteArray(charset(CHARSET))
    digest.update(bytes, 0, bytes.size)
    val key = digest.digest()

    return SecretKeySpec(key, CIPHER)
}
@Throws(GeneralSecurityException::class)
fun encrypt(key: SecretKeySpec, iv: ByteArray, message: ByteArray): ByteArray {
    val cipher = Cipher.getInstance(AES_MODE)
    val ivSpec = IvParameterSpec(iv)
    cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
    return cipher.doFinal(message)
}
fun String.encryptByAES(password:String,iv:ByteArray=IV_BYTES):String{
    try {
        val key = generateKey(password)
        val cipherText = encrypt(key, iv, encodeToByteArray())
        //NO_WRAP is important as was getting \n at the end
        return encoder.encodeToString(cipherText)
    } catch (e: UnsupportedEncodingException) {
        throw GeneralSecurityException(e)
    }
}
fun decrypt(key: SecretKeySpec, iv: ByteArray, decodedCipherText: ByteArray): ByteArray {
    val cipher = Cipher.getInstance(AES_MODE)
    val ivSpec = IvParameterSpec(iv)
    cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
    return cipher.doFinal(decodedCipherText)
}
fun String.decryptByAES(password: String,iv: ByteArray=IV_BYTES):String{
    try {
        val decryptedBytes = decrypt(generateKey(password), iv, decoder.decode(this))
        return String(decryptedBytes, charset(CHARSET))
    } catch (e: UnsupportedEncodingException) {
        throw GeneralSecurityException(e)
    }
}

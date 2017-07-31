package android.infoglobo.com.criptografia

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

/**
 * Created by AllysonRodrigues on 31/07/17.
 */

class CryptoHelper {


    companion object {
        @Throws(Exception::class)
        fun decodeFile(key: ByteArray, fileData: ByteArray?): ByteArray? {
            val skeySpec = SecretKeySpec(key, "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.DECRYPT_MODE, skeySpec)

            val decrypted = cipher.doFinal(fileData)

            return decrypted
        }

        @Throws(Exception::class)
        fun encodeFile(key: ByteArray, fileData: ByteArray?): ByteArray {

            val skeySpec = SecretKeySpec(key, "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec)

            val encrypted = cipher.doFinal(fileData)

            return encrypted
        }


        @Throws(Exception::class)
        fun generateKey(password: String): ByteArray {
            val keyStart = password.toByteArray(charset("UTF-8"))

            val kgen = KeyGenerator.getInstance("AES")
            val sr = SecureRandom.getInstance("SHA1PRNG", "Crypto")
            sr.setSeed(keyStart)
            kgen.init(128, sr)
            val skey = kgen.generateKey()
            return skey.getEncoded()
        }
    }
}
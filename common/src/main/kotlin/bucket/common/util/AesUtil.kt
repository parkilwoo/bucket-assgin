package bucket.common.util

import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object AesUtil {
    private const val key = "MySecretKey12345" // 16자
    private val keySpec = SecretKeySpec(key.toByteArray(), "AES")

    fun encrypt(value: String): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val encrypted = cipher.doFinal(value.toByteArray())
        return Base64.getEncoder().encodeToString(encrypted)
    }
}

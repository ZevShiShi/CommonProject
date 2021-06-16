package com.qljm.swh.utils

import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ObjectUtils
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object MD5Utils {

    private fun hashTemplate(data: ByteArray?, algorithm: String?): ByteArray? {
        return if (data == null || data.isEmpty()) null else try {
            val md = MessageDigest.getInstance(algorithm)
            md.update(data)
            md.digest()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            null
        }
    }

    private fun encryptMD5(data: ByteArray?): ByteArray? {
        return hashTemplate(data, "MD5")
    }

    /**
     * md5加密
     */
    fun encryptMD5ToString(data: String?): String? {
        if (ObjectUtils.isEmpty(data)) return ""
        return ConvertUtils.bytes2HexString(encryptMD5(data!!.toByteArray()), false)
    }

}
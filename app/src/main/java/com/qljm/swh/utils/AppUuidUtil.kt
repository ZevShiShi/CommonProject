package com.qljm.icelive.utils

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import com.blankj.utilcode.util.ObjectUtils
import com.blankj.utilcode.util.PhoneUtils
import com.qljm.swh.base.BaseApplication
import java.io.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.experimental.and


/**
 *   Created by ruirui on 2020/3/30
 */
object AppUuidUtil {

    private const val PREFS_FILE = "dev_id"
    private const val DEVICE_UUID_FILE_NAME = ".dev_id.txt"
    private const val PREFS_DEVICE_ID = "dev_id"

    /**
     * 获取手机唯一ID
     */
    val uuid: String
        get() {
            try {
                var uuid: UUID? = null
                synchronized(AppUuidUtil::class.java) {
                    val prefs = BaseApplication.context?.getSharedPreferences(PREFS_FILE, 0)
                    val id = prefs?.getString(PREFS_DEVICE_ID, null)
                    if (id != null) {
                        uuid = UUID.fromString(id)
                    } else {
                        if (recoverDeviceUuidFromSD() != null) {
                            uuid = UUID.fromString(recoverDeviceUuidFromSD())
                        } else {
                            var m_szLongID = (androidID
                                    + imei
                                    + pseudoUniqueID
                                    + wifiMac
                                    + bTMACAddress)
                            if (TextUtils.isEmpty(m_szLongID)) {
                                m_szLongID = Date().time.toString()
                            }
                            try {
                                uuid =
                                    UUID.nameUUIDFromBytes(toMD5(m_szLongID).toByteArray(charset("utf8")))
                                saveDeviceUuidToSD(uuid.toString())
                            } catch (e: NoSuchAlgorithmException) {
                            } catch (e: UnsupportedEncodingException) {
                            }
                        }
                        prefs?.edit()?.putString(PREFS_DEVICE_ID, uuid.toString())?.apply()
                    }
                }
                return uuid.toString()
            } catch (e: Exception) {
                return UUID.randomUUID().toString()
            }
        }

    private fun recoverDeviceUuidFromSD(): String? {
        var fileReader: FileReader? = null
        return try {
            val dirPath = Environment.getExternalStorageDirectory().absolutePath
            val dir = File(dirPath)
            val uuidFile = File(dir, DEVICE_UUID_FILE_NAME)
            if (!dir.exists() || !uuidFile.exists()) {
                return null
            }
            fileReader = FileReader(uuidFile)
            val sb = StringBuilder()
            val buffer = CharArray(100)
            var readCount: Int
            while (fileReader.read(buffer).also { readCount = it } > 0) {
                sb.append(buffer, 0, readCount)
            }
            val uuid = UUID.fromString(sb.toString())
            uuid.toString()
        } catch (e: Exception) {
            null
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close()
                } catch (e: IOException) {
                }
            }
        }
    }

    private fun saveDeviceUuidToSD(uuid: String) {
        val dirPath = BaseApplication.context?.filesDir?.absolutePath
        val targetFile = File(dirPath, DEVICE_UUID_FILE_NAME)
        if (!targetFile.exists()) {
            var osw: OutputStreamWriter? = null
            try {
                osw = OutputStreamWriter(FileOutputStream(targetFile), "utf-8")
                try {
                    osw.write(uuid)
                    osw.flush()
                    osw.close()
                } catch (e: IOException) {
                }
            } catch (e: UnsupportedEncodingException) {
            } catch (e: FileNotFoundException) {
            } finally {
                if (osw != null) {
                    try {
                        osw.close()
                    } catch (e: IOException) {
                    }
                }
            }
        }
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun toMD5(text: String): String {
        val messageDigest = MessageDigest.getInstance("MD5")
        val digest = messageDigest.digest(text.toByteArray())
        val sb = StringBuilder()
        for (i in digest.indices) {
            val digestInt: Byte = digest[i] and 0xff.toByte()
            val hexString = Integer.toHexString(digestInt.toInt())
            if (hexString.length < 2) {
                sb.append(0)
            }
            sb.append(hexString)
        }
        //返回整个结果
        return sb.toString()
    }


    private val imei: String?
        get() {
            // Android Q获取不到imei，就获取ANDROID_ID
            if (ActivityCompat.checkSelfPermission(
                    BaseApplication.context!!,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) return ""
            val imei = PhoneUtils.getIMEI()
            if (ObjectUtils.isNotEmpty(imei)) {
                return imei
            }
            return Settings.System.getString(
                BaseApplication.context?.contentResolver, Settings.Secure.ANDROID_ID
            )
        }


    /**
     * 获取设备AndroidID
     *
     * @return AndroidID
     */
    private val androidID: String
        @SuppressLint("HardwareIds")
        get() = Settings.Secure.getString(
            BaseApplication.context?.contentResolver,
            Settings.Secure.ANDROID_ID
        )

    private val pseudoUniqueID: String
        get() = "35" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.CPU_ABI.length % 10 + Build.DEVICE.length % 10 + Build.DISPLAY.length % 10 + Build.HOST.length % 10 + Build.ID.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10 + Build.TAGS.length % 10 + Build.TYPE.length % 10 + Build.USER.length % 10

    /**
     * 获取mac地址
     *
     */
    private val wifiMac: String
        get() {
            val wifi = BaseApplication.context?.applicationContext!!
                .getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (wifi != null) {
                val info = wifi.connectionInfo
                @SuppressLint("HardwareIds") var str = info.macAddress
                if (str == null) str = ""
                return str
            }
            return ""
        }

    /**
     * 获取蓝牙Mac
     */
    private val bTMACAddress: String
        @SuppressLint("HardwareIds")
        get() {
            var address = ""
            val addr = BluetoothAdapter.getDefaultAdapter().address
            if (addr != null) {
                address = addr
            }
            return address
        }


    // 获取渠道号
    /**
     * 手机品牌
     */
    val phone_brand: String
        get() {
            var brand = ""
            brand = Build.BRAND;
            return brand
        }

//    val getChannel: String
//        get() {
//            var channel = "";
//        try{
//            val info = m_instance.getPackageManager().
//                getApplicationInfo(m_instance.getPackageName(), PackageManager.GET_META_DATA);
//            if(info != null && info.metaData != null){
//                val metaData = info.metaData.getString("CP_CHANNEL");
//                if(!metaData.isEmpty()){
//                    channel = metaData;
//                }
//            }
//        }catch (e: IOException){
//            e.printStackTrace();
//        }
//        LogUtil.logDebug(LOG_TAG, "当前渠道为："+ channel)
//            return channel;
//        }

}
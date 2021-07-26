package com.qljm.swh.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.*
import com.lihui.base.common.BaseConstant
import com.qljm.swh.R
import com.qljm.swh.base.BaseApplication
import com.qljm.swh.view.BackgroundBlurPopupWindow
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import java.io.*
import java.text.DecimalFormat
import java.util.*
import kotlin.math.roundToInt


/**
 *   Created by ruirui on 2019/12/19
 */
object AppUtil {

    const val splitChar = "@"

    fun writeString(content: String) {
        if (ObjectUtils.isEmpty(content)) return
        val dir = File(getFileDirPath())
        if (!dir.exists()) {
            dir.mkdir()
        }
        val file = File(dir, getFileName())
        if (!file.exists()) {
            file.createNewFile()
        }
        FileIOUtils.writeFileFromString(file, content)
        LogUtils.d("writeString====$content")
    }

    private fun readString(): String {
        val dir = File(getFileDirPath())
        if (!dir.exists()) {
            dir.mkdir()
        }
        val file = File(dir, getFileName())
        if (file.exists()) {
            val str: String? = FileIOUtils.readFile2String(file)
            if (ObjectUtils.isEmpty(str)) {
                return ""
            }
            return str!!
        }
        return ""
    }

    fun getServer(): MutableList<String> {
        val list: MutableList<String> = mutableListOf()
        val readString: String? = readString()
        if (ObjectUtils.isEmpty(readString)) {
            LogUtils.d("getServer=======$list")
            return list
        }
        val stringArray = readString?.split(splitChar)
        if (ObjectUtils.isNotEmpty(stringArray)) {
            for (content in stringArray!!) {
                list.add(content)
            }
        }
        LogUtils.d("getServer=======$list")
        return list
    }

    /**
     * 渠道号
     */
    fun getAppChannel(
        context: Context?,
        key: String?
    ): String? {
        if (context == null || ObjectUtils.isEmpty(key)) {
            return null
        }
        var channelNumber: String? = null
        try {
            val packageManager = context.packageManager
            if (packageManager != null) {
                val applicationInfo = packageManager.getApplicationInfo(
                    context.packageName,
                    PackageManager.GET_META_DATA
                )
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        channelNumber = applicationInfo.metaData.getString(key)
                    }
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return channelNumber
    }

    fun getServerType(): Int {
        if (ObjectUtils.isEmpty(getServer()) || getServer().size < 3) {
            return 0
        }
        return getServer()[2].toInt()
    }

//    fun delServerFile() {
//        val serverFile = File(File(getFileDirPath()), getFileName())
//        if (serverFile.exists()) {
//            serverFile.delete()
//        }
//    }

    private fun getFileDirPath(): String {
        return BaseApplication.context?.filesDir?.absolutePath!!
    }

    private fun getFileName(): String {
        return "ice_live_server.txt"
    }


    fun changeBaseUrl() {
        var baseUrls: MutableList<String> = getServer()  // baseUrls[0] 服务器地址，baseUrls[1] H5地址
        LogUtils.d("baseUrl===$baseUrls")
        if (ObjectUtils.isNotEmpty(baseUrls)) {
            BaseConstant.BASE_URL = baseUrls[0]
            BaseConstant.BASE_h5 = baseUrls[1]
            RetrofitUrlManager.getInstance()
                .setGlobalDomain(BaseConstant.BASE_URL) // 设置全局base url
            RetrofitUrlManager.getInstance().setDebug(AppUtils.isAppDebug()) // 开启debug调试
        }
    }


    private var serverType = 0
    fun showSwitchServer(activity: Activity, view: View) {
        // 如果是release，不显示切换服务
        if (!AppUtils.isAppDebug()) {
            return
        }
        val serverView = LayoutInflater.from(activity).inflate(R.layout.item_pop_server, null)
        val pop = BackgroundBlurPopupWindow(serverView, -2, -2, activity, false)
        pop.setBlurRadius(0)
//        pop.darkBelow(ivLogo)
        pop.darkAnimStyle = R.style.popAnim
        pop.setDarkColor(ContextCompat.getColor(activity, R.color.pop_comment_bg))

        // ============================接口服务切换===========================================
        val tvAli = serverView.findViewById<TextView>(R.id.tvAli)
        val tvAliUat = serverView.findViewById<TextView>(R.id.tvAliUat)
        val tvLocal = serverView.findViewById<TextView>(R.id.tvLocal)
        val tvHuawei = serverView.findViewById<TextView>(R.id.tvHuawei)
        val tvWei = serverView.findViewById<TextView>(R.id.tvWei)
        val tvFu = serverView.findViewById<TextView>(R.id.tvFu)
        val tvYu = serverView.findViewById<TextView>(R.id.tvYu)
        val tvLiu = serverView.findViewById<TextView>(R.id.tvLiu)
        val list = mutableListOf<TextView>()
        list.add(tvAli)
        list.add(tvAliUat)
        list.add(tvLocal)
        list.add(tvHuawei)
        list.add(tvWei)
        list.add(tvFu)
        list.add(tvYu)
        list.add(tvLiu)
        serverType = getServerType()
        when (serverType) {
            0 -> {
                Collections.swap(list, list.indexOf(tvAli), 0)
            }
            1 -> {
                Collections.swap(list, list.indexOf(tvAliUat), 0)
            }
            2 -> {
                Collections.swap(list, list.indexOf(tvLocal), 0)
            }
            3 -> {
                Collections.swap(list, list.indexOf(tvHuawei), 0)
            }
            4 -> {
                Collections.swap(list, list.indexOf(tvWei), 0)
            }
            5 -> {
                Collections.swap(list, list.indexOf(tvFu), 0)
            }
            6 -> {
                Collections.swap(list, list.indexOf(tvYu), 0)
            }
            7 -> {
                Collections.swap(list, list.indexOf(tvLiu), 0)
            }
        }
        setServerTextColor(list)

        val clickListener: View.OnClickListener = View.OnClickListener {
            when (it.id) {
                R.id.tvAli -> {
                    LogUtils.d("clickListener===阿里生产")
                    BaseConstant.BASE_URL = BaseConstant.BASE_ALI_URL
                    BaseConstant.BASE_h5 = BaseConstant.BASE_ALI_h5
                    serverType = 0
                }
                R.id.tvAliUat -> {
                    LogUtils.d("clickListener===阿里UAT生产")
                    BaseConstant.BASE_URL = BaseConstant.BASE_ALI_UAT_URL
                    BaseConstant.BASE_h5 = BaseConstant.BASE_ALI_UAT_h5
                    serverType = 1
                }
                R.id.tvLocal -> {
                    LogUtils.d("clickListener===本地")
                    BaseConstant.BASE_URL = BaseConstant.BASE_LOCAL_URL
                    BaseConstant.BASE_h5 = BaseConstant.BASE_LOCAL_h5
                    serverType = 2
                }
                R.id.tvHuawei -> {
                    LogUtils.d("clickListener===华为")
                    BaseConstant.BASE_URL = BaseConstant.BASE_HUAWEI_URL
                    BaseConstant.BASE_h5 = BaseConstant.BASE_HUAWEI_h5
                    serverType = 3
                }
                R.id.tvWei -> {
                    LogUtils.d("clickListener===小伟")
                    BaseConstant.BASE_URL = BaseConstant.BASE_WEI_URL
                    BaseConstant.BASE_h5 = BaseConstant.BASE_WEI_h5
                    serverType = 4
                }
                R.id.tvFu -> {
                    LogUtils.d("clickListener===付志豪")
                    BaseConstant.BASE_URL = BaseConstant.BASE_WU_URL
                    BaseConstant.BASE_h5 = BaseConstant.BASE_WU_h5
                    serverType = 5
                }
                R.id.tvYu -> {
                    LogUtils.d("clickListener===黄宇飞")
                    BaseConstant.BASE_URL = BaseConstant.BASE_YU_URL
                    BaseConstant.BASE_h5 = BaseConstant.BASE_YU_h5
                    serverType = 6
                }
                R.id.tvLiu -> {
                    LogUtils.d("clickListener===刘震海")
                    BaseConstant.BASE_URL = BaseConstant.BASE_LIU_URL
                    BaseConstant.BASE_h5 = BaseConstant.BASE_YU_h5
                    serverType = 7
                }
            }
            writeString(BaseConstant.BASE_URL + splitChar + BaseConstant.BASE_h5 + splitChar + serverType)
            pop.dismiss()
            AppUtils.relaunchApp(true)
        }
        tvAli.setOnClickListener(clickListener)
        tvAliUat.setOnClickListener(clickListener)
        tvLocal.setOnClickListener(clickListener)
        tvHuawei.setOnClickListener(clickListener)
        tvWei.setOnClickListener(clickListener)
        tvFu.setOnClickListener(clickListener)
        tvYu.setOnClickListener(clickListener)
        tvLiu.setOnClickListener(clickListener)
        pop.showAtLocation(view, Gravity.CENTER, 0, 0)
    }

    /**
     * 设置text的颜色，默认第一个设置选中颜色
     */
    private fun setServerTextColor(
        textList: MutableList<TextView>
    ) {
        if (ObjectUtils.isEmpty(textList)) return
        for ((i, v) in textList.withIndex()) {
            if (i == 0) {
                v.setTextColor(
                    ContextCompat.getColor(
                        BaseApplication.context?.applicationContext!!,
                        R.color.color_CF0703
                    )
                )
            } else {
                v.setTextColor(
                    ContextCompat.getColor(
                        BaseApplication.context?.applicationContext!!,
                        R.color.color_222222
                    )
                )
            }
        }
    }

    /**
     * 获取版本名称
     *
     */
    fun getVersionName(context: Context): String? {
        //获取包管理器
        val pm = context.packageManager
        //获取包信息
        try {
            val packageInfo = pm.getPackageInfo(context.packageName, 0)
            //返回版本号
            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return null

    }


    /**
     * 检查是否安装微信
     */
    fun checkInstallWx(): Boolean {
        val packageManager = BaseApplication.context?.packageManager // 获取packagemanager
        val pinfo =
            packageManager?.getInstalledPackages(0) // 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (i in pinfo.indices) {
                val pn = pinfo[i].packageName
                if (pn == "com.tencent.mm") {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 超过万的显示格式
     *
     * @num 数字
     * @endNum 显示几位小数
     * @unit 超过万后的单位后缀
     */
    fun formatBigNum(num: String?, endNum: String?, unit: String?): String? {
        var number = "0"
        if (ObjectUtils.isEmpty(num)) {
            return number
        }
        try {
            val numberLong = num!!.toLong()
            var tempLong = 0f
            if (numberLong >= 10000) {
                val format = DecimalFormat(endNum)   // .00
                tempLong = numberLong * 1.0f / 10000
                number = format.format(tempLong) + unit
            } else {
                number = num
            }
        } catch (e: Exception) {
            LogUtils.e("formatBigNum====$e")
        }
        return number
    }


    /**
     * 获取版本号
     *
     */
//    fun getVersionCode(context: Context): Int {
//
//        //获取包管理器
//        val pm = context.packageManager
//        //获取包信息
//        try {
//            val packageInfo = pm.getPackageInfo(context.packageName, 0)
//            //返回版本号
//            return packageInfo.versionCode
//        } catch (e: PackageManager.NameNotFoundException) {
//            e.printStackTrace()
//        }
//
//        return 0
//
//    }
//
//    fun checkSHA1(context: Context): String? {
//        try {
//            val info = context.packageManager.getPackageInfo(
//                context.packageName, PackageManager.GET_SIGNATURES
//            )
//            val cert = info.signatures[0].toByteArray()
//            val md = MessageDigest.getInstance("SHA1")
//            val publicKey = md.digest(cert)
//            val hexString = StringBuffer()
//            for (i in publicKey.indices) {
//                val appendString =
//                    Integer.toHexString(0xFF and publicKey[i].toInt())
//                        .toUpperCase(Locale.US)
//                if (appendString.length == 1) hexString.append("0")
//                hexString.append(appendString)
//                hexString.append(":")
//            }
//            val result = hexString.toString()
//            return result.substring(0, result.length - 1)
//        } catch (e: PackageManager.NameNotFoundException) {
//            e.printStackTrace()
//        } catch (e: NoSuchAlgorithmException) {
//            e.printStackTrace()
//        }
//        return null
//    }

//    fun checkNetwork(context: Context): Boolean {
//        val connectivity =
//            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//                ?: return false
//        @SuppressLint("MissingPermission") val info =
//            connectivity.activeNetworkInfo
//        return info != null && info.isConnected
//    }

    /**
     * @param context
     * @return WebView 的缓存路径
     */
    fun getCachePath(context: Context): String? {
        return context.cacheDir
            .absolutePath + File.separator + "agentweb-cache"
    }


    /**
     * 返回保存的图片路径
     */
    fun saveImage(context: Activity): File {
        val storageDir =
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(
            storageDir,
            "worldhds_" + System.currentTimeMillis() + ".jpg"
        )
    }

    /**
     * 返回保存的gif图片路径
     */
    fun saveGif(context: Context): File {
        val storageDir =
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(
            storageDir,
            "worldhds_" + System.currentTimeMillis() + ".gif"
        )
    }

    /**
     * 兼容Android Q以及低版本 扫描图片到系统相册中
     */
    fun insertImage(context: Context, file: File?) {
        if (file == null) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.DESCRIPTION, "This is an news image")
            values.put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            values.put(MediaStore.Images.Media.TITLE, "Image.jpg")
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/")

            val external: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val resolver: ContentResolver = context.contentResolver
            val insertUri: Uri? = resolver.insert(external, values)
            var inputStream: BufferedInputStream? = null
            var os: OutputStream? = null
            try {
                inputStream = BufferedInputStream(FileInputStream(file))
                if (insertUri != null) {
                    os = resolver.openOutputStream(insertUri)
                }
                if (os != null) {
                    val buffer = ByteArray(1024 * 4)
                    var len: Int
                    while (inputStream.read(buffer).also { len = it } != -1) {
                        os.write(buffer, 0, len)
                    }
                    os.flush()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                CloseUtils.closeIO(os, inputStream)
            }
        } else {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.DATA, file.absolutePath)
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            val uri: Uri = context.contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            intent.data = uri
            context.sendBroadcast(intent)
        }
    }

    fun timeParse(duration: Long): String? {
        var time: String? = ""
        val minute = duration / 60000
        val seconds = duration % 60000
        val second = (seconds.toFloat() / 1000).roundToInt().toLong()
        if (minute < 10) {
            time += "0"
        }
        time += "$minute:"
        if (second < 10) {
            time += "0"
        }
        time += second
        return time
    }

//    fun goToTaobao(url: String, context: Context) {
//        var productUrl = url.trim()
//        // 是否为淘宝商品或天猫商品，并且是否安装淘宝客户端
//        if ((productUrl.indexOf("detail.tmall") != -1 || productUrl.indexOf("item.taobao") != -1)
//            && Utils.checkPackage("com.taobao.taobao", context)
//        ) {
//            val intent = Intent("android.intent.action.VIEW")
//            intent.data = Uri.parse(productUrl)
//            intent.setClassName(
//                "com.taobao.taobao",
//                "com.taobao.tao.detail.activity.DetailActivity"
//            )
//            context.startActivity(intent)
//        } else if (productUrl.startsWith("complexpackage")) {
//            if (checkPackage("com.tencent.mm", context)) {
//                WxUtils.jumpOfficialAccounts(productUrl)
//            } else {
//                ToastUtils.showShort("请安装微信客户端，否则无法跳转小程序！")
//            }
//            LogUtils.d("111111")
//        } else {
//            val intent = Intent("android.intent.action.VIEW")
//            intent.data = Uri.parse(productUrl)
//            context.startActivity(intent)
//        }
//    }


    fun checkPackage(
        packageName: String?,
        context: Context
    ): Boolean {
        return if (packageName == null || "" == packageName) false else try {
            context.packageManager
                .getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * 判断Activity是否Destroy
     * @param mActivity
     * @return true:已销毁
     */
    @SuppressLint("ObsoleteSdkInt")
    fun isDestroy(mActivity: Activity?): Boolean {
        return mActivity == null ||
                mActivity.isFinishing ||
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed
    }
}

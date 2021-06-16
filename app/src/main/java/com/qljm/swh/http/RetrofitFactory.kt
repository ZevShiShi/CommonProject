package com.qljm.swh.http


import android.os.Build
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ObjectUtils
import com.blankj.utilcode.util.SPUtils
import com.qljm.icelive.utils.AppUuidUtil
import com.qljm.swh.utils.MD5Utils
import com.qljm.swh.base.BaseApplication
import com.qljm.swh.common.BaseConstant
import com.qljm.swh.utils.AppUtil
import com.qljm.swh.utils.MultiLanguageUtils
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.LinkedHashMap


class RetrofitFactory private constructor() {
    companion object {
        val instance: RetrofitFactory = RetrofitFactory()
    }

    private var retrofit: Retrofit
    private var interceptor: Interceptor    // 添加header拦截器
    private var encryption: Interceptor     // 加密拦截器

    init {
        interceptor = Interceptor { chain ->
            var country = ""
            var lang = if (SPUtils.getInstance().getString(BaseConstant.LOCALE_LANGUAGE)
                    .isNotEmpty()
                && SPUtils.getInstance().getString(BaseConstant.LOCALE_COUNTRY)
                    .isNotEmpty()
            ) {
                country = SPUtils.getInstance().getString(BaseConstant.LOCALE_COUNTRY)
                SPUtils.getInstance().getString(BaseConstant.LOCALE_LANGUAGE) + "_" + country
            } else {
                country = MultiLanguageUtils.getAppLocale(BaseApplication.app).country
                (MultiLanguageUtils.getAppLocale(BaseApplication.app).language) + "_" + country
            }
            lang = when (lang) {
                "zh_TW" -> {
                    "tc_CN"
                }
                "en_US" -> {
                    "en_US"
                }
                else -> {
                    "zh_CN"
                }
            }
            val request = chain.request().newBuilder()
                .addHeader("charset", "UTF-8")
                .addHeader("Content-Type", "application/json")
                .addHeader("language", lang) //language,值：zh_CN:简体中文,tc_CN:繁体中文,en_US:英文
                .addHeader(
                    "token",
                    SPUtils.getInstance().getString(BaseConstant.KEY_SP_TOKEN)
                )
                .addHeader("client_id", AppUuidUtil.uuid)
                .addHeader(
                    "client_info",
                    Build.BRAND + ";" + AppUtil.getVersionName(
                        BaseApplication.app
                    ) + ";" + Build.MODEL + ";" + Build.VERSION.RELEASE + ";"
                            + lang + ";" + AppUtil.getAppChannel(
                        BaseApplication.app,
                        "UMENG_CHANNEL"
                    )
                )
                .build()
            LogUtils.d("interceptor=============================================${request.url}")
            return@Interceptor chain.proceed(request)
        }

        /**
         * 验签规则：
         * 例如传给后台的参数对象有：param={id:1; pageNo:1; pageNum:10; lastIndex:1566984556;}
         * 前端操作：
         * 1、将param中的参数按照key的首字母升序排列后将value值进行拼接成字符串，如：
         * value1value2value3value4value5
         * 2、后台设定一串不变的随机字符串(xxxxxxxxxxxxxxxxxxxxxx)给到前端写死，然后将其进行md5加密;
         * 3、拼接后的的value值参数字符串 + md5加密后的随机字符串再进行拼接;
         * 4、对第3步拼接后的字符串再进行md5加密;
         * 5、将第4步加密后的值插入请求头中传给后台的对象中，即:sign=第4步加密后的值；
         *    md5加密是小写的32位;
         *
         * 后端操作：
         * 拿到前端传给后台的参数按照上述步骤操作，最后后台生成的sign对比前端传给后台的sign的value值是否相同,
         * 相同则成功，否则返回签名失败的操作,接口提示请求数据错误.
         *
         */
        encryption = Interceptor { chain ->
            var request = chain.request()
            request = if ("GET" == request.method) {
                addGetParams(request)
            } else {
                addPostParams(request)
            }
            return@Interceptor chain.proceed(request)
        }

        val okHttpClient = RetrofitUrlManager.getInstance().with(initClient()).build()
        retrofit = Retrofit.Builder()
            .baseUrl(BaseConstant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }

    private fun addPostParams(request: Request): Request {
        var formBody = request.body
        // form 表单
        if (formBody is FormBody) {
            val fieldSize = formBody?.size ?: 0
            val keyNames = mutableListOf<String>()
            keyNames.add("sign:")
            for (i in 0 until fieldSize) {
                keyNames.add(formBody.name(i) + ":" + formBody.value(i))
            }
            Collections.sort(keyNames, String.CASE_INSENSITIVE_ORDER) // 参数根据首字母排序
            val sortMap = LinkedHashMap<String, String>()  // 首字母升序排序完成的map
            val strKeyBuilder = StringBuilder()
            for (content in keyNames) {
                val key = content.split(":")[0]
                val value = content.split(":")[1]
                sortMap[key] = value
                strKeyBuilder.append(value)
            }
            val tempSign = MD5Utils.encryptMD5ToString(BaseConstant.SIGN)
            strKeyBuilder.append(tempSign)
            val finalSign = MD5Utils.encryptMD5ToString(strKeyBuilder.toString())
            sortMap["sign"] = finalSign!!
            LogUtils.d("addPostParams POST values=$sortMap")
            LogUtils.d("addPostParams POST finalSign = $finalSign,url====${request.url}")
//            builder.add("sign", finalSign)
            return request.newBuilder().addHeader("sign", finalSign).build()
        }
        // 获取请求的数据,@Body
        val charset = Charset.forName("UTF-8")
        val buffer = Buffer()
        formBody?.writeTo(buffer)
        val requestData: String =
            URLDecoder.decode(buffer.readString(charset).trim(), "utf-8")

        // 普通表单
        if (ObjectUtils.isNotEmpty(requestData)) {
            // @Body 方式
            val jsonObj = JSONObject(requestData)
            jsonObj.put("sign", "") // 添加公共参数
            val keyNames = mutableListOf<String>()
            jsonObj.keys().forEach {
                keyNames.add(it)
            }
            Collections.sort(keyNames, String.CASE_INSENSITIVE_ORDER) // 参数根据首字母排序
            val sortMap = LinkedHashMap<String, String>()  // 首字母升序排序完成的map
            val strKeyBuilder = StringBuilder()
            for (key in keyNames) {
                sortMap[key] = jsonObj.optString(key)
                strKeyBuilder.append(jsonObj.optString(key))
            }
            val tempSign = MD5Utils.encryptMD5ToString(BaseConstant.SIGN)
            strKeyBuilder.append(tempSign)
            val finalSign = MD5Utils.encryptMD5ToString(strKeyBuilder.toString())
            sortMap["sign"] = finalSign!!
            LogUtils.d("addPostParams POST values=$sortMap")
            LogUtils.d("addPostParams POST finalSign = $finalSign,url====${request.url}")
            jsonObj.put("sign", finalSign) // 添加公共参数
//            val body = jsonObj.toString().toRequestBody(formBody?.contentType())
            return request.newBuilder().addHeader("sign", finalSign).build()
        } else {
            // 无@Body方式
            LogUtils.d("addPostParams POST @Query")
            val jsonObj = JSONObject("{}")
            val tempSign = MD5Utils.encryptMD5ToString(BaseConstant.SIGN)
            val finalSign = MD5Utils.encryptMD5ToString(tempSign)
            val body = jsonObj.toString().toRequestBody(formBody?.contentType())
            return request.newBuilder().post(body).addHeader("sign", finalSign!!).build()
        }
    }

    private fun addGetParams(request: Request): Request {
        // @Query or @QueryMap方式
        var httpUrl = request.url.newBuilder()
            .addQueryParameter("sign", "")   // 参数占位
            .build()

        val params: Set<String> = httpUrl.queryParameterNames
        val keyNames = mutableListOf<String>()
        keyNames.addAll(params)
        Collections.sort(keyNames, String.CASE_INSENSITIVE_ORDER) // 参数根据首字母排序
        val strKeyBuilder = StringBuilder()
        val sortMap = LinkedHashMap<String, String>()
        for (key in keyNames) {
            sortMap[key] = httpUrl.queryParameter(key)!!
            strKeyBuilder.append(httpUrl.queryParameter(key)!!)
        }
        val tempSign = MD5Utils.encryptMD5ToString(BaseConstant.SIGN)
        strKeyBuilder.append(tempSign)
        val finalSign = MD5Utils.encryptMD5ToString(strKeyBuilder.toString())
        sortMap["sign"] = finalSign!!
        LogUtils.d("addGetParams GET values=$sortMap")
        LogUtils.d("addGetParams GET finalSign = $finalSign,url====${request.url}")
//        httpUrl = request.url.newBuilder()
//            .addQueryParameter("sign", finalSign)   // 赋值
//            .build()
        return request.newBuilder().addHeader("sign", finalSign).build()
    }


    private fun initClient(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(encryption)
            .addInterceptor(initLogInterceptor())
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
//            .retryOnConnectionFailure(false) // 不重试请求
//           .proxy(Proxy.NO_PROXY)   //禁止抓包
    }

    private fun initLogInterceptor(): Interceptor {
//        val log = HttpLoggingInterceptor()
//        // release模式不打印日志
//        log.level = HttpLoggingInterceptor.Level.BODY
//        return log
        val level = if (AppUtils.isAppDebug()) {
            Level.BASIC
        } else {
            Level.NONE
        }
        return LoggingInterceptor.Builder()
            .setLevel(level)

            .tag("okhttp")
            .build()
    }


    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }
}
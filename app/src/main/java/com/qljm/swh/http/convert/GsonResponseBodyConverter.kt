/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain empty_view copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qljm.swh.http.convert

import com.google.gson.TypeAdapter

import okhttp3.ResponseBody
import retrofit2.Converter

import java.io.IOException

internal class GsonResponseBodyConverter<T>(private val adapter: TypeAdapter<T>) : Converter<ResponseBody, T> {

    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T? {
        val gson = value.string()
        //        String decode = URLDecoder.decode(gson, "UTF-8");
        //解密
        //   String decodeStr = Des3.decode(gson);
        //        LogUtils.d(decodeStr);
        return adapter.fromJson(gson)

    }
}

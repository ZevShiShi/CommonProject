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

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Converter
import java.io.IOException
import java.lang.reflect.Type
import java.nio.charset.Charset


class GsonRequestBodyConverter<T> internal constructor(
    private val gson: Gson,
    private val type: Type
) :
    Converter<T, RequestBody> {

    @Throws(IOException::class)
    override fun convert(value: T): RequestBody? {
        //加密
        val json = gson.toJson(value)
        return RequestBody.create(MEDIA_TYPE, json)


    }

    companion object {
        //        private val MEDIA_TYPE = MediaType.parse("application/x-www-form-urlencoded")
        private val MEDIA_TYPE = "application/json".toMediaTypeOrNull()
        private val UTF_8 = Charset.forName("UTF-8")
    }
}

package com.qljm.swh.bean

class BaseArrayBean<T>(
    val msg: String,
    val code: Int,
    val data: ArrayList<T>
) {

    fun isSuccess(): Boolean {
        if (code == 0) {
            return true
        }
        return false
    }
}
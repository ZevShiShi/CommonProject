package com.qljm.swh.bean.base

data class BaseBean<T>(
    val msg: String,
    val code: Int,
    val data: T
) {

    fun isSuccess(): Boolean {
        if (code == 0) {
            return true
        }
        return false
    }
}
package com.example.yuehaoting.util

import cn.hutool.core.codec.Rot
import com.example.yuehaoting.util.Encryption.encryption


/**
* 作者: LiangKe
* 时间: 2021/11/4 20:06
* 描述: 
*/object Encryption {
    /**
     * 加密
     */
    fun encryption(add:String){

        val encode13 = Rot.encode13(add)
        println("加密 : $encode13")
        decrypt(encode13)
    }

    /**
     * 解密
     */
    fun decrypt(add:String){
        val decode13 = Rot.decode13(add)

        println("解密 : $decode13")

    }
}


fun main() {
    encryption("true")
}
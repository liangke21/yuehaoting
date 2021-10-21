package com.example.yuehaoting.kotlin

/**
 * 作者: 天使
 * 时间: 2021/6/27 8:08
 * 描述:
 */

class MyMain() {

    inner class Mian() {
        val m: MyMain get() = this@MyMain
    }

    class Mian2() {
        val m: MyMain get() = MyMain()
    }

    fun play() {
        println("哇咔咔")
    }
}


fun main() {

    val m1 = MyMain()

   // val m2 = MyMain.Mian()

    val m3 = MyMain.Mian2()
}
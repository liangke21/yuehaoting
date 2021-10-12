package com.example.yuehaoting.kotlin

/**
 * 作者: 天使
 * 时间: 2021/6/27 8:08
 * 描述:
 */

class MyMain() {

    inline fun test(block1: (a: String) -> String, noinline block2: () -> Unit) {
        block1("哇咔咔")
        block2()
    }

    private var a = 0

    fun seta(a: Int) {
        this.a = a
    }
}


fun main() {


    MyMain().test({
        println(it)
        it
    }, {
        println("蛮吉")
    })


    val m = true
    when (m) {
        true -> println("哇咔咔")
        false -> println("巴卡八嘎")
    }

    val k = true
    if (k) {
        println("哇咔咔")
    } else {
        println("巴卡八嘎")
    }


}

package com.example.musiccrawler.kotlin

/**
 * 作者: 天使
 * 时间: 2021/6/27 8:08
 * 描述:
 */

class MyMain() {

    inline  fun test(block1: (a: String) -> String, noinline block2: () -> Unit) {
        block1("哇咔咔")
        block2()
    }
}

fun main() {
    MyMain().test({
       println(it)
        it
    },{
       println("蛮吉")
    })
}

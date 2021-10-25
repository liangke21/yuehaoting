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


enum class Color{
    RED,BLACK,BLUE,GREEN,WHITE
}

/*enum class Color(val rgb: Int) {
    RED(1),
    GREEN(2),
    BLUE(3)
}*/


fun main() {

//println(Color.values())





}
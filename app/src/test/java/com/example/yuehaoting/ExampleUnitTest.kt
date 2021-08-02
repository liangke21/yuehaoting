package com.example.yuehaoting


import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

        val numbersMap = mapOf("key1" to "sss", "key2" to 2, "key3" to 3, "key4" to 1)
        println("All keys: ${numbersMap.keys}")
        println("All values: ${numbersMap.values}")
        if ("key2" in numbersMap) println("Value by key \"key2\": ${numbersMap["key2"]}")
        if (1 in numbersMap.values) println("The value 1 is in the map")
        if (numbersMap.containsValue(1)) println("The value 1 is in the map") // 同上
    }




}



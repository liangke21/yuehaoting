package com.example.musiccrawler.hifini

import android.app.Service
import android.os.Bundle
import android.os.Message
import android.os.Messenger
import android.text.TextUtils
import com.alibaba.fastjson.JSON
import com.example.musiccrawler.base.ServiceHandler
import com.google.gson.Gson
import org.jsoup.Jsoup

/**
 * 作者: 天使
 * 时间: 2021/7/11 13:31
 * 描述:
 */
object JsoupS {
    lateinit var gson: String

    fun jsoupSHiFiNiSearch(string: String,replyTo: Messenger): String {
        val attributes = ArrayList<DataSearch.Attributes>()
        val pageNumber = ArrayList<String>()
        try {
            val document = Jsoup.parse(string)

            val li = document.select("#body > div > div > div > div.card.search > div.card-body")[0].select("ul").select("li")



            li.forEach {
                val a = it.select("div > div")[0].select("a").toString()
                val aSplit = a.split(">")
                val href = aSplit[0].replace("<a href=\"", "").replace(".htm\"", "")

                val span = aSplit[1].replace("<span class=\"text-danger\"", "")

                val span2 = aSplit[2].replace("</span", "")

                val span3 = aSplit[3].replace("[FLAC", "").replace("/MP3", "").replace("-320", "").replace("K]", "")
                    .replace("</a","").replace("k]","")

              println(span3)
                val song = span + span2 + span3

                attributes.add(DataSearch.Attributes(song, href))

            }

            val mulatto = document.select("#body > div > div > div > nav > ul").select("li")
            var int = 0
            var size = 0
            mulatto.forEach {
                ++int
                if (size != 0 && mulatto.size - 1 != size) {
                    val lit = it.toString().replace("<li class=\"page-item\"><a href=\"", "").replace("\" class=", "")
                        .replace("page-link\">", "").replace("</a></li>", "").replace("\"$int", "")
                    pageNumber.add(lit)

                }
                size++
            }




            if (pageNumber.isEmpty()) {
                pageNumber.add("0")
            }


            val dataSearch = DataSearch(
                attributes = attributes,
                pageNumber = pageNumber
            )
            val gson = Gson().toJson(dataSearch)
            this.gson = gson
            println(gson)

            val msg = Message()
            msg.what = 2
            val bundle = Bundle()
            bundle.putString("json", gson)
            msg.data = bundle
            msg.replyTo=replyTo
            ServiceHandler().sendMessage(msg)

            return gson
        } catch (e: Exception) {
            if (pageNumber.isEmpty()) {
                pageNumber.add("0")
            }
            attributes.add(DataSearch.Attributes("0", "0"))

            val dataSearch = DataSearch(
                attributes = attributes,
                pageNumber = pageNumber
            )
            val gson = Gson().toJson(dataSearch)
            println(gson)

            this.gson = gson
            e.printStackTrace()
            return gson
        }

    }


    fun jsoupSHiFiNiThread(string: String) {

        try {
            val document = Jsoup.parse(string)
            //获取所有的script
            val script = document.getElementsByTag("script")[1]
            var data = script.data()
            val song = document.select("div.message.break-all").select("p")
            val lyric = java.lang.StringBuilder()
            for (i in song.indices) {
                var text = song[i].text()
                //符号转义
                text = text.replace("'", "\\\'")
                if (TextUtils.isEmpty(text)) {
                    continue
                }
                if (text.startsWith("https")) {
                    break
                }
                lyric.append(text).append("\n")
            }
            data = data.replace(
                "var ap4 = new APlayer({    element: document.getElementById('player4'),    narrow: false,    autoplay: false,\tpreload: 'none',    showlrc: false,    mutex: true,    theme: '#ad7a86',    music: [\t\t",
                ""
            ).replace(",    ]}); ", "")

            val music = JSON.parseObject(data, Music::class.java)
            music.type = document.select("li.breadcrumb-item").eq(1).text()
            music.hot = document.select("span.ml-2").eq(1).text().toInt()
            music.lyric = lyric.toString()
            val mTitle = music.title?.replace("'", "\\\'")
            val mAuthor = music.author?.replace("'", "\\\'")
            val songAttributes: String = "('$mTitle','$mAuthor','${music.url}','${music.pic}','${music.type}','${music.lyric}','${music.hot}'),"
            println("写入文件${music.url}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    internal class Music {
        /**
         * 封面
         */
        var pic: String? = null

        /**
         * 链接
         */
        var url: String? = null

        /**
         * 作者
         */
        var author: String? = null

        /**
         * 标题
         */
        var title: String? = null

        /**
         * 歌词
         */
        var lyric: String? = null

        /**
         * 歌曲类型
         */
        var type: String? = null

        /**
         * 热度
         */
        var hot: Int? = null
        override fun toString(): String {
            return "Music(pic=$pic, url=$url, author=$author, title=$title, lyric=$lyric, type=$type, hot=$hot)"
        }


    }

}
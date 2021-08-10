package com.example.yuehaoting.data.musicMiGu

class MiGuList : ArrayList<MiGuList.MiGuListItem>(){
    data class MiGuListItem(
        val album: String?,
        val artist: List<String?>?,
        val id: String?,
        val lyric_id: String?,
        val name: String?,
        val pic_id: String?,
        val source: String?,
        val url_id: String?
    )
}
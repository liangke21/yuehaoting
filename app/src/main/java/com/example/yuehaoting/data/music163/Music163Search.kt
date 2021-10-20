package com.example.yuehaoting.data.music163

class Music163Search : ArrayList<Music163Search.Music163SearchItem>(){
    data class Music163SearchItem(
        val album: String?,
        val artist: List<String?>?,
        val id: Int?,
        val lyric_id: Int?,
        val name: String?,
        val pic_id: String?,
        val source: String?,
        val url_id: Int?
    )
}
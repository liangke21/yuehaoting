package com.example.yuehaoting.data.musicMiGu

data class MiGuSearch(
    val keyword: String?,
    val musics: List<Music>?,
    val pageNo: String?,
    val pgt: Int?,
    val success: Boolean?
) {
    data class Music(
        val albumId: String?,
        val albumName: String?,
        val artist: String?,
        val auditionsFlag: Any?,
        val auditionsLength: Any?,
        val copyrightId: String?,
        val cover: String?,
        val has24Bitqq: String?,
        val has3Dqq: Any?,
        val hasHQqq: String?,
        val hasMv: Any?,
        val hasSQqq: String?,
        val id: String?,
        val isHdCrbt: Any?,
        val lyrics: String?,
        val mp3: String?,
        val mvCopyrightId: Any?,
        val mvId: String?,
        val singerId: String?,
        val singerName: String?,
        val songName: String?,
        val title: String?,
        val unuseFlag: Any?
    )
}
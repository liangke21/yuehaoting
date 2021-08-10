package com.example.yuehaoting.data.musicKuWo

class KuWoList : ArrayList<KuWoList.KuWoListItem>(){
    data class KuWoListItem(
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
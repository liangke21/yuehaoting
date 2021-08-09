package com.example.yuehaoting.data.musicQQ

data class QQSongList(
    val code: Int?,
    val `data`: Data?,
    val message: String?,
    val notice: String?,
    val subcode: Int?,
    val time: Int?,
    val tips: String?
) {
    data class Data(
        val keyword: String?,
        val priority: Int?,
        val qc: List<Any?>?,
        val semantic: Semantic?,
        val song: Song?,
        val tab: Int?,
        val taglist: List<Any?>?,
        val totaltime: Int?,
        val zhida: Zhida?
    ) {
        data class Semantic(
            val curnum: Int?,
            val curpage: Int?,
            val list: List<Any?>?,
            val totalnum: Int?
        )

        data class Song(
            val curnum: Int?,
            val curpage: Int?,
            val list: List<Lists>?,
            val totalnum: Int?
        ) {
            data class Lists(
                val action: Action?,
                val album: Album?,
                val chinesesinger: Int?,
                val desc: String?,
                val desc_hilight: String?,
                val docid: String?,
                val es: String?,
                val `file`: File?,
                val fnote: Int?,
                val genre: Int?,
                val grp: List<Grp?>?,
                val id: Int?,
                val index_album: Int?,
                val index_cd: Int?,
                val interval: Int?,
                val isonly: Int?,
                val ksong: Ksong?,
                val language: Int?,
                val lyric: String?,
                val lyric_hilight: String?,
                val mid: String?,
                val mv: Mv?,
                val name: String?,
                val newStatus: Int?,
                val nt: Long?,
                val ov: Int?,
                val pay: Pay?,
                val pure: Int?,
                val sa: Int?,
                val singer: List<Singer?>?,
                val status: Int?,
                val subtitle: String?,
                val t: Int?,
                val tag: Int?,
                val tid: Int?,
                val time_public: String?,
                val title: String?,
                val title_hilight: String?,
                val type: Int?,
                val url: String?,
                val ver: Int?,
                val volume: Volume?
            ) {
                data class Action(
                    val alert: Int?,
                    val icons: Int?,
                    val msg: Int?,
                    val switch: Int?
                )

                data class Album(
                    val id: Int?,
                    val mid: String?,
                    val name: String?,
                    val pmid: String?,
                    val subtitle: String?,
                    val title: String?,
                    val title_hilight: String?
                )

                data class File(
                    val b_30s: Int?,
                    val e_30s: Int?,
                    val media_mid: String?,
                    val size_128: Int?,
                    val size_128mp3: Int?,
                    val size_320: Int?,
                    val size_320mp3: Int?,
                    val size_aac: Int?,
                    val size_ape: Int?,
                    val size_dts: Int?,
                    val size_flac: Int?,
                    val size_ogg: Int?,
                    val size_try: Int?,
                    val strMediaMid: String?,
                    val try_begin: Int?,
                    val try_end: Int?
                )

                data class Grp(
                    val action: Action?,
                    val album: Album?,
                    val chinesesinger: Int?,
                    val desc: String?,
                    val desc_hilight: String?,
                    val docid: String?,
                    val es: String?,
                    val `file`: File?,
                    val fnote: Int?,
                    val genre: Int?,
                    val id: Int?,
                    val index_album: Int?,
                    val index_cd: Int?,
                    val interval: Int?,
                    val isonly: Int?,
                    val ksong: Ksong?,
                    val language: Int?,
                    val lyric: String?,
                    val lyric_hilight: String?,
                    val mid: String?,
                    val mv: Mv?,
                    val name: String?,
                    val newStatus: Int?,
                    val nt: Long?,
                    val ov: Int?,
                    val pay: Pay?,
                    val pure: Int?,
                    val sa: Int?,
                    val singer: List<Singer?>?,
                    val status: Int?,
                    val subtitle: String?,
                    val t: Int?,
                    val tag: Int?,
                    val tid: Int?,
                    val time_public: String?,
                    val title: String?,
                    val title_hilight: String?,
                    val type: Int?,
                    val url: String?,
                    val ver: Int?,
                    val volume: Volume?
                ) {
                    data class Action(
                        val alert: Int?,
                        val icons: Int?,
                        val msg: Int?,
                        val switch: Int?
                    )

                    data class Album(
                        val id: Int?,
                        val mid: String?,
                        val name: String?,
                        val pmid: String?,
                        val subtitle: String?,
                        val title: String?,
                        val title_hilight: String?
                    )

                    data class File(
                        val b_30s: Int?,
                        val e_30s: Int?,
                        val media_mid: String?,
                        val size_128: Int?,
                        val size_128mp3: Int?,
                        val size_320: Int?,
                        val size_320mp3: Int?,
                        val size_aac: Int?,
                        val size_ape: Int?,
                        val size_dts: Int?,
                        val size_flac: Int?,
                        val size_ogg: Int?,
                        val size_try: Int?,
                        val strMediaMid: String?,
                        val try_begin: Int?,
                        val try_end: Int?
                    )

                    data class Ksong(
                        val id: Int?,
                        val mid: String?
                    )

                    data class Mv(
                        val id: Int?,
                        val vid: String?
                    )

                    data class Pay(
                        val pay_down: Int?,
                        val pay_month: Int?,
                        val pay_play: Int?,
                        val pay_status: Int?,
                        val price_album: Int?,
                        val price_track: Int?,
                        val time_free: Int?
                    )

                    data class Singer(
                        val id: Int?,
                        val mid: String?,
                        val name: String?,
                        val title: String?,
                        val title_hilight: String?,
                        val type: Int?,
                        val uin: Int?
                    )

                    data class Volume(
                        val gain: Double?,
                        val lra: Double?,
                        val peak: Double?
                    )
                }

                data class Ksong(
                    val id: Int?,
                    val mid: String?
                )

                data class Mv(
                    val id: Int?,
                    val vid: String?
                )

                data class Pay(
                    val pay_down: Int?,
                    val pay_month: Int?,
                    val pay_play: Int?,
                    val pay_status: Int?,
                    val price_album: Int?,
                    val price_track: Int?,
                    val time_free: Int?
                )

                data class Singer(
                    val id: Int?,
                    val mid: String?,
                    val name: String?,
                    val title: String?,
                    val title_hilight: String?,
                    val type: Int?,
                    val uin: Long?
                )

                data class Volume(
                    val gain: Double?,
                    val lra: Double?,
                    val peak: Double?
                )
            }
        }

        data class Zhida(
            val type: Int?,
            val zhida_mv: ZhidaMv?
        ) {
            data class ZhidaMv(
                val desc: String?,
                val id: Int?,
                val pic: String?,
                val publish_date: String?,
                val title: String?,
                val vid: String?
            )
        }
    }
}
package com.example.yuehaoting.data.kugou

data class NewSong(
    val `data`: Data?,
    val errcode: Int?,
    val error: String?,
    val status: Int?
) {
    data class Data(
        val info: List<Info>?,
        val timestamp: Int?,
        val total: Int?
    ) {
        data class Info(
            val `320filesize`: Int?,
            val `320hash`: String?,
            val `320privilege`: Int?,
            val addtime: String?,
            val album_audio_id: Int?,
            val album_cover: String?,
            val album_id: String?,
            val audio_id: Int?,
            val bitrate: Int?,
            val bitrate_high: Int?,
            val bitrate_super: Int?,
            val cover: String?,
            val duration: Int?,
            val duration_high: Int?,
            val duration_super: Int?,
            val extname: String?,
            val extname_super: String?,
            val fail_process: Int?,
            val fail_process_320: Int?,
            val fail_process_sq: Int?,
            val feetype: Int?,
            val filename: String?,
            val filesize: Int?,
            val filesize_high: Int?,
            val filesize_super: Int?,
            val first: Int?,
            val has_accompany: Int?,
            val hash: String?,
            val hash_high: String?,
            val hash_super: String?,
            val inlist: Int?,
            val isfirst: Int?,
            val issue: Int?,
            val m4afilesize: Int?,
            val musical: Any?,
            val mvhash: String?,
            val old_cpy: Int?,
            val pay_type: Int?,
            val pay_type_320: Int?,
            val pay_type_sq: Int?,
            val pkg_price: Int?,
            val pkg_price_320: Int?,
            val pkg_price_sq: Int?,
            val price: Int?,
            val price_320: Int?,
            val price_sq: Int?,
            val privilege: Int?,
            val privilege_high: Int?,
            val privilege_super: Int?,
            val rank_cid: Int?,
            val recommend_reason: String?,
            val remark: String?,
            val rp_publish: Int?,
            val rp_type: String?,
            val sqfilesize: Int?,
            val sqhash: String?,
            val sqprivilege: Int?,
            val topic_url: String?,
            val topic_url_320: String?,
            val topic_url_sq: String?,
            val trans_param: TransParam?,
            val zone: String?
        ) {
            data class TransParam(
                val appid_block: String?,
                val cid: Int?,
                val classmap: Classmap?,
                val cpy_attr0: Int?,
                val cpy_grade: Int?,
                val cpy_level: Int?,
                val display: Int?,
                val display_rate: Int?,
                val hash_offset: HashOffset?,
                val musicpack_advance: Int?,
                val pay_block_tpl: Int?
            ) {
                data class Classmap(
                    val attr0: Int?
                )

                data class HashOffset(
                    val end_byte: Int?,
                    val end_ms: Int?,
                    val file_type: Int?,
                    val offset_hash: String?,
                    val start_byte: Int?,
                    val start_ms: Int?
                )
            }
        }
    }
}
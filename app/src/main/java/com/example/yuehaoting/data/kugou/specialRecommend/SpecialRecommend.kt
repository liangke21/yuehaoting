package com.example.yuehaoting.data.kugou.specialRecommend

data class SpecialRecommend(
    val `data`: Data?,
    val error_code: Int?,
    val status: Int?
) {
    data class Data(
        val OlexpIds: String?,
        val alg_id: Int?,
        val all_client_playlist_flag: Int?,
        val has_next: Int?,
        val operation_list: List<Operation?>?,
        val refresh_time: Int?,
        val session: String?,
        val show_time: Int?,
        val special_list: List<Special?>?
    ) {
        data class Operation(
            val extra: Extra?,
            val id: Int?,
            val image_url: String?,
            val position: Int?,
            val subscript: Int?,
            val title: String?,
            val type: Int?
        ) {
            data class Extra(
                val global_collection_id: String?,
                val specialid: Int?,
                val suid: Int?
            )
        }

        data class Special(
            val alg_path: String?,
            val bz_status: Int?,
            val collectType: Int?,
            val collectcount: Int?,
            val flexible_cover: String?,
            val from: Any?,
            val from_hash: String?,
            val from_tag: Int?,
            val global_collection_id: String?,
            val imgurl: String?,
            val intro: String?,
            val nickname: String?,
            val percount: Int?,
            val pic: String?,
            val play_count: Int?,
            val publishtime: String?,
            val report_info: String?,
            val show: String?,
            val singername: String?,
            val slid: Int?,
            val specialid: Int?,
            val specialname: String?,
            val suid: Int?,
            val sync: Int?,
            val tags: List<Tag?>?,
            val trans_param: TransParam?,
            val type: Int?,
            val ugc_talent_review: Int?
        ) {
            data class Tag(
                val tag_id: Int?,
                val tag_name: String?
            )

            data class TransParam(
                val special_tag: Int?
            )
        }
    }
}
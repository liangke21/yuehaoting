package com.example.yuehaoting.searchfor.data.kugou

data class KuGou(
    val ErrorCode: Int,
    val data: List<Data>,
    val error_code: Int,
    val status: Int
)
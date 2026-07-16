package com.desabanggle.ovylia

import com.google.gson.annotations.SerializedName

data class KadesStatistikResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: StatistikData?
)

data class StatistikData(
    @SerializedName("total_surat") val totalSurat: Int, // Menggunakan camelCase di Kotlin agar rapi, tapi tetap aman membaca snake_case dari PHP
    @SerializedName("menunggu") val menunggu: Int,
    @SerializedName("diproses") val diproses: Int,
    @SerializedName("selesai") val selesai: Int,
    @SerializedName("ditolak") val ditolak: Int,
    @SerializedName("total_aspirasi") val totalAspirasi: Int
)
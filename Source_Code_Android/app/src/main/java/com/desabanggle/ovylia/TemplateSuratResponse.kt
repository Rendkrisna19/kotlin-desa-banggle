package com.desabanggle.ovylia

import com.google.gson.annotations.SerializedName

data class TemplateSuratResponse(
    @SerializedName("id_template") val idTemplate: Int,
    @SerializedName("jenis_surat") val jenisSurat: String,
    @SerializedName("kode_surat") val kodeSurat: String,
    @SerializedName("isi_pembuka") val isiPembuka: String,
    @SerializedName("isi_penutup") val isiPenutup: String
)

data class CRUDTemplateResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String
)
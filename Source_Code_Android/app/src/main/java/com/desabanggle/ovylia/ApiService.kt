package com.desabanggle.ovylia

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // ==========================================
    // AUTHENTICATION & LAPORAN
    // ==========================================

    @FormUrlEncoded
    @POST("register.php")
    // Pastikan @Field mengikuti nama yang diharapkan oleh file register.php
    fun registerWarga(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("nama_lengkap") nama: String,
        @Field("nik") nik: String
    ): Call<ApiService.LoginResponse> // Menggunakan response yang sama agar mudah

    @FormUrlEncoded
    @POST("login.php")
    fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<AuthResponse>

    @FormUrlEncoded
    @POST("kirim_laporan.php")
    fun kirimLaporan(
        @Field("username") username: String,
        @Field("kategori") kategori: String,
        @Field("subjek") subjek: String,
        @Field("isi_laporan") isiLaporan: String
    ): Call<AuthResponse>

    @FormUrlEncoded
    @POST("kirim_penilaian.php")
    fun kirimPenilaian(
        @Field("username") username: String,
        @Field("rating") rating: Int,
        @Field("ulasan") ulasan: String
    ): Call<AuthResponse>

    // Penilaian per layanan — dengan jenis_layanan dan id_pengajuan
    @FormUrlEncoded
    @POST("kirim_penilaian.php")
    fun kirimPenilaianLengkap(
        @Field("username") username: String,
        @Field("rating") rating: Int,
        @Field("ulasan") ulasan: String,
        @Field("jenis_layanan") jenisLayanan: String,
        @Field("id_pengajuan") idPengajuan: Int
    ): Call<AuthResponse>

    // Model untuk surat yang sudah selesai (untuk penilaian per layanan)
    data class SuratSelesaiModel(
        val id_pengajuan: Int,
        val jenis_surat: String,
        val tanggal_pengajuan: String,
        val status_akhir: String
    )

    data class SuratSelesaiResponse(
        val status: String,
        val message: String?,
        val data: List<SuratSelesaiModel>
    )

    @GET("get_surat_selesai_warga.php")
    fun getSuratSelesaiWarga(
        @Query("username") username: String
    ): Call<SuratSelesaiResponse>



    // ==========================================
    // PENGELOLAAN AKUN WARGA (ADMIN)
    // ==========================================

    data class WargaModel(
        val username: String,
        val nama_lengkap: String?,
        val status_akun: String
    )

    data class WargaResponse(
        val status: String,
        val data: List<WargaModel>
    )

    @GET("get_pendaftaran_warga.php")
    fun getPendaftaranWarga(): Call<WargaResponse>

    @FormUrlEncoded
    @POST("verifikasi_akun.php")
    fun verifikasiAkun(
        @Field("username_warga") usernameWarga: String,
        @Field("status_baru") statusBaru: String
    ): Call<AuthResponse>


    // ==========================================
    // MANAJEMEN JENIS SURAT & SYARAT (ADMIN)
    // ==========================================

    data class JenisSuratModel(
        val id: Int,
        val nama_surat: String
    )

    data class JenisSuratResponse(
        val status: String,
        val data: List<JenisSuratModel>
    )

    @GET("get_jenis_surat.php")
    fun getJenisSurat(): Call<JenisSuratResponse>

    @FormUrlEncoded
    @POST("tambah_jenis_surat.php")
    fun tambahJenisSurat(
        @Field("nama_surat") namaSurat: String
    ): Call<AuthResponse>

    @FormUrlEncoded
    @POST("ubah_jenis_surat.php")
    fun ubahJenisSurat(
        @Field("id") id: Int,
        @Field("nama_surat") namaSurat: String
    ): Call<AuthResponse>

    @FormUrlEncoded
    @POST("hapus_jenis_surat.php")
    fun hapusJenisSurat(
        @Field("id") id: Int
    ): Call<AuthResponse>

    data class SyaratSuratModel(
        val id: Int,
        val id_jenis_surat: Int,
        val nama_syarat: String
    )

    data class SyaratSuratResponse(
        val status: String,
        val data: List<SyaratSuratModel>
    )

    @GET("get_syarat_surat.php")
    fun getSyaratSurat(
        @Query("id_jenis_surat") idJenisSurat: Int
    ): Call<SyaratSuratResponse>

    @FormUrlEncoded
    @POST("tambah_syarat_surat.php")
    fun tambahSyaratSurat(
        @Field("id_jenis_surat") idJenisSurat: Int,
        @Field("nama_syarat") namaSyarat: String
    ): Call<AuthResponse>

    @FormUrlEncoded
    @POST("hapus_syarat_surat.php")
    fun hapusSyaratSurat(
        @Field("id") id: Int
    ): Call<AuthResponse>


    // ==========================================
    // PROSES PENGAJUAN SURAT (ADMIN / RT-RW)
    // ==========================================

    @retrofit2.http.Multipart
    @POST("kelola_surat.php")
    fun uploadSurat(
        @retrofit2.http.Part("action") action: okhttp3.RequestBody,
        @retrofit2.http.Part("username") username: okhttp3.RequestBody,
        @retrofit2.http.Part("jenis_surat") jenisSurat: okhttp3.RequestBody,
        @retrofit2.http.Part("keperluan") keperluan: okhttp3.RequestBody,
        @retrofit2.http.Part("keterangan") keterangan: okhttp3.RequestBody,
        @retrofit2.http.Part file_syarat: okhttp3.MultipartBody.Part
    ): Call<AuthResponse>

    // Endpoint pengajuan lengkap dengan Nama Pengaju dan No KTP (TASK 3 - legacy, kept for compat)
    @retrofit2.http.Multipart
    @POST("kelola_surat.php")
    fun uploadSuratLengkap(
        @retrofit2.http.Part("action") action: okhttp3.RequestBody,
        @retrofit2.http.Part("username") username: okhttp3.RequestBody,
        @retrofit2.http.Part("jenis_surat") jenisSurat: okhttp3.RequestBody,
        @retrofit2.http.Part("nama_pengaju") namaPengaju: okhttp3.RequestBody,
        @retrofit2.http.Part("no_ktp") noKtp: okhttp3.RequestBody,
        @retrofit2.http.Part("keperluan") keperluan: okhttp3.RequestBody,
        @retrofit2.http.Part("keterangan") keterangan: okhttp3.RequestBody,
        @retrofit2.http.Part file_syarat: okhttp3.MultipartBody.Part
    ): Call<AuthResponse>

    // Endpoint baru: Pengajuan dengan 2 file terpisah (KTP + KK)
    @retrofit2.http.Multipart
    @POST("kelola_surat.php")
    fun uploadSuratDualFile(
        @retrofit2.http.Part("action") action: okhttp3.RequestBody,
        @retrofit2.http.Part("username") username: okhttp3.RequestBody,
        @retrofit2.http.Part("jenis_surat") jenisSurat: okhttp3.RequestBody,
        @retrofit2.http.Part("nama_pengaju") namaPengaju: okhttp3.RequestBody,
        @retrofit2.http.Part("no_ktp") noKtp: okhttp3.RequestBody,
        @retrofit2.http.Part("keperluan") keperluan: okhttp3.RequestBody,
        @retrofit2.http.Part("keterangan") keterangan: okhttp3.RequestBody,
        @retrofit2.http.Part fileKtp: okhttp3.MultipartBody.Part,
        @retrofit2.http.Part fileKk: okhttp3.MultipartBody.Part
    ): Call<AuthResponse>

    data class BerkasModel(
        val nama_syarat: String,
        val url_file: String
    )

    data class PengajuanSuratModel(
        val id_pengajuan: Int,
        val nama_warga: String,
        val jenis_surat: String,
        val tanggal_pengajuan: String,
        val status: String,
        val catatan: String?,
        val berkas_persyaratan: List<BerkasModel>
    )

    data class AdminPengajuanResponse(
        val status: String,
        val data: List<PengajuanSuratModel>
    )

    @GET("get_semua_pengajuan.php")
    fun getSemuaPengajuan(): Call<AdminPengajuanResponse>

    @FormUrlEncoded
    @POST("proses_status_surat.php")
    fun prosesStatusSurat(
        @Field("id_pengajuan") idPengajuan: Int,
        @Field("status") status: String,
        @Field("catatan") catatan: String
    ): Call<AuthResponse>

    // 🚀 TAMBAHAN: Untuk sinkronisasi dengan AdminLihatPengajuanActivity Anda
    data class PengajuanSurat(
        @com.google.gson.annotations.SerializedName("id_pengajuan")
        val id: Int,
        val username: String,
        val jenis_surat: String,
        val keperluan: String,
        val status: String,
        val dokumen: String?,
        val file_ktp: String?,
        val file_kk: String?
    )

    data class PengajuanResponse(
        val status: String,
        val data: List<PengajuanSurat>
    )

    @GET("get_semua_pengajuan.php") // Mengarah ke endpoint master pengajuan admin
    fun getAllPengajuan(): Call<PengajuanResponse>


    // ==========================================
    // FITUR LAMA: TRACKING SURAT WARGA & RT/RW
    // ==========================================

    @GET("get_pengajuan_warga.php")
    fun getPengajuanWarga(
        @Query("username_rtrw") usernameRtRw: String
    ): Call<PengajuanResponse>

    @FormUrlEncoded
    @POST("verifikasi_surat.php")
    fun verifikasiSurat(
        @Field("id_surat") idSurat: Int,
        @Field("status_baru") statusBaru: String,
        @Field("catatan_rtrw") catatan: String
    ): Call<AuthResponse>

    @GET("get_riwayat_verifikasi.php")
    fun getRiwayatVerifikasi(
        @Query("username_rtrw") usernameRtRw: String
    ): Call<PengajuanResponse>


    // ==========================================
    // MANAJEMEN SEKDES & KADES (PENGESAHAN)
    // ==========================================

    @GET("get_surat_siap_sahkan.php")
    fun getSuratSiapSahkan(): Call<AdminPengajuanResponse>

    @FormUrlEncoded
    @POST("sahkan_surat.php")
    fun sahkanSurat(
        @Field("id_pengajuan") idPengajuan: Int,
        @Field("token_ttd") tokenTtd: String
    ): Call<AuthResponse>

    data class RekapPelayananModel(
        val total_pengajuan: Int,
        val surat_pending: Int,
        val surat_disetujui: Int,
        val surat_ditolak: Int,
        val surat_selesai: Int
    )

    data class RekapResponse(
        val status: String,
        val data: RekapPelayananModel
    )

    @GET("get_rekap_pelayanan.php")
    fun getRekapPelayanan(): Call<RekapResponse>


    // ==========================================
    // MANAJEMEN LAPORAN & ASPIRASI WARGA
    // ==========================================

    data class LaporanWargaModel(
        val id_laporan: Int,
        val username: String,
        val kategori: String,
        val subjek: String,
        val isi_laporan: String,
        val tanggal_kirim: String,
        val status_tindak_lanjut: String,
        val tanggapan_admin: String?
    )

    data class LaporanWargaResponse(
        val status: String,
        val data: List<LaporanWargaModel>
    )

    @GET("get_semua_laporan_warga.php")
    fun getSemuaLaporanWarga(): Call<LaporanWargaResponse>

    @FormUrlEncoded
    @POST("tindak_lanjuti_laporan.php")
    fun tindakLanjutiLaporan(
        @Field("id_laporan") idLaporan: Int,
        @Field("status_baru") statusBaru: String,
        @Field("tanggapan") tanggapan: String
    ): Call<AuthResponse>


    // ==========================================
    // MANAJEMEN BERITA DESA
    // ==========================================

    data class BeritaModel(
        val id_berita: Int,
        val judul: String,
        val isi_berita: String,
        val tanggal_post: String
    )

    data class BeritaResponse(
        val status: String,
        val data: List<BeritaModel>
    )

    @GET("get_semua_berita.php")
    fun getSemuaBerita(): Call<BeritaResponse>

    @FormUrlEncoded
    @POST("tambah_berita.php")
    fun tambahBerita(
        @Field("judul") judul: String,
        @Field("isi_berita") isiBerita: String
    ): Call<AuthResponse>

    @FormUrlEncoded
    @POST("ubah_berita.php")
    fun ubahBerita(
        @Field("id_berita") idBerita: Int,
        @Field("judul") judul: String,
        @Field("isi_berita") isiBerita: String
    ): Call<AuthResponse>

    @FormUrlEncoded
    @POST("hapus_berita.php")
    fun hapusBerita(
        @Field("id_berita") idBerita: Int
    ): Call<AuthResponse>


    // ==========================================
    // MANAGEMENT ADMIN & DETAIL DASHBOARD SEKDES
    // ==========================================

    data class AdminModel(
        val id_admin: Int,
        val username: String,
        val nama_lengkap: String,
        val jabatan: String
    )

    data class AdminLoginResponse(
        val status: String,
        val message: String,
        val data: AdminModel?
    )

    @FormUrlEncoded
    @POST("login_admin.php")
    fun loginAdmin(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<AdminLoginResponse>

    data class SuratModel(
        val id_pengajuan: Int,
        val jenis_surat: String,
        val nama_pemohon: String,
        val nik_pemohon: String,
        val status_admin: String,
        val status_rtrw: String,
        val tanggal_pengajuan: String
    )

    data class SuratVerifiedResponse(
        val status: String,
        val data: List<SuratModel>
    )

    @GET("get_surat_terverifikasi.php")
    fun getSuratTerverifikasi(): Call<SuratVerifiedResponse>

    data class DetailSuratResponse(
        val status: String,
        val message: String?,
        val data: DetailSuratModel?
    )

    data class DetailSuratModel(
        val id_pengajuan: Int,
        val jenis_surat: String,
        val nama_pemohon: String,
        val nik_pemohon: String,
        val keperluan: String,
        val tanggal_pengajuan: String,
        val status_rtrw: String,
        val catatan_rtrw: String?,
        val tanggal_rtrw: String?,
        val status_admin: String,
        val catatan_admin: String?,
        val tanggal_admin: String?
    )

    @GET("get_detail_surat_sekdes.php")
    fun getDetailSuratSekdes(
        @Query("id_pengajuan") idPengajuan: Int
    ): Call<DetailSuratResponse>

    data class ActionSekdesResponse(
        val status: String,
        val message: String
    )

    @FormUrlEncoded
    @POST("update_status_sekdes.php")
    fun updateStatusSekdes(
        @Field("id_pengajuan") idPengajuan: Int,
        @Field("status_akhir") statusAkhir: String,
        @Field("catatan_sekdes") catatanSekdes: String?
    ): Call<ActionSekdesResponse>


    // ==========================================
    // TEMPLATE SURAT & STATISTIK KADES
    // ==========================================

    @GET("kelola_template.php?action=read")
    fun getTemplateSurat(): Call<List<TemplateSuratResponse>>

    @FormUrlEncoded
    @POST("kelola_template.php?action=update")
    fun updateTemplateSurat(
        @Field("id_template") idTemplate: Int,
        @Field("jenis_surat") jenisSurat: String,
        @Field("kode_surat") kodeSurat: String,
        @Field("isi_pembuka") isiPembuka: String,
        @Field("isi_penutup") isiPenutup: String
    ): Call<CRUDTemplateResponse>

    data class LoginResponse(
        val status: String,
        val message: String,
        val data: LoginData?
    )

    data class LoginData(
        val id: Int,
        val username: String,
        val role: String
    )

    @FormUrlEncoded
    @POST("login_perangkat.php")
    fun loginPerangkat(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("get_kades_statistik.php")
    fun getKadesStatistik(): Call<KadesStatistikResponse>

    @FormUrlEncoded
    @POST("hapus_syarat.php")
    fun hapusSyarat(
        @Field("id_syarat") idSyarat: Int
    ): Call<AuthResponse>

    // ==========================================
    // KELOLA PENGAJUAN (WARGA)
    // ==========================================
    data class StatusSuratModel(
        val id_pengajuan: Int,
        val jenis_surat: String,
        val keterangan: String?,
        val status: String,
        val catatan: String?,
        val dokumen_hasil: String?
    )

    data class CekStatusResponse(
        val status: String,
        val message: String?,
        val data: List<StatusSuratModel>?
    )

    @FormUrlEncoded
    @POST("cek_status.php")
    fun cekStatusSurat(
        @Field("username") username: String
    ): Call<CekStatusResponse>

    @FormUrlEncoded
    @POST("kelola_surat.php")
    fun actionSurat(
        @Field("action") action: String,
        @Field("id_surat") idSurat: Int,
        @Field("keterangan") keterangan: String? = null
    ): Call<AuthResponse>
}
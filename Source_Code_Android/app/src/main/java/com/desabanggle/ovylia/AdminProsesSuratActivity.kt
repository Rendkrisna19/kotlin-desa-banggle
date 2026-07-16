package com.desabanggle.ovylia

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminProsesSuratActivity : AppCompatActivity(), PengajuanSuratAdapter.OnPengajuanClickListener {

    private lateinit var rvDaftarPengajuan: RecyclerView
    private lateinit var adapter: PengajuanSuratAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_proses_surat)

        rvDaftarPengajuan = findViewById(R.id.rvDaftarPengajuan)
        rvDaftarPengajuan.layoutManager = LinearLayoutManager(this)

        muatDaftarPengajuan()
    }

    private fun muatDaftarPengajuan() {
        // 🛠️ PERBAIKAN: Callback disesuaikan menjadi AdminPengajuanResponse sesuai dengan ApiService.kt
        ApiClient.instance.getSemuaPengajuan().enqueue(object : Callback<ApiService.AdminPengajuanResponse> {
            override fun onResponse(call: Call<ApiService.AdminPengajuanResponse>, response: Response<ApiService.AdminPengajuanResponse>) {
                val res = response.body()
                if (response.isSuccessful && res != null && res.status == "success") {
                    // Pastikan kelas PengajuanSuratAdapter Anda menerima List<ApiService.PengajuanSuratModel>
                    adapter = PengajuanSuratAdapter(res.data, this@AdminProsesSuratActivity)
                    rvDaftarPengajuan.adapter = adapter
                } else {
                    Toast.makeText(this@AdminProsesSuratActivity, "Gagal memuat data atau data kosong", Toast.LENGTH_SHORT).show()
                }
            }

            // 🛠️ PERBAIKAN: Fungsi duplikat/palsu PengajuanSuratAdapter yang merusak return statement SUDAH DIHAPUS

            override fun onFailure(call: Call<ApiService.AdminPengajuanResponse>, t: Throwable) {
                Toast.makeText(this@AdminProsesSuratActivity, "Gagal memuat antrean surat: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Trigger Aksi Klik tombol Periksa Berkas & Proses
    override fun onDetailKlik(pengajuan: ApiService.PengajuanSuratModel) {
        // Susun teks daftar berkas yang diunggah warga untuk ditampilkan di dialog
        val stringBuilder = StringBuilder()
        stringBuilder.append("Pemohon: ${pengajuan.nama_warga}\n")
        stringBuilder.append("Surat: ${pengajuan.jenis_surat}\n\n")
        stringBuilder.append("Daftar Dokumen yang Diunggah:\n")

        if (pengajuan.berkas_persyaratan.isEmpty()) {
            stringBuilder.append("- Tidak ada berkas diunggah\n")
        } else {
            pengajuan.berkas_persyaratan.forEach { berkas ->
                stringBuilder.append("• ${berkas.nama_syarat} (Tersedia)\n")
            }
        }

        // Tampilkan Dialog Keputusan Admin
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Verifikasi Berkas Pengajuan")
        builder.setMessage(stringBuilder.toString())

        builder.setPositiveButton("Setujui") { _, _ ->
            eksekusiUbahStatus(pengajuan.id_pengajuan, "Disetujui", "Berkas lengkap dan valid. Surat sedang dicetak.")
        }

        builder.setNegativeButton("Tolak") { _, _ ->
            tampilkanDialogAlasanTolak(pengajuan.id_pengajuan)
        }

        builder.setNeutralButton("Kembali", null)
        builder.show()
    }

    // Jika ditolak, wajib meminta input alasan penolakan agar warga tahu kekurangannya
    private fun tampilkanDialogAlasanTolak(idPengajuan: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alasan Penolakan")

        val inputCatatan = EditText(this)
        inputCatatan.hint = "Contoh: Foto KK buram / Berkas KTP salah"
        inputCatatan.setPadding(50, 40, 50, 40)
        builder.setView(inputCatatan)

        builder.setPositiveButton("Kirim Penolakan") { _, _ ->
            val alasan = inputCatatan.text.toString().trim()
            if (alasan.isNotEmpty()) {
                eksekusiUbahStatus(idPengajuan, "Ditolak", alasan)
            } else {
                Toast.makeText(this, "Alasan penolakan wajib diisi!", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Batal", null)
        builder.show()
    }

    private fun eksekusiUbahStatus(idPengajuan: Int, statusBaru: String, catatan: String) {
        ApiClient.instance.prosesStatusSurat(idPengajuan, statusBaru, catatan).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@AdminProsesSuratActivity, "Status surat berhasil diperbarui: $statusBaru", Toast.LENGTH_SHORT).show()
                    muatDaftarPengajuan() // Refresh antrean list terbaru
                }
            }
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(this@AdminProsesSuratActivity, "Gagal mengubah status", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
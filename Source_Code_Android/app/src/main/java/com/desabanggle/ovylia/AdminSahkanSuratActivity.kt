package com.desabanggle.ovylia

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class AdminSahkanSuratActivity : AppCompatActivity(), SahkanSuratAdapter.OnSahkanClickListener {

    private lateinit var rvSahkanSurat: RecyclerView
    private lateinit var adapter: SahkanSuratAdapter
    private lateinit var tvDataKosong: android.widget.TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_sahkan_surat)

        rvSahkanSurat = findViewById(R.id.rvSahkanSurat)
        tvDataKosong = findViewById(R.id.tvDataKosong)
        rvSahkanSurat.layoutManager = LinearLayoutManager(this)

        muatSuratSiapSah()
    }

    private fun muatSuratSiapSah() {
        ApiClient.instance.getSuratSiapSahkan().enqueue(object : Callback<ApiService.AdminPengajuanResponse> {
            override fun onResponse(call: Call<ApiService.AdminPengajuanResponse>, response: Response<ApiService.AdminPengajuanResponse>) {
                val res = response.body()
                if (response.isSuccessful && res != null && res.status == "success") {
                    val data = res.data
                    adapter = SahkanSuratAdapter(data, this@AdminSahkanSuratActivity)
                    rvSahkanSurat.adapter = adapter
                    
                    if (data.isEmpty()) {
                        tvDataKosong.visibility = android.view.View.VISIBLE
                        rvSahkanSurat.visibility = android.view.View.GONE
                    } else {
                        tvDataKosong.visibility = android.view.View.GONE
                        rvSahkanSurat.visibility = android.view.View.VISIBLE
                    }
                } else {
                    rvSahkanSurat.adapter = SahkanSuratAdapter(ArrayList(), this@AdminSahkanSuratActivity)
                    tvDataKosong.visibility = android.view.View.VISIBLE
                    rvSahkanSurat.visibility = android.view.View.GONE
                }
            }
            override fun onFailure(call: Call<ApiService.AdminPengajuanResponse>, t: Throwable) {
                Toast.makeText(this@AdminSahkanSuratActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                tvDataKosong.visibility = android.view.View.VISIBLE
                rvSahkanSurat.visibility = android.view.View.GONE
            }
        })
    }

    // 1. Fungsi Aksi Unduh Draf Surat menggunakan DownloadManager Android
    override fun onUnduhKlik(pengajuan: ApiService.PengajuanSuratModel) {
        val namaFile = "Draf_Surat_${pengajuan.id_pengajuan}.pdf"
        // Sesuaikan URL ini dengan alamat file cetak PDF di server Laragon Anda
        val urlUnduh = "${ApiClient.BASE_URL}cetak_surat.php?id=${pengajuan.id_pengajuan}"

        try {
            val request = DownloadManager.Request(Uri.parse(urlUnduh))
                .setTitle("Mengunduh $namaFile")
                .setDescription("Sedang mengunduh dokumen draf surat masyarakat...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, namaFile)

            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
            Toast.makeText(this, "Unduhan dimulai. Cek panel notifikasi.", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal mengunduh berkas: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // 2. Fungsi Aksi Semat TTD / Pengesahan Digital Surat
    override fun onSahkanKlik(pengajuan: ApiService.PengajuanSuratModel) {
        val inputPin = android.widget.EditText(this).apply {
            hint = "Masukkan PIN Kades (123456)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }

        AlertDialog.Builder(this)
            .setTitle("Otorisasi Surat Digital")
            .setMessage("Masukkan PIN rahasia untuk menandatangani surat milik ${pengajuan.nama_warga} secara digital:")
            .setView(inputPin)
            .setPositiveButton("Sahkan") { _, _ ->
                val pinValue = inputPin.text.toString().trim()
                if (pinValue.isNotEmpty()) {
                    kirimStatusSahkanKeServer(pengajuan.id_pengajuan, pinValue)
                } else {
                    Toast.makeText(this, "PIN tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun kirimStatusSahkanKeServer(idPengajuan: Int, token: String) {
        ApiClient.instance.sahkanSurat(idPengajuan, token).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@AdminSahkanSuratActivity, "Surat Berhasil Disahkan secara Digital!", Toast.LENGTH_SHORT).show()
                    muatSuratSiapSah() // Refresh halaman list antrean
                } else {
                    val errorMsg = response.body()?.message ?: "Gagal mengesahkan surat."
                    Toast.makeText(this@AdminSahkanSuratActivity, errorMsg, Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(this@AdminSahkanSuratActivity, "Koneksi bermasalah: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
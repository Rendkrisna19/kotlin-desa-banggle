package com.desabanggle.ovylia

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CekStatusSuratActivity : AppCompatActivity() {

    private lateinit var tvNamaSurat: TextView
    private lateinit var tvTanggal: TextView
    private lateinit var badgeStatus: TextView
    private lateinit var tvCatatanAdmin: TextView
    private lateinit var btnUnduhSurat: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cek_status_surat)

        tvNamaSurat = findViewById(R.id.tvNamaSurat)
        tvTanggal = findViewById(R.id.tvTanggal)
        badgeStatus = findViewById(R.id.badgeStatus)
        tvCatatanAdmin = findViewById(R.id.tvCatatanAdmin)
        btnUnduhSurat = findViewById(R.id.btnUnduhSurat)

        fetchStatus()
    }

    private fun fetchStatus() {
        val sharedPref = getSharedPreferences("SesiLogin", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "") ?: ""
        
        if (username.isEmpty()) return

        ApiClient.instance.cekStatusSurat(username).enqueue(object : Callback<ApiService.CekStatusResponse> {
            override fun onResponse(call: Call<ApiService.CekStatusResponse>, response: Response<ApiService.CekStatusResponse>) {
                val body = response.body()
                if (response.isSuccessful && body?.status == "success" && body.data != null && body.data.isNotEmpty()) {
                    val surat = body.data[0]
                    tvNamaSurat.text = surat.jenis_surat
                    tvTanggal.text = "ID Pengajuan: ${surat.id_pengajuan}"
                    switchStatusInterface(surat.status ?: "pending", surat.catatan ?: "-", surat.dokumen_hasil, surat.id_pengajuan, surat.jenis_surat)
                } else {
                    tvNamaSurat.text = "Belum Ada Pengajuan"
                    tvTanggal.text = "-"
                    switchStatusInterface("kosong", "", null, null,  null)
                }
            }

            override fun onFailure(call: Call<ApiService.CekStatusResponse>, t: Throwable) {
                Toast.makeText(this@CekStatusSuratActivity, "Koneksi gagal", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun switchStatusInterface(status: String, catatan: String, dokumenHasil: String?, idPengajuan: Int?, jenisSurat: String?) {
        // Default sembunyikan tombol unduh
        btnUnduhSurat.visibility = View.GONE
        
        when (status.lowercase()) {
            "kosong" -> {
                badgeStatus.text = "Kosong"
                badgeStatus.setBackgroundColor(Color.GRAY)
                tvCatatanAdmin.visibility = View.GONE
            }
            "pending", "menunggu" -> {
                badgeStatus.text = "Pending"
                badgeStatus.setBackgroundColor(Color.parseColor("#FFB300")) // Kuning Amber
                badgeStatus.setTextColor(Color.WHITE)
                tvCatatanAdmin.visibility = View.GONE
            }
            "diproses" -> {
                badgeStatus.text = "Diproses"
                badgeStatus.setBackgroundColor(Color.parseColor("#1E88E5")) // Biru
                badgeStatus.setTextColor(Color.WHITE)
                tvCatatanAdmin.visibility = View.VISIBLE
                tvCatatanAdmin.text = "Catatan: $catatan"
            }
            "ditolak" -> {
                badgeStatus.text = "Ditolak"
                badgeStatus.setBackgroundColor(Color.parseColor("#E53935")) // Merah
                badgeStatus.setTextColor(Color.WHITE)
                tvCatatanAdmin.visibility = View.VISIBLE
                tvCatatanAdmin.text = "Alasan Penolakan: $catatan"
            }
            "selesai", "disetujui" -> {
                badgeStatus.text = "Selesai"
                badgeStatus.setBackgroundColor(Color.parseColor("#43A047")) // Hijau
                badgeStatus.setTextColor(Color.WHITE)
                tvCatatanAdmin.visibility = View.VISIBLE
                tvCatatanAdmin.text = "Pesan: $catatan"
                
                if (!dokumenHasil.isNullOrEmpty()) {
                    btnUnduhSurat.visibility = View.VISIBLE
                    btnUnduhSurat.setOnClickListener {
                        // Jika URL sudah berisi "https://" = Google Drive URL langsung
                        // Jika tidak, berarti path lokal: gabungkan dengan BASE_URL
                        val url = if (dokumenHasil.startsWith("http://") || dokumenHasil.startsWith("https://")) {
                            dokumenHasil
                        } else {
                            "${ApiClient.BASE_URL}$dokumenHasil"
                        }
                        val intentBrowser = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(Intent.createChooser(intentBrowser, "Buka Dokumen"))
                        
                        // Menampilkan Form Penilaian secara otomatis setelah klik Buka Dokumen
                        val intentPenilaian = Intent(this@CekStatusSuratActivity, BeriPenilaianActivity::class.java).apply {
                            val sharedPref = getSharedPreferences("SesiLogin", Context.MODE_PRIVATE)
                            val username = sharedPref.getString("username", "") ?: ""
                            putExtra("EXTRA_USERNAME", username)
                            putExtra("EXTRA_ID_PENGAJUAN", idPengajuan ?: 0)
                            putExtra("EXTRA_JENIS_SURAT", jenisSurat)
                        }
                        startActivity(intentPenilaian)
                    }
                }
            }
            else -> {
                badgeStatus.text = status.capitalize()
                badgeStatus.setBackgroundColor(Color.GRAY)
                tvCatatanAdmin.visibility = View.VISIBLE
                tvCatatanAdmin.text = "Catatan: $catatan"
            }
        }
    }
}
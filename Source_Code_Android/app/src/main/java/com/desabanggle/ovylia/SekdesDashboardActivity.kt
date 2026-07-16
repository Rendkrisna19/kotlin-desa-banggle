package com.desabanggle.ovylia

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.view.View
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SekdesDashboardActivity : AppCompatActivity() {

    private lateinit var tvNamaSekdes: TextView
    private lateinit var menuSuratTerverifikasi: CardView
    private lateinit var menuKelolaBerita: CardView
    private lateinit var menuKelolaAspirasi: CardView
    private lateinit var menuKelolaTemplate: CardView
    private lateinit var tvBadgeNotifSekdes: TextView
    private lateinit var menuLogout: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sekdes_dashboard)

        // Inisialisasi Komponen View
        tvNamaSekdes = findViewById(R.id.tvNamaSekdes)
        menuSuratTerverifikasi = findViewById(R.id.menuSuratTerverifikasi)
        menuKelolaBerita = findViewById(R.id.menuKelolaBerita)
        menuKelolaAspirasi = findViewById(R.id.menuKelolaAspirasi)
        menuKelolaTemplate = findViewById(R.id.menuKelolaTemplate)
        tvBadgeNotifSekdes = findViewById(R.id.tvBadgeNotifSekdes)
        menuLogout = findViewById(R.id.menuLogout)

        // Set Nama Berdasarkan Sesi Login (Contoh Statis)
        tvNamaSekdes.text = "Halo, Sekretaris Desa Banggle"

        // 1. Aksi Menu Surat Terverifikasi
        menuSuratTerverifikasi.setOnClickListener {
            val intent = Intent(this, SekdesSuratVerifiedActivity::class.java)
            startActivity(intent)
        }

        // 2. Aksi Menu Kelola Pengumuman & Berita
        menuKelolaBerita.setOnClickListener {
            val intent = Intent(this, AdminKelolaBeritaActivity::class.java)
            startActivity(intent)
        }

        // 3. Aksi Menu Kelola Keluhan & Aspirasi
        menuKelolaAspirasi.setOnClickListener {
            val intent = Intent(this, AdminKelolaAspirasiActivity::class.java)
            startActivity(intent)
        }

        // Aksi Menu Kelola Template
        menuKelolaTemplate.setOnClickListener {
            val intent = Intent(this, SekdesEditTemplateActivity::class.java)
            startActivity(intent)
        }

        // 4. Aksi Menu Keluar Aplikasi (Logout)
        menuLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Keluar Sistem")
                .setMessage("Apakah Anda yakin ingin keluar dari akun Sekretaris Desa?")
                .setPositiveButton("Ya, Keluar") { _, _ ->
                    Toast.makeText(this, "Berhasil keluar", Toast.LENGTH_SHORT).show()

                    // 🛠️ PERBAIKAN: Dialihkan ke LoginActivity karena AdminLoginActivity sudah dihapus
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        updateBadgeNotifikasi()
    }

    private fun updateBadgeNotifikasi() {
        ApiClient.instance.getSuratTerverifikasi().enqueue(object : Callback<ApiService.SuratVerifiedResponse> {
            override fun onResponse(call: Call<ApiService.SuratVerifiedResponse>, response: Response<ApiService.SuratVerifiedResponse>) {
                val res = response.body()
                if (response.isSuccessful && res != null && res.status == "success") {
                    val count = res.data.size
                    if (count > 0) {
                        tvBadgeNotifSekdes.visibility = View.VISIBLE
                        tvBadgeNotifSekdes.text = count.toString()
                    } else {
                        tvBadgeNotifSekdes.visibility = View.GONE
                    }
                } else {
                    tvBadgeNotifSekdes.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<ApiService.SuratVerifiedResponse>, t: Throwable) {
                tvBadgeNotifSekdes.visibility = View.GONE
            }
        })
    }
}
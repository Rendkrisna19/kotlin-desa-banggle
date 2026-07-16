package com.desabanggle.ovylia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.view.View
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuRtRwActivity : AppCompatActivity() {

    private lateinit var tvSelamatDatangRt: TextView
    private lateinit var menuValidasiSurat: CardView
    private lateinit var menuPantauKeluhan: CardView
    private lateinit var menuRiwayatVerifikasi: CardView // 🛠️ TAMBAHAN: Deklarasi variabel baru
    private lateinit var tvBadgeNotifRtRw: TextView
    private lateinit var btnLogoutRt: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_rtrw)

        // Inisialisasi Komponen UI dari XML
        tvSelamatDatangRt = findViewById(R.id.tvSelamatDatangRt)
        menuValidasiSurat = findViewById(R.id.menuValidasiSurat)
        menuPantauKeluhan = findViewById(R.id.menuPantauKeluhan)
        menuRiwayatVerifikasi = findViewById(R.id.menuRiwayatVerifikasi) // 🛠️ TAMBAHAN: Hubungkan ke ID di XML
        tvBadgeNotifRtRw = findViewById(R.id.tvBadgeNotifRtRw)
        btnLogoutRt = findViewById(R.id.btnLogoutRt)

        // Menerima data nama akun RT/RW dari LoginActivity
        val username = intent.getStringExtra("EXTRA_USERNAME") ?: "Perangkat RT/RW"
        tvSelamatDatangRt.text = "Selamat Datang, $username!"

        // Aksi Klik Menu Validasi Surat Warga
        menuValidasiSurat.setOnClickListener {
            val intent = Intent(this, LihatPengajuanActivity::class.java).apply {
                putExtra("EXTRA_USERNAME", username)
            }
            startActivity(intent)
        }

        // Aksi Klik Menu Memantau Keluhan Warga
        menuPantauKeluhan.setOnClickListener {
            val intent = Intent(this, AdminKelolaAspirasiActivity::class.java)
            startActivity(intent)
        }

        // Aksi Klik Menu Riwayat Verifikasi Surat
        menuRiwayatVerifikasi.setOnClickListener {
            val intent = Intent(this, RiwayatVerifikasiActivity::class.java).apply {
                putExtra("EXTRA_USERNAME", username)
            }
            startActivity(intent)
        }

        // Aksi Tombol Keluar
        btnLogoutRt.setOnClickListener {
            Toast.makeText(this, "Berhasil keluar dari akun perangkat", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        updateBadgeNotifikasi()
    }

    private fun updateBadgeNotifikasi() {
        val username = intent.getStringExtra("EXTRA_USERNAME") ?: ""
        if (username.isEmpty()) return

        ApiClient.instance.getPengajuanWarga(username).enqueue(object : Callback<ApiService.PengajuanResponse> {
            override fun onResponse(call: Call<ApiService.PengajuanResponse>, response: Response<ApiService.PengajuanResponse>) {
                val res = response.body()
                if (response.isSuccessful && res != null && res.status == "success") {
                    val count = res.data.size
                    if (count > 0) {
                        tvBadgeNotifRtRw.visibility = View.VISIBLE
                        tvBadgeNotifRtRw.text = count.toString()
                    } else {
                        tvBadgeNotifRtRw.visibility = View.GONE
                    }
                } else {
                    tvBadgeNotifRtRw.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<ApiService.PengajuanResponse>, t: Throwable) {
                tvBadgeNotifRtRw.visibility = View.GONE
            }
        })
    }
}
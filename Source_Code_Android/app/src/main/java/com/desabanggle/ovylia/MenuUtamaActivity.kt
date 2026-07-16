package com.desabanggle.ovylia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.view.View
import android.content.Context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuUtamaActivity : AppCompatActivity() {

    private lateinit var tvSelamatDatang: TextView
    private lateinit var tvRoleUser: TextView
    private lateinit var tvBadgeNotif: TextView

    // Menu Utama Layanan Masyarakat
    private lateinit var menuKelolaPengajuan: CardView
    private lateinit var menuCekStatus: CardView
    private lateinit var menuKirimKeluhan: CardView
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_utama)

        // Hubungkan Variabel dengan ID XML Layout
        tvSelamatDatang = findViewById(R.id.tvSelamatDatang)
        tvRoleUser = findViewById(R.id.tvRoleUser)
        tvBadgeNotif = findViewById(R.id.tvBadgeNotif)

        menuKelolaPengajuan = findViewById(R.id.menuKelolaPengajuan)
        menuCekStatus = findViewById(R.id.menuCekStatus)
        menuKirimKeluhan = findViewById(R.id.menuKirimKeluhan)
        btnLogout = findViewById(R.id.btnLogout)

        // 1. Menerima data kiriman username dari LoginActivity saat berhasil masuk
        val username = intent.getStringExtra("EXTRA_USERNAME") ?: "Warga"

        // 2. Menampilkan teks sapaan hangat pada dashboard
        tvSelamatDatang.text = "Selamat Datang, $username!"
        tvRoleUser.text = "Peran Anda: Masyarakat Desa"

        // ==================== AKSI NAVIGASI LAYANAN WARGA ====================

        // 3. Buka Halaman Informasi & Pengajuan Surat (consolidated)
        menuKelolaPengajuan.setOnClickListener {
            val intent = Intent(this, PengajuanSuratActivity::class.java)
            startActivity(intent)
        }

        // 5. Buka Halaman Monitor/Cek Status Surat Masuk & Notifikasi
        menuCekStatus.setOnClickListener {
            val intent = Intent(this, CekStatusSuratActivity::class.java)
            startActivity(intent)
        }

        // 6. Buka Form Keluhan & Aspirasi Warga (Mengirim data parameter nama pengirim)
        menuKirimKeluhan.setOnClickListener {
            val intent = Intent(this, KirimKeluhanActivity::class.java).apply {
                putExtra("EXTRA_USERNAME", username)
            }
            startActivity(intent)
        }



        // 8. Keluar dari Aplikasi (Bersihkan Stack Halaman dan kembali ke halaman Login)
        btnLogout.setOnClickListener {
            Toast.makeText(this, "Berhasil keluar", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        updateBadgeNotif()
    }

    private fun updateBadgeNotif() {
        val sharedPref = getSharedPreferences("SesiLogin", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "") ?: ""
        
        if (username.isEmpty()) return

        ApiClient.instance.cekStatusSurat(username).enqueue(object : Callback<ApiService.CekStatusResponse> {
            override fun onResponse(call: Call<ApiService.CekStatusResponse>, response: Response<ApiService.CekStatusResponse>) {
                val body = response.body()
                if (response.isSuccessful && body?.status == "success" && body.data != null) {
                    // Hitung jumlah surat yang Selesai atau Diproses
                    val unreadCount = body.data.count { 
                        it.status?.equals("selesai", ignoreCase = true) == true || 
                        it.status?.equals("disetujui", ignoreCase = true) == true ||
                        it.status?.equals("diproses", ignoreCase = true) == true
                    }
                    
                    if (unreadCount > 0) {
                        tvBadgeNotif.visibility = View.VISIBLE
                        tvBadgeNotif.text = unreadCount.toString()
                    } else {
                        tvBadgeNotif.visibility = View.GONE
                    }
                } else {
                    tvBadgeNotif.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<ApiService.CekStatusResponse>, t: Throwable) {
                tvBadgeNotif.visibility = View.GONE
            }
        })
    }
}
package com.desabanggle.ovylia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MenuAdminActivity : AppCompatActivity() {

    private lateinit var tvSelamatDatangAdmin: TextView
    private lateinit var menuKelolaSuratTotal: CardView
    private lateinit var menuCetakSuratAdmin: CardView
    private lateinit var menuVerifikasiAkunWarga: CardView
    private lateinit var menuKelolaAspirasiAdmin: CardView
    private lateinit var btnLogoutAdmin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_admin)

        tvSelamatDatangAdmin    = findViewById(R.id.tvSelamatDatangAdmin)
        menuKelolaSuratTotal    = findViewById(R.id.menuKelolaSuratTotal)
        menuCetakSuratAdmin     = findViewById(R.id.menuCetakSuratAdmin)
        menuVerifikasiAkunWarga = findViewById(R.id.menuVerifikasiAkunWarga)
        menuKelolaAspirasiAdmin = findViewById(R.id.menuKelolaAspirasiAdmin)
        btnLogoutAdmin          = findViewById(R.id.btnLogoutAdmin)

        val emailAdmin = intent.getStringExtra("EXTRA_EMAIL") ?: "Administrator"
        tvSelamatDatangAdmin.text = "Selamat Datang,\n$emailAdmin"

        // Menu 1 — Kelola Semua Surat Masuk
        menuKelolaSuratTotal.setOnClickListener {
            startActivity(Intent(this, AdminLihatPengajuanActivity::class.java))
        }

        // Menu 2 — Cetak / Rekapitulasi Surat
        menuCetakSuratAdmin.setOnClickListener {
            startActivity(Intent(this, AdminLaporanPelayananActivity::class.java))
        }

        // Menu 3 — Verifikasi Akun Warga Baru ← WAS MISSING
        menuVerifikasiAkunWarga.setOnClickListener {
            startActivity(Intent(this, AdminVerifikasiWargaActivity::class.java))
        }

        // Menu 4 — Kelola Laporan & Aspirasi Warga ← WAS MISSING
        menuKelolaAspirasiAdmin.setOnClickListener {
            startActivity(Intent(this, AdminKelolaAspirasiActivity::class.java))
        }

        // Logout
        btnLogoutAdmin.setOnClickListener {
            Toast.makeText(this, "Keluar dari session administrator", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
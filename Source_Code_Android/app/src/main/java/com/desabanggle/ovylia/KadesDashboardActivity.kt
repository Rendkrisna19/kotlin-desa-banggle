package com.desabanggle.ovylia

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.view.View

class KadesDashboardActivity : AppCompatActivity() {

    // Inisialisasi widget data statistik
    private lateinit var tvNamaKades: TextView
    private lateinit var tvTotalSurat: TextView
    private lateinit var tvStatMenunggu: TextView
    private lateinit var tvStatDiproses: TextView
    private lateinit var tvStatSelesai: TextView
    private lateinit var tvStatDitolak: TextView
    private lateinit var tvBadgeNotifKades: TextView

    // Inisialisasi widget menu navigasi
    private lateinit var menuMonitorSurat: CardView
    private lateinit var menuMonitorBerita: CardView
    private lateinit var menuMonitorAspirasi: CardView
    private lateinit var menuLogoutKades: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kades_dashboard)

        // 1. Hubungkan variabel dengan ID komponen di XML Layout
        tvNamaKades = findViewById(R.id.tvNamaKades)
        tvTotalSurat = findViewById(R.id.tvTotalSurat)
        tvStatMenunggu = findViewById(R.id.tvStatMenunggu)
        tvStatDiproses = findViewById(R.id.tvStatDiproses)
        tvStatSelesai = findViewById(R.id.tvStatSelesai)
        tvStatDitolak = findViewById(R.id.tvStatDitolak)
        tvBadgeNotifKades = findViewById(R.id.tvBadgeNotifKades)

        menuMonitorSurat = findViewById(R.id.menuMonitorSurat)
        menuMonitorBerita = findViewById(R.id.menuMonitorBerita)
        menuMonitorAspirasi = findViewById(R.id.menuMonitorAspirasi)
        menuLogoutKades = findViewById(R.id.menuLogoutKades)

        // 2. Set teks sambutan awal
        tvNamaKades.text = "Halo, Bapak Kades Banggle"

        // 3. Konfigurasi aksi klik (Listener) untuk menu navigasi
        menuMonitorSurat.setOnClickListener {
            val intent = Intent(this, AdminSahkanSuratActivity::class.java)
            startActivity(intent)
        }

        menuMonitorBerita.setOnClickListener {
            val intent = Intent(this, AdminKelolaBeritaActivity::class.java)
            startActivity(intent)
        }

        menuMonitorAspirasi.setOnClickListener {
            val intent = Intent(this, AdminKelolaAspirasiActivity::class.java)
            startActivity(intent)
        }

        menuLogoutKades.setOnClickListener {
            konfirmasiLogout()
        }

        // 4. Jalankan pengambilan data statistik ke server Laragon
        muatStatistikPelayanan()
    }

    override fun onResume() {
        super.onResume()
        // Ambil data terbaru setiap kali halaman ini aktif kembali
        muatStatistikPelayanan()
    }

    private fun muatStatistikPelayanan() {
        ApiClient.instance.getKadesStatistik().enqueue(object : Callback<KadesStatistikResponse> {
            override fun onResponse(call: Call<KadesStatistikResponse>, response: Response<KadesStatistikResponse>) {
                val res = response.body()
                if (response.isSuccessful && res != null && res.status == "success") {
                    val data = res.data
                    if (data != null) {
                        // 🛠️ PERBAIKAN: Diubah dari total_surat menjadi totalSurat sesuai data class Kotlin
                        tvTotalSurat.text = "Total Pengajuan Surat: ${data.totalSurat}"
                        tvStatMenunggu.text = data.menunggu.toString()
                        tvStatDiproses.text = data.diproses.toString()
                        tvStatSelesai.text = data.selesai.toString()
                        tvStatDitolak.text = data.ditolak.toString()

                        // Update dynamic headers
                        findViewById<TextView>(R.id.tvTotalSuratHeader).text = data.selesai.toString()
                        findViewById<TextView>(R.id.tvTotalAspirasi).text = data.totalAspirasi.toString()

                        if (data.diproses > 0) {
                            tvBadgeNotifKades.visibility = View.VISIBLE
                            tvBadgeNotifKades.text = data.diproses.toString()
                        } else {
                            tvBadgeNotifKades.visibility = View.GONE
                        }
                    }
                } else {
                    tvBadgeNotifKades.visibility = View.GONE
                    Toast.makeText(this@KadesDashboardActivity, "Gagal mengambil statistik terbaru", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<KadesStatistikResponse>, t: Throwable) {
                tvBadgeNotifKades.visibility = View.GONE
                Toast.makeText(this@KadesDashboardActivity, "Koneksi database lokal terputus", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun konfirmasiLogout() {
        AlertDialog.Builder(this)
            .setTitle("Keluar Aplikasi")
            .setMessage("Apakah Anda yakin ingin keluar dari Panel Kades?")
            .setPositiveButton("Ya") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
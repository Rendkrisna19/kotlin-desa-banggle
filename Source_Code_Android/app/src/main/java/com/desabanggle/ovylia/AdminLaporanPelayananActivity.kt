package com.desabanggle.ovylia

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminLaporanPelayananActivity : AppCompatActivity() {

    private lateinit var tvTotalPengajuan: TextView
    private lateinit var tvRekapPending: TextView
    private lateinit var tvRekapDisetujui: TextView
    private lateinit var tvRekapDitolak: TextView
    private lateinit var tvRekapSelesai: TextView
    private lateinit var btnUnduhLaporanExcel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_laporan_pelayanan)

        tvTotalPengajuan = findViewById(R.id.tvTotalPengajuan)
        tvRekapPending = findViewById(R.id.tvRekapPending)
        tvRekapDisetujui = findViewById(R.id.tvRekapDisetujui)
        tvRekapDitolak = findViewById(R.id.tvRekapDitolak)
        tvRekapSelesai = findViewById(R.id.tvRekapSelesai)
        btnUnduhLaporanExcel = findViewById(R.id.btnUnduhLaporanExcel)

        muatDataStatistik()

        // Klik aksi untuk mengunduh berkas spreadsheet rekapitulasi data dari Laragon
        btnUnduhLaporanExcel.setOnClickListener {
            eksekusiUnduhLaporanCsv()
        }
    }

    private fun muatDataStatistik() {
        ApiClient.instance.getRekapPelayanan().enqueue(object : Callback<ApiService.RekapResponse> {
            override fun onResponse(call: Call<ApiService.RekapResponse>, response: Response<ApiService.RekapResponse>) {
                val res = response.body()
                if (response.isSuccessful && res != null && res.status == "success") {
                    val data = res.data
                    tvTotalPengajuan.text = data.total_pengajuan.toString()
                    tvRekapPending.text = data.surat_pending.toString()
                    tvRekapDisetujui.text = data.surat_disetujui.toString()
                    tvRekapDitolak.text = data.surat_ditolak.toString()
                    tvRekapSelesai.text = data.surat_selesai.toString()
                }
            }

            override fun onFailure(call: Call<ApiService.RekapResponse>, t: Throwable) {
                Toast.makeText(this@AdminLaporanPelayananActivity, "Gagal sinkronisasi data statistik", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun eksekusiUnduhLaporanCsv() {
        val namaFile = "Rekap_Pelayanan_Desa_Banggle.csv"
        val urlUnduh = "${ApiClient.BASE_URL}export_laporan_csv.php"

        try {
            val request = DownloadManager.Request(Uri.parse(urlUnduh))
                .setTitle("Mengunduh Laporan Pelayanan")
                .setDescription("Sedang mengekspor rekapitulasi surat ke penyimpanan...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, namaFile)

            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
            Toast.makeText(this, "Unduhan laporan dimulai. Periksa folder Download.", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal memproses unduhan: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
package com.desabanggle.ovylia

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailPengajuanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_pengajuan)

        val dtNamaWarga: TextView = findViewById(R.id.dtNamaWarga)
        val dtJenisSurat: TextView = findViewById(R.id.dtJenisSurat)
        val dtKeperluan: TextView = findViewById(R.id.dtKeperluan)
        val dtNamaFileKtp: TextView = findViewById(R.id.dtNamaFileKtp)
        val dtNamaFileKk: TextView = findViewById(R.id.dtNamaFileKk)
        val btnUnduhKtp: Button = findViewById(R.id.btnUnduhKtp)
        val btnUnduhKk: Button = findViewById(R.id.btnUnduhKk)
        val btnTolakSurat: Button = findViewById(R.id.btnTolakSurat)
        val btnSetujuiSurat: Button = findViewById(R.id.btnSetujuiSurat)

        val idSurat = intent.getIntExtra("EXTRA_ID", -1)
        val nama = intent.getStringExtra("EXTRA_NAMA")
        val jenis = intent.getStringExtra("EXTRA_JENIS")
        val keperluan = intent.getStringExtra("EXTRA_KEPERLUAN")
        val fileKtp = intent.getStringExtra("EXTRA_KTP")
        val fileKk = intent.getStringExtra("EXTRA_KK")

        dtNamaWarga.text = "Nama Pengaju: $nama"
        dtJenisSurat.text = "Jenis Surat: $jenis"
        dtKeperluan.text = "Keperluan: $keperluan"
        dtNamaFileKtp.text = fileKtp ?: "KTP tidak ada"
        dtNamaFileKk.text = fileKk ?: "KK tidak ada"

        btnUnduhKtp.setOnClickListener {
            if (!fileKtp.isNullOrEmpty()) {
                val urlUnduh = "${ApiClient.BASE_URL}uploads/$fileKtp"
                val intentBrowser = Intent(Intent.ACTION_VIEW, Uri.parse(urlUnduh))
                startActivity(intentBrowser)
            } else {
                Toast.makeText(this, "Dokumen KTP tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        }
        
        btnUnduhKk.setOnClickListener {
            if (!fileKk.isNullOrEmpty()) {
                val urlUnduh = "${ApiClient.BASE_URL}uploads/$fileKk"
                val intentBrowser = Intent(Intent.ACTION_VIEW, Uri.parse(urlUnduh))
                startActivity(intentBrowser)
            } else {
                Toast.makeText(this, "Dokumen KK tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        }

        // 👍 TOMBOL SETUJUI: Memunculkan Dialog Input Catatan
        btnSetujuiSurat.setOnClickListener {
            tampilkanDialogCatatan(idSurat, "Disetujui", "Tulis rekomendasi untuk Admin Desa...")
        }

        // 👎 TOMBOL TOLAK: Memunculkan Dialog Input Catatan
        btnTolakSurat.setOnClickListener {
            tampilkanDialogCatatan(idSurat, "Ditolak", "Tulis alasan penolakan berkas...")
        }
    }

    // 🛠️ FUNGSI BARU: Menampilkan Popup Pengisian Catatan Verifikasi
    private fun tampilkanDialogCatatan(idSurat: Int, statusBaru: String, petunjukInput: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Tambah Catatan Verifikasi")

        // Membuat komponen input text secara dinamis di dalam popup
        val inputCatatan = EditText(this)
        inputCatatan.hint = petunjukInput
        inputCatatan.setPadding(50, 40, 50, 40)
        builder.setView(inputCatatan)

        // Aksi jika tombol Kirim di dalam popup ditekan
        builder.setPositiveButton("Kirim Hasil") { _, _ ->
            val catatanUser = inputCatatan.text.toString().trim()
            if (catatanUser.isEmpty()) {
                Toast.makeText(this, "Catatan tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            } else {
                // Jalankan pengiriman data ke server PHP
                prosesVerifikasiKeServer(idSurat, statusBaru, catatanUser)
            }
        }

        // Aksi jika tombol Batal di dalam popup ditekan
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    // Fungsi pengiriman data ke database PHP Laragon
    private fun prosesVerifikasiKeServer(idSurat: Int, statusBaru: String, catatan: String) {
        if (idSurat == -1) {
            Toast.makeText(this, "Gagal: ID Surat tidak valid!", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.instance.prosesStatusSurat(idSurat, statusBaru, catatan).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                val res = response.body()
                if (response.isSuccessful && res != null && res.status == "success") {
                    Toast.makeText(this@DetailPengajuanActivity, "Verifikasi berhasil dikirim!", Toast.LENGTH_SHORT).show()
                    finish() // Menutup halaman dan kembali ke daftar list utama
                } else {
                    val pesanError = res?.message ?: "Gagal menyimpan verifikasi"
                    Toast.makeText(this@DetailPengajuanActivity, pesanError, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(this@DetailPengajuanActivity, "Kesalahan Jaringan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
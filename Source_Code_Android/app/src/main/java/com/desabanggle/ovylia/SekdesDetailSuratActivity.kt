package com.desabanggle.ovylia

import android.content.DialogInterface
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

class SekdesDetailSuratActivity : AppCompatActivity() {

    private lateinit var detJenisSurat: TextView
    private lateinit var detNama: TextView
    private lateinit var detNik: TextView
    private lateinit var detKeperluan: TextView
    private lateinit var statusRtRw: TextView
    private lateinit var catatanRtRw: TextView
    private lateinit var statusAdmin: TextView
    private lateinit var catatanAdmin: TextView
    private lateinit var btnSetujuiSekdes: Button
    private lateinit var btnTolakSekdes: Button
    private var idPengajuan: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sekdes_detail_surat)

        detJenisSurat = findViewById(R.id.detJenisSurat)
        detNama = findViewById(R.id.detNama)
        detNik = findViewById(R.id.detNik)
        detKeperluan = findViewById(R.id.detKeperluan)
        statusRtRw = findViewById(R.id.statusRtRw)
        catatanRtRw = findViewById(R.id.catatanRtRw)
        statusAdmin = findViewById(R.id.statusAdmin)
        catatanAdmin = findViewById(R.id.catatanAdmin)
        
        btnSetujuiSekdes = findViewById(R.id.btnSetujuiSekdes)
        btnTolakSekdes = findViewById(R.id.btnTolakSekdes)

        idPengajuan = intent.getIntExtra("id_pengajuan", 0)

        if (idPengajuan != 0) {
            muatDetailPengajuan(idPengajuan)
        }

        // 1. AKSI JIKA DISETUJUI (OTORISASI TTE)
        btnSetujuiSekdes.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Pengesahan")
                .setMessage("Apakah Anda yakin ingin menyetujui dan memberikan otorisasi pada surat ini agar bisa disahkan oleh Kades?")
                .setPositiveButton("Ya, Setujui") { _, _ ->
                    prosesPengesahanAkhir(idPengajuan, "Siap Disahkan", "Telah disetujui oleh Sekretaris Desa.")
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        // 2. AKSI JIKA DITOLAK (WAJIB MENGISI ALASAN)
        btnTolakSekdes.setOnClickListener {
            val inputCatatan = EditText(this)
            inputCatatan.hint = "Contoh: Berkas NIK tidak sinkron atau alasan lainnya..."

            AlertDialog.Builder(this)
                .setTitle("Tolak Pengajuan Surat")
                .setMessage("Masukkan alasan penolakan akhir:")
                .setView(inputCatatan)
                .setPositiveButton("Kirim Penolakan") { _, _ ->
                    val alasan = inputCatatan.text.toString().trim()
                    if (alasan.isNotEmpty()) {
                        prosesPengesahanAkhir(idPengajuan, "Ditolak Sekdes", alasan)
                    } else {
                        Toast.makeText(this, "Alasan penolakan tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    private fun prosesPengesahanAkhir(id: Int, status: String, catatan: String) {
        ApiClient.instance.updateStatusSekdes(id, status, catatan).enqueue(object : Callback<ApiService.ActionSekdesResponse> {
            override fun onResponse(call: Call<ApiService.ActionSekdesResponse>, response: Response<ApiService.ActionSekdesResponse>) {
                val res = response.body()
                if (response.isSuccessful && res != null && res.status == "success") {
                    Toast.makeText(this@SekdesDetailSuratActivity, "Otorisasi berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    finish() // Tutup halaman rincian kembali ke daftar utama sekdes
                } else {
                    Toast.makeText(this@SekdesDetailSuratActivity, res?.message ?: "Gagal memproses otorisasi", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiService.ActionSekdesResponse>, t: Throwable) {
                Toast.makeText(this@SekdesDetailSuratActivity, "Terjadi gangguan jaringan ke server", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun muatDetailPengajuan(id: Int) {
        ApiClient.instance.getDetailSuratSekdes(id).enqueue(object : Callback<ApiService.DetailSuratResponse> {
            override fun onResponse(call: Call<ApiService.DetailSuratResponse>, response: Response<ApiService.DetailSuratResponse>) {
                val res = response.body()
                if (response.isSuccessful && res != null && res.status == "success") {
                    val data = res.data
                    if (data != null) {
                        detJenisSurat.text = data.jenis_surat
                        detNama.text = data.nama_pemohon
                        detNik.text = data.nik_pemohon
                        detKeperluan.text = data.keperluan
                        
                        statusRtRw.text = data.status_rtrw
                        catatanRtRw.text = data.catatan_rtrw ?: "Tidak ada catatan"
                        
                        statusAdmin.text = data.status_admin
                        catatanAdmin.text = data.catatan_admin ?: "Tidak ada catatan"
                    }
                } else {
                    Toast.makeText(this@SekdesDetailSuratActivity, "Gagal memuat detail data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiService.DetailSuratResponse>, t: Throwable) {
                Toast.makeText(this@SekdesDetailSuratActivity, "Koneksi server gagal", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
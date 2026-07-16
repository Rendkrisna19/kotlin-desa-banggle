package com.desabanggle.ovylia

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminKelolaAspirasiActivity : AppCompatActivity(), LaporanWargaAdapter.OnLaporanClickListener {

    private lateinit var rvLaporanWarga: RecyclerView
    private lateinit var adapter: LaporanWargaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_kelola_aspirasi)

        rvLaporanWarga = findViewById(R.id.rvLaporanWarga)
        rvLaporanWarga.layoutManager = LinearLayoutManager(this)

        muatSemuaAduanWarga()
    }

    private fun muatSemuaAduanWarga() {
        ApiClient.instance.getSemuaLaporanWarga().enqueue(object : Callback<ApiService.LaporanWargaResponse> {
            override fun onResponse(call: Call<ApiService.LaporanWargaResponse>, response: Response<ApiService.LaporanWargaResponse>) {
                val res = response.body()
                if (response.isSuccessful && res != null && res.status == "success") {
                    adapter = LaporanWargaAdapter(res.data, this@AdminKelolaAspirasiActivity)
                    rvLaporanWarga.adapter = adapter
                } else {
                    rvLaporanWarga.adapter = LaporanWargaAdapter(ArrayList(), this@AdminKelolaAspirasiActivity)
                }
            }

            override fun onFailure(call: Call<ApiService.LaporanWargaResponse>, t: Throwable) {
                Toast.makeText(this@AdminKelolaAspirasiActivity, "Koneksi database bermasalah", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Aksi klik respons keluhan warga
    override fun onTindakLanjutiKlik(laporan: ApiService.LaporanWargaModel) {
        // Inflate kustom dialog box untuk penanganan aduan
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_tindak_lanjut_aspirasi, null)
        builder.setView(dialogView)

        val tvDetailAduan = dialogView.findViewById<TextView>(R.id.tvDetailAduanWarga)
        val rgStatus = dialogView.findViewById<RadioGroup>(R.id.rgStatusTindakLanjut)
        val etTanggapan = dialogView.findViewById<EditText>(R.id.etTanggapanAdmin)

        // Set data awal aduan di dialog
        tvDetailAduan.text = "Subjek: ${laporan.subjek}\nIsi: ${laporan.isi_laporan}"
        if (!laporan.tanggapan_admin.isNullOrEmpty()) {
            etTanggapan.setText(laporan.tanggapan_admin)
        }

        builder.setTitle("Form Tindak Lanjut Aduan")
        builder.setPositiveButton("Simpan Perubahan") { dialog, _ ->
            val tanggapanTeks = etTanggapan.text.toString().trim()

            // Cek pilihan radio button status baru
            val statusBaru = when (rgStatus.checkedRadioButtonId) {
                R.id.rbStatusDiproses -> "Diproses"
                R.id.rbStatusSelesai -> "Selesai"
                else -> "Pending"
            }

            if (tanggapanTeks.isNotEmpty()) {
                eksekusiSimpanTanggapan(laporan.id_laporan, statusBaru, tanggapanTeks)
            } else {
                Toast.makeText(this, "Tanggapan deskripsi wajib diisi!", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Batal", null)
        builder.show()
    }

    private fun eksekusiSimpanTanggapan(idLaporan: Int, statusBaru: String, tanggapan: String) {
        ApiClient.instance.tindakLanjutiLaporan(idLaporan, statusBaru, tanggapan).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@AdminKelolaAspirasiActivity, "Laporan berhasil ditindaklanjuti!", Toast.LENGTH_SHORT).show()
                    muatSemuaAduanWarga() // Refresh list aduan terkini
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(this@AdminKelolaAspirasiActivity, "Gagal memperbarui data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
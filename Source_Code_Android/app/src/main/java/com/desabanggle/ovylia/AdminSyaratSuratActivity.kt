package com.desabanggle.ovylia

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminSyaratSuratActivity : AppCompatActivity(), SyaratSuratAdapter.OnSyaratClickListener {

    private lateinit var tvTargetJenisSurat: TextView
    private lateinit var etInputSyaratBaru: EditText
    private lateinit var btnSimpanSyaratBaru: Button
    private lateinit var rvSyaratSurat: RecyclerView
    private lateinit var adapter: SyaratSuratAdapter

    private var idJenisSurat: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_syarat_surat)

        tvTargetJenisSurat = findViewById(R.id.tvTargetJenisSurat)
        etInputSyaratBaru = findViewById(R.id.etInputSyaratBaru)
        btnSimpanSyaratBaru = findViewById(R.id.btnSimpanSyaratBaru)
        rvSyaratSurat = findViewById(R.id.rvSyaratSurat)

        rvSyaratSurat.layoutManager = LinearLayoutManager(this)

        // Menangkap data lemparan dari halaman Jenis Surat sebelumnya
        idJenisSurat = intent.getIntExtra("EXTRA_ID_JENIS", -1)
        val namaJenisSurat = intent.getStringExtra("EXTRA_NAMA_JENIS") ?: "Tidak Diketahui"

        tvTargetJenisSurat.text = "Mengatur Syarat Untuk:\n$namaJenisSurat"

        muatDataSyarat()

        // Eksekusi Aksi Tambah Syarat
        btnSimpanSyaratBaru.setOnClickListener {
            val syaratInput = etInputSyaratBaru.text.toString().trim()
            if (syaratInput.isEmpty()) {
                etInputSyaratBaru.error = "Keterangan syarat tidak boleh kosong!"
                etInputSyaratBaru.requestFocus()
            } else {
                tambahSyaratKeServer(syaratInput)
            }
        }
    }

    private fun muatDataSyarat() {
        if (idJenisSurat == -1) return

        ApiClient.instance.getSyaratSurat(idJenisSurat).enqueue(object : Callback<ApiService.SyaratSuratResponse> {
            override fun onResponse(call: Call<ApiService.SyaratSuratResponse>, response: Response<ApiService.SyaratSuratResponse>) {
                val res = response.body()
                if (response.isSuccessful && res != null && res.status == "success") {
                    adapter = SyaratSuratAdapter(res.data, this@AdminSyaratSuratActivity)
                    rvSyaratSurat.adapter = adapter
                } else {
                    // Jika data kosong, kosongkan RecyclerView adapter
                    rvSyaratSurat.adapter = SyaratSuratAdapter(ArrayList(), this@AdminSyaratSuratActivity)
                }
            }
            override fun onFailure(call: Call<ApiService.SyaratSuratResponse>, t: Throwable) {
                Toast.makeText(this@AdminSyaratSuratActivity, "Gagal memuat syarat", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun tambahSyaratKeServer(namaSyarat: String) {
        ApiClient.instance.tambahSyaratSurat(idJenisSurat, namaSyarat).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@AdminSyaratSuratActivity, "Syarat berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                    etInputSyaratBaru.setText("") // Clear Form Input
                    muatDataSyarat() // Refresh List
                }
            }
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {}
        })
    }

    // Aksi Hapus Syarat lewat Trigger Interface Adapter
    override fun onHapusSyaratKlik(id: Int) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Syarat")
            .setMessage("Apakah Anda yakin ingin menghapus dokumen persyaratan ini?")
            .setPositiveButton("Hapus") { _, _ ->
                ApiClient.instance.hapusSyaratSurat(id).enqueue(object : Callback<AuthResponse> {
                    override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                        if (response.isSuccessful && response.body()?.status == "success") {
                            Toast.makeText(this@AdminSyaratSuratActivity, "Syarat dihapus", Toast.LENGTH_SHORT).show()
                            muatDataSyarat() // Refresh List
                        }
                    }
                    override fun onFailure(call: Call<AuthResponse>, t: Throwable) {}
                })
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
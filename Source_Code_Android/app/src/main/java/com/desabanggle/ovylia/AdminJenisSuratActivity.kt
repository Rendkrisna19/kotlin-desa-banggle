package com.desabanggle.ovylia

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminJenisSuratActivity : AppCompatActivity(), JenisSuratAdapter.OnJenisSuratClickListener {

    private lateinit var rvJenisSurat: RecyclerView
    private lateinit var fabTambahJenisSurat: FloatingActionButton
    private lateinit var adapter: JenisSuratAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_jenis_surat)

        rvJenisSurat = findViewById(R.id.rvJenisSurat)
        fabTambahJenisSurat = findViewById(R.id.fabTambahJenisSurat)
        rvJenisSurat.layoutManager = LinearLayoutManager(this)

        muatDataJenisSurat()

        // Aksi klik tombol tambah melayang (FAB)
        fabTambahJenisSurat.setOnClickListener {
            tampilkanDialogInput(null)
        }
    }

    private fun muatDataJenisSurat() {
        ApiClient.instance.getJenisSurat().enqueue(object : Callback<ApiService.JenisSuratResponse> {
            override fun onResponse(call: Call<ApiService.JenisSuratResponse>, response: Response<ApiService.JenisSuratResponse>) {
                val res = response.body()
                if (response.isSuccessful && res != null && res.status == "success") {
                    adapter = JenisSuratAdapter(res.data, this@AdminJenisSuratActivity)
                    rvJenisSurat.adapter = adapter
                }
            }
            override fun onFailure(call: Call<ApiService.JenisSuratResponse>, t: Throwable) {
                Toast.makeText(this@AdminJenisSuratActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Dialog untuk Tambah atau Edit Data
    private fun tampilkanDialogInput(jenisSurat: ApiService.JenisSuratModel?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(if (jenisSurat == null) "Tambah Jenis Surat" else "Ubah Jenis Surat")

        val input = EditText(this)
        input.setPadding(50, 40, 50, 40)
        if (jenisSurat != null) input.setText(jenisSurat.nama_surat)
        builder.setView(input)

        builder.setPositiveButton("Simpan") { _, _ ->
            val textInput = input.text.toString().trim()
            if (textInput.isNotEmpty()) {
                if (jenisSurat == null) eksekusiTambah(textInput) else eksekusiUbah(jenisSurat.id, textInput)
            } else {
                Toast.makeText(this, "Input tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun eksekusiTambah(namaSurat: String) {
        ApiClient.instance.tambahJenisSurat(namaSurat).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@AdminJenisSuratActivity, "Berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    muatDataJenisSurat()
                }
            }
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {}
        })
    }

    private fun eksekusiUbah(id: Int, namaSurat: String) {
        ApiClient.instance.ubahJenisSurat(id, namaSurat).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@AdminJenisSuratActivity, "Berhasil diubah", Toast.LENGTH_SHORT).show()
                    muatDataJenisSurat()
                }
            }
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {}
        })
    }

    // Aksi Klik Trigger Interface dari Adapter
    override fun onEditKlik(jenisSurat: ApiService.JenisSuratModel) {
        tampilkanDialogInput(jenisSurat)
    }

    override fun onHapusKlik(id: Int) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Data")
            .setMessage("Apakah Anda yakin ingin menghapus jenis surat ini?")
            .setPositiveButton("Ya") { _, _ ->
                ApiClient.instance.hapusJenisSurat(id).enqueue(object : Callback<AuthResponse> {
                    override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                        if (response.isSuccessful && response.body()?.status == "success") {
                            Toast.makeText(this@AdminJenisSuratActivity, "Berhasil dihapus", Toast.LENGTH_SHORT).show()
                            muatDataJenisSurat()
                        }
                    }
                    override fun onFailure(call: Call<AuthResponse>, t: Throwable) {}
                })
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
}
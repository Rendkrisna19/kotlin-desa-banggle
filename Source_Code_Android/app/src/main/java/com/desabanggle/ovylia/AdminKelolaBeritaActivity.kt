package com.desabanggle.ovylia

import android.os.Bundle
import android.view.LayoutInflater
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

class AdminKelolaBeritaActivity : AppCompatActivity(), KelolaBeritaAdapter.OnBeritaClickListener {

    private lateinit var rvKelolaBerita: RecyclerView
    private lateinit var fabTambahBerita: FloatingActionButton
    private var listBeritaData: List<ApiService.BeritaModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_kelola_berita)

        rvKelolaBerita = findViewById(R.id.rvKelolaBerita)
        fabTambahBerita = findViewById(R.id.fabTambahBerita)
        rvKelolaBerita.layoutManager = LinearLayoutManager(this)

        muatBeritaDesa()

        fabTambahBerita.setOnClickListener {
            tampilkanFormDialog(null) // Buka form kosong untuk input berita baru
        }
    }

    private fun muatBeritaDesa() {
        ApiClient.instance.getSemuaBerita().enqueue(object : Callback<ApiService.BeritaResponse> {
            override fun onResponse(call: Call<ApiService.BeritaResponse>, response: Response<ApiService.BeritaResponse>) {
                val res = response.body()
                if (response.isSuccessful && res != null && res.status == "success") {
                    listBeritaData = res.data
                    rvKelolaBerita.adapter = KelolaBeritaAdapter(listBeritaData, this@AdminKelolaBeritaActivity)
                }
            }
            override fun onFailure(call: Call<ApiService.BeritaResponse>, t: Throwable) {
                Toast.makeText(this@AdminKelolaBeritaActivity, "Gagal memuat berita", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Aksi Gabungan Tambah & Edit via Dialog Box
    private fun tampilkanFormDialog(berita: ApiService.BeritaModel?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_form_berita, null)
        val etJudul = dialogView.findViewById<EditText>(R.id.etJudulBeritaInput)
        val etIsi = dialogView.findViewById<EditText>(R.id.etIsiBeritaInput)

        val isEditMode = berita != null
        if (isEditMode) {
            etJudul.setText(berita?.judul)
            etIsi.setText(berita?.isi_berita)
        }

        AlertDialog.Builder(this)
            .setTitle(if (isEditMode) "Ubah Berita/Pengumuman" else "Tambah Berita Baru")
            .setView(dialogView)
            .setPositiveButton("Simpan") { d, _ ->
                val judul = etJudul.text.toString().trim()
                val isi = etIsi.text.toString().trim()

                if (judul.isNotEmpty() && isi.isNotEmpty()) {
                    if (isEditMode) {
                        eksekusiUbahBerita(berita!!.id_berita, judul, isi)
                    } else {
                        eksekusiTambahBerita(judul, isi)
                    }
                    d.dismiss()
                } else {
                    Toast.makeText(this, "Semua kolom wajib diisi!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun eksekusiTambahBerita(judul: String, isi: String) {
        ApiClient.instance.tambahBerita(judul, isi).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@AdminKelolaBeritaActivity, "Berita berhasil diterbitkan!", Toast.LENGTH_SHORT).show()
                    muatBeritaDesa()
                }
            }
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) { /**/ }
        })
    }

    override fun onEditKlik(berita: ApiService.BeritaModel) {
        tampilkanFormDialog(berita)
    }

    private fun eksekusiUbahBerita(id: Int, judul: String, isi: String) {
        ApiClient.instance.ubahBerita(id, judul, isi).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@AdminKelolaBeritaActivity, "Berita berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                    muatBeritaDesa()
                }
            }
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) { /**/ }
        })
    }

    override fun onHapusKlik(berita: ApiService.BeritaModel) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Berita")
            .setMessage("Hapus pengumuman '${berita.judul}'?")
            .setPositiveButton("Hapus") { d, _ ->
                ApiClient.instance.hapusBerita(berita.id_berita).enqueue(object : Callback<AuthResponse> {
                    override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                        if (response.isSuccessful && response.body()?.status == "success") {
                            Toast.makeText(this@AdminKelolaBeritaActivity, "Berita terhapus", Toast.LENGTH_SHORT).show()
                            muatBeritaDesa()
                        }
                    }
                    override fun onFailure(call: Call<AuthResponse>, t: Throwable) { /**/ }
                })
                d.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
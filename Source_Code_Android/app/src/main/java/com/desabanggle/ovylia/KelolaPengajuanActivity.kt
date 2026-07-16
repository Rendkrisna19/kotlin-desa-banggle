package com.desabanggle.ovylia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KelolaPengajuanActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var tvJenisSurat: TextView
    private lateinit var tvKeterangan: TextView
    private lateinit var layoutAksi: LinearLayout
    private lateinit var btnEdit: Button
    private lateinit var btnHapus: Button
    private lateinit var cardSurat: CardView

    private var currentSuratId: Int = -1
    private var currentKeterangan: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kelola_pengajuan)

        tvStatus = findViewById(R.id.tvStatus)
        tvJenisSurat = findViewById(R.id.tvJenisSurat)
        tvKeterangan = findViewById(R.id.tvKeterangan)
        layoutAksi = findViewById(R.id.layoutAksi)
        btnEdit = findViewById(R.id.btnEdit)
        btnHapus = findViewById(R.id.btnHapus)
        cardSurat = findViewById(R.id.cardSurat)

        // Default: hide everything until loaded
        cardSurat.visibility = View.GONE

        // Aksi Tombol Edit
        btnEdit.setOnClickListener {
            if (currentSuratId != -1) {
                tampilkanDialogEdit()
            }
        }

        // Aksi Tombol Hapus
        btnHapus.setOnClickListener {
            if (currentSuratId != -1) {
                tampilkanDialogHapus()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        muatDataPengajuan()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, 1, 0, "Tambah Baru")?.apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            setIcon(android.R.drawable.ic_input_add)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 1) {
            val intent = Intent(this, InformasiSuratActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun muatDataPengajuan() {
        val sharedPref = getSharedPreferences("SesiLogin", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "") ?: ""

        if (username.isEmpty()) return

        ApiClient.instance.cekStatusSurat(username).enqueue(object : Callback<ApiService.CekStatusResponse> {
            override fun onResponse(call: Call<ApiService.CekStatusResponse>, response: Response<ApiService.CekStatusResponse>) {
                val body = response.body()
                if (response.isSuccessful && body?.status == "success" && !body.data.isNullOrEmpty()) {
                    val surat = body.data[0] // Ambil surat terbaru
                    currentSuratId = surat.id_pengajuan
                    currentKeterangan = surat.keterangan ?: ""
                    
                    tvJenisSurat.text = surat.jenis_surat
                    tvKeterangan.text = "Keterangan: ${surat.keterangan}"
                    tvStatus.text = "Status: ${surat.status.replaceFirstChar { it.uppercase() }}"
                    
                    if (surat.status.lowercase() == "pending") {
                        layoutAksi.visibility = View.VISIBLE
                    } else {
                        layoutAksi.visibility = View.GONE
                    }
                    
                    cardSurat.visibility = View.VISIBLE
                } else {
                    cardSurat.visibility = View.GONE
                    Toast.makeText(this@KelolaPengajuanActivity, "Belum ada pengajuan surat", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiService.CekStatusResponse>, t: Throwable) {
                Toast.makeText(this@KelolaPengajuanActivity, "Gagal memuat data: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun tampilkanDialogEdit() {
        val input = EditText(this)
        input.setText(currentKeterangan)
        input.hint = "Keterangan pengajuan"
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 0)
            addView(input)
        }

        AlertDialog.Builder(this)
            .setTitle("Edit Keterangan Pengajuan")
            .setView(layout)
            .setPositiveButton("Simpan") { _, _ ->
                val newKet = input.text.toString().trim()
                if (newKet.isNotEmpty()) {
                    prosesAksiSurat("edit", currentSuratId, newKet)
                } else {
                    Toast.makeText(this, "Keterangan tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun tampilkanDialogHapus() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Pengajuan")
            .setMessage("Apakah Anda yakin ingin menghapus pengajuan ini secara permanen?")
            .setPositiveButton("Ya, Hapus") { _, _ ->
                prosesAksiSurat("hapus", currentSuratId, null)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun prosesAksiSurat(action: String, idSurat: Int, keterangan: String?) {
        ApiClient.instance.actionSurat(action, idSurat, keterangan).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                val res = response.body()
                if (response.isSuccessful && res?.status == "success") {
                    Toast.makeText(this@KelolaPengajuanActivity, "Pengajuan berhasil di-${if (action == "hapus") "hapus" else "edit"}", Toast.LENGTH_SHORT).show()
                    muatDataPengajuan() // Refresh data
                } else {
                    Toast.makeText(this@KelolaPengajuanActivity, "Gagal memproses aksi: ${res?.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(this@KelolaPengajuanActivity, "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
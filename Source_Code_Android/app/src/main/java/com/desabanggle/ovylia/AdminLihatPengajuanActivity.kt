package com.desabanggle.ovylia

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminLihatPengajuanActivity : AppCompatActivity() {

    private lateinit var rvAdminPengajuanSurat: RecyclerView
    private lateinit var spinnerFilterStatus: Spinner
    private lateinit var adapter: PengajuanAdapter

    // Variabel untuk menampung list data master dari server Laragon
    private var listSemuaSurat: List<ApiService.PengajuanSurat> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_lihat_pengajuan)

        rvAdminPengajuanSurat = findViewById(R.id.rvAdminPengajuanSurat)
        spinnerFilterStatus = findViewById(R.id.spinnerFilterStatus)

        // Setup LayoutManager untuk RecyclerView
        rvAdminPengajuanSurat.layoutManager = LinearLayoutManager(this)

        // Inisialisasi adapter kosong di awal agar tidak null pointer
        adapter = PengajuanAdapter(ArrayList())
        rvAdminPengajuanSurat.adapter = adapter

        // 1. Setup Data Pilihan Dropdown Spinner
        val opsiStatus = arrayOf("Semua Status", "Pending", "Disetujui RT/RW", "Ditolak RT/RW")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opsiStatus)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilterStatus.adapter = spinnerAdapter

        // 2. Ambil Seluruh Data Awal dari Server MySQL
        muatDataSuratDariServer()

        // 3. Logika Deteksi Perubahan Pilihan Filter Spinner
        spinnerFilterStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val statusTerpilih = opsiStatus[position]
                filterDataSurat(statusTerpilih)
            }

            // BAGIAN EROR SUDAH DIHAPUS (toolsNothingSelected dihapus)
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun muatDataSuratDariServer() {
        ApiClient.instance.getAllPengajuan().enqueue(object : Callback<ApiService.PengajuanResponse> {
            override fun onResponse(call: Call<ApiService.PengajuanResponse>, response: Response<ApiService.PengajuanResponse>) {
                val res = response.body()
                if (response.isSuccessful && res != null && res.status == "success") {
                    // Simpan data asli ke list master
                    listSemuaSurat = res.data

                    // Perbarui data di dalam adapter utama
                    adapter.updateData(listSemuaSurat)
                } else {
                    Toast.makeText(this@AdminLihatPengajuanActivity, "Tidak ada data pengajuan surat", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiService.PengajuanResponse>, t: Throwable) {
                Toast.makeText(this@AdminLihatPengajuanActivity, "Kesalahan Jaringan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 🛠️ FUNGSI FILTER: Menyaring data lokal tanpa tembak ulang API ke database
    private fun filterDataSurat(status: String) {
        if (listSemuaSurat.isEmpty()) return

        val listHasilFilter = if (status == "Semua Status") {
            listSemuaSurat
        } else {
            // Melakukan perbandingan String status dari database MySQL
            listSemuaSurat.filter { it.status.equals(status, ignoreCase = true) }
        }

        // Cukup perbarui list data di dalam adapter yang sama agar animasi transisi lebih halus
        adapter.updateData(listHasilFilter)

        if (listHasilFilter.isEmpty()) {
            Toast.makeText(this, "Tidak ada data dengan status: $status", Toast.LENGTH_SHORT).show()
        }
    }
}
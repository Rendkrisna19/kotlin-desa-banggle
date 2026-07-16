package com.desabanggle.ovylia

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatVerifikasiActivity : AppCompatActivity() {

    private lateinit var rvRiwayatVerifikasi: RecyclerView
    private lateinit var adapter: PengajuanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat_verifikasi)

        rvRiwayatVerifikasi = findViewById(R.id.rvRiwayatVerifikasi)
        rvRiwayatVerifikasi.layoutManager = LinearLayoutManager(this)

        // Mengambil username RT/RW yang aktif login dari intent
        val usernameRtRw = intent.getStringExtra("EXTRA_USERNAME") ?: ""

        // Memuat data riwayat dari server MySQL melalui Retrofit
        ApiClient.instance.getRiwayatVerifikasi(usernameRtRw).enqueue(object : Callback<ApiService.PengajuanResponse> {
            override fun onResponse(
                call: Call<ApiService.PengajuanResponse>,
                response: Response<ApiService.PengajuanResponse>
            ) {
                val res = response.body()
                if (response.isSuccessful && res != null && res.status == "success") {
                    // Pasang data ke adapter recycler view
                    adapter = PengajuanAdapter(res.data)
                    rvRiwayatVerifikasi.adapter = adapter
                } else {
                    Toast.makeText(this@RiwayatVerifikasiActivity, "Belum ada riwayat verifikasi surat", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiService.PengajuanResponse>, t: Throwable) {
                Toast.makeText(this@RiwayatVerifikasiActivity, "Gagal koneksi server: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
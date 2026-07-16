package com.desabanggle.ovylia

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LihatPengajuanActivity : AppCompatActivity() {

    private lateinit var rvPengajuanWarga: RecyclerView
    private lateinit var adapter: PengajuanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lihat_pengajuan)

        rvPengajuanWarga = findViewById(R.id.rvPengajuanWarga)
        rvPengajuanWarga.layoutManager = LinearLayoutManager(this)

        // Mengambil info username RT/RW yang aktif login
        val usernameRtRw = intent.getStringExtra("EXTRA_USERNAME") ?: ""

        // Memuat data dari server melalui Retrofit
        ApiClient.instance.getPengajuanWarga(usernameRtRw).enqueue(object : Callback<ApiService.PengajuanResponse> {
            // 🛠️ PERBAIKAN: Parameter kedua diubah menjadi ApiService.PengajuanResponse (Bukan ReviewResponse)
            override fun onResponse(call: Call<ApiService.PengajuanResponse>, response: Response<ApiService.PengajuanResponse>) {
                val res = response.body()
                if (response.isSuccessful && res != null && res.status == "success") {
                    adapter = PengajuanAdapter(res.data)
                    rvPengajuanWarga.adapter = adapter
                } else {
                    Toast.makeText(this@LihatPengajuanActivity, "Tidak ada data surat masuk", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiService.PengajuanResponse>, t: Throwable) {
                Toast.makeText(this@LihatPengajuanActivity, "Gagal koneksi server: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
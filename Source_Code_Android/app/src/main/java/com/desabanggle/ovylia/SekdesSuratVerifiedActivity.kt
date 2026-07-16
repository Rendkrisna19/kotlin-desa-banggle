package com.desabanggle.ovylia

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SekdesSuratVerifiedActivity : AppCompatActivity() {

    private lateinit var rvSuratVerified: RecyclerView
    private lateinit var adapter: SuratVerifiedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sekdes_surat_verified)

        rvSuratVerified = findViewById(R.id.rvSuratVerified)
        rvSuratVerified.layoutManager = LinearLayoutManager(this)

        ambilDataSuratTerverifikasi()
    }

    private fun ambilDataSuratTerverifikasi() {
        ApiClient.instance.getSuratTerverifikasi().enqueue(object : Callback<ApiService.SuratVerifiedResponse> {
            override fun onResponse(call: Call<ApiService.SuratVerifiedResponse>, response: Response<ApiService.SuratVerifiedResponse>) {
                val res = response.body()
                if (response.isSuccessful && res != null && res.status == "success") {
                    adapter = SuratVerifiedAdapter(res.data)
                    rvSuratVerified.adapter = adapter
                } else {
                    Toast.makeText(this@SekdesSuratVerifiedActivity, "Tidak ada data pengajuan baru", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiService.SuratVerifiedResponse>, t: Throwable) {
                Toast.makeText(this@SekdesSuratVerifiedActivity, "Koneksi database bermasalah", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
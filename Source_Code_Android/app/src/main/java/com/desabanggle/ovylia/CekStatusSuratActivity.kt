package com.desabanggle.ovylia

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CekStatusSuratActivity : AppCompatActivity() {

    private lateinit var rvStatusSurat: RecyclerView
    private lateinit var adapter: CekStatusSuratAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cek_status_surat)

        rvStatusSurat = findViewById(R.id.rvStatusSurat)
        rvStatusSurat.layoutManager = LinearLayoutManager(this)
        
        fetchStatus()
    }

    private fun fetchStatus() {
        val sharedPref = getSharedPreferences("SesiLogin", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "") ?: ""
        
        if (username.isEmpty()) return

        ApiClient.instance.cekStatusSurat(username).enqueue(object : Callback<ApiService.CekStatusResponse> {
            override fun onResponse(call: Call<ApiService.CekStatusResponse>, response: Response<ApiService.CekStatusResponse>) {
                val body = response.body()
                if (response.isSuccessful && body?.status == "success" && body.data != null && body.data.isNotEmpty()) {
                    adapter = CekStatusSuratAdapter(body.data, this@CekStatusSuratActivity)
                    rvStatusSurat.adapter = adapter
                } else {
                    Toast.makeText(this@CekStatusSuratActivity, "Belum Ada Pengajuan Surat", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiService.CekStatusResponse>, t: Throwable) {
                Toast.makeText(this@CekStatusSuratActivity, "Koneksi gagal", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
package com.desabanggle.ovylia

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminVerifikasiWargaActivity : AppCompatActivity(), WargaVerifikasiAdapter.OnVerifikasiClickListener {

    private lateinit var rvVerifikasiWarga: RecyclerView
    private lateinit var adapter: WargaVerifikasiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_verifikasi_warga)

        rvVerifikasiWarga = findViewById(R.id.rvVerifikasiWarga)
        rvVerifikasiWarga.layoutManager = LinearLayoutManager(this)

        muatPendaftaranWarga()
    }

    private fun muatPendaftaranWarga() {
        ApiClient.instance.getPendaftaranWarga().enqueue(object : Callback<ApiService.WargaResponse> {
            override fun onResponse(call: Call<ApiService.WargaResponse>, response: Response<ApiService.WargaResponse>) {
                val res = response.body()
                if (response.isSuccessful && res != null && res.status == "success") {
                    adapter = WargaVerifikasiAdapter(res.data, this@AdminVerifikasiWargaActivity)
                    rvVerifikasiWarga.adapter = adapter
                } else {
                    Toast.makeText(this@AdminVerifikasiWargaActivity, "Tidak ada antrean pendaftaran akun", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiService.WargaResponse>, t: Throwable) {
                Toast.makeText(this@AdminVerifikasiWargaActivity, "Koneksi Bermasalah: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Eksekusi interface ketika tombol Tolak / Setujui ditekan
    override fun onAksiKlik(username: String, statusBaru: String) {
        ApiClient.instance.verifikasiAkun(username, statusBaru).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                val res = response.body()
                if (response.isSuccessful && res != null && res.status == "success") {
                    Toast.makeText(this@AdminVerifikasiWargaActivity, "Akun $username berhasil diubah ke: $statusBaru", Toast.LENGTH_SHORT).show()
                    muatPendaftaranWarga() // Refresh list otomatis setelah verifikasi sukses
                } else {
                    Toast.makeText(this@AdminVerifikasiWargaActivity, "Gagal mengubah status akun", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(this@AdminVerifikasiWargaActivity, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
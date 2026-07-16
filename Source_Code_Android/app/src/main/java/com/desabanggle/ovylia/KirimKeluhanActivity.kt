package com.desabanggle.ovylia

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KirimKeluhanActivity : AppCompatActivity() {

    private lateinit var rgKategori: RadioGroup
    private lateinit var etSubjek: EditText
    private lateinit var etIsiLaporan: EditText
    private lateinit var btnKirimLaporan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kirim_keluhan)

        rgKategori = findViewById(R.id.rgKategori)
        etSubjek = findViewById(R.id.etSubjek)
        etIsiLaporan = findViewById(R.id.etIsiLaporan)
        btnKirimLaporan = findViewById(R.id.btnKirimLaporan)

        // Mengambil data username pengirim (bisa dipassing dari dashboard MenuUtama)
        val usernamePengirim = intent.getStringExtra("EXTRA_USERNAME") ?: "warga_anonim"

        btnKirimLaporan.setOnClickListener {
            val subjek = etSubjek.text.toString().trim()
            val isiLaporan = etIsiLaporan.text.toString().trim()

            // Mengambil teks dari RadioButton yang terpilih
            val selectedId = rgKategori.checkedRadioButtonId
            val radioButton = findViewById<RadioButton>(selectedId)
            val kategori = radioButton.text.toString()

            if (subjek.isEmpty()) {
                etSubjek.error = "Subjek tidak boleh kosong"
                etSubjek.requestFocus()
            } else if (isiLaporan.isEmpty()) {
                etIsiLaporan.error = "Isi laporan tidak boleh kosong"
                etIsiLaporan.requestFocus()
            } else {
                // Kirim data ke API Laragon menggunakan Retrofit
                ApiClient.instance.kirimLaporan(usernamePengirim, kategori, subjek, isiLaporan)
                    .enqueue(object : Callback<AuthResponse> {
                        override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                            val res = response.body()
                            if (response.isSuccessful && res != null) {
                                if (res.status == "success") {
                                    Toast.makeText(this@KirimKeluhanActivity, "Laporan Berhasil Dikirim!", Toast.LENGTH_LONG).show()
                                    finish() // Tutup halaman form dan kembali ke Menu Utama
                                } else {
                                    Toast.makeText(this@KirimKeluhanActivity, res.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                            Toast.makeText(this@KirimKeluhanActivity, "Gagal terhubung ke server: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }
    }
}
package com.desabanggle.ovylia

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etNik = findViewById<TextInputEditText>(R.id.regNik)
        val etNama = findViewById<TextInputEditText>(R.id.regNama)
        val etUsername = findViewById<TextInputEditText>(R.id.regUsername)
        val etPassword = findViewById<TextInputEditText>(R.id.regPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLogin = findViewById<TextView>(R.id.tvLoginLink)

        btnRegister.setOnClickListener {
            val nik = etNik.text.toString().trim()
            val nama = etNama.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validasi Sederhana
            if (nik.length < 16) {
                etNik.error = "NIK harus 16 digit"
            } else if (nama.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua kolom wajib diisi", Toast.LENGTH_SHORT).show()
            } else {
                prosesPendaftaran(nik, nama, username, password)
            }
        }

        tvLogin.setOnClickListener { finish() } // Kembali ke LoginActivity
    }

    private fun prosesPendaftaran(nik: String, nama: String, user: String, pass: String) {
        ApiClient.instance.registerWarga(user, pass, nama, nik).enqueue(object : Callback<ApiService.LoginResponse> {
            override fun onResponse(call: Call<ApiService.LoginResponse>, response: Response<ApiService.LoginResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@RegisterActivity, "Pendaftaran Berhasil! Silakan Login", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    val msg = response.body()?.message ?: "Gagal mendaftar"
                    Toast.makeText(this@RegisterActivity, msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiService.LoginResponse>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Koneksi Gagal: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
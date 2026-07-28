package com.desabanggle.ovylia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var tvDaftar: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inisialisasi komponen
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvDaftar = findViewById(R.id.tvDaftar)

        // Navigasi ke Halaman Daftar
        tvDaftar.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Aksi Login
        btnLogin.setOnClickListener {
            android.util.Log.d("LOGIN_TEST", "Masuk clicked! User: ${etUsername.text}, Pass: ${etPassword.text}")
            val user = etUsername.text.toString().trim()
            val pass = etPassword.text.toString().trim()

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Username dan Password wajib diisi", Toast.LENGTH_SHORT).show()
            } else {
                eksekusiLogin(user, pass)
            }
        }
    }

    private fun eksekusiLogin(u: String, p: String) {
        // Coba login sebagai Warga / Masyarakat terlebih dahulu
        ApiClient.instance.loginUser(u, p).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                val body = response.body()
                if (response.isSuccessful && body?.status == "success") {
                    simpanSesiLaluPindah(u, body.role ?: "masyarakat")
                } else if (body?.message != null && body.message.contains("menunggu", ignoreCase = true)) {
                    Toast.makeText(this@LoginActivity, body.message, Toast.LENGTH_LONG).show()
                } else {
                    // Jika gagal login Warga, coba login Perangkat Desa
                    cobaLoginPerangkat(u, p)
                }
            }
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                cobaLoginPerangkat(u, p)
            }
        })
    }

    private fun cobaLoginPerangkat(u: String, p: String) {
        ApiClient.instance.loginPerangkat(u, p).enqueue(object : Callback<ApiService.LoginResponse> {
            override fun onResponse(call: Call<ApiService.LoginResponse>, response: Response<ApiService.LoginResponse>) {
                val body = response.body()
                if (response.isSuccessful && body?.status == "success" && body.data != null) {
                    simpanSesiLaluPindah(u, body.data.role)
                } else {
                    // Jika gagal login Perangkat, coba login Admin
                    cobaLoginAdmin(u, p)
                }
            }
            override fun onFailure(call: Call<ApiService.LoginResponse>, t: Throwable) {
                cobaLoginAdmin(u, p)
            }
        })
    }

    private fun cobaLoginAdmin(u: String, p: String) {
        ApiClient.instance.loginAdmin(u, p).enqueue(object : Callback<ApiService.AdminLoginResponse> {
            override fun onResponse(call: Call<ApiService.AdminLoginResponse>, response: Response<ApiService.AdminLoginResponse>) {
                val body = response.body()
                if (response.isSuccessful && body?.status == "success" && body.data != null) {
                    simpanSesiLaluPindah(u, "admin")
                } else {
                    Toast.makeText(this@LoginActivity, "Login Gagal: Username atau Password salah", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ApiService.AdminLoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Koneksi ke server gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun simpanSesiLaluPindah(username: String, role: String) {
        // Simpan sesi login menggunakan SharedPreferences
        val sharedPref = getSharedPreferences("SesiLogin", MODE_PRIVATE)
        with (sharedPref.edit()) {
            putBoolean("isLoggedIn", true)
            putString("username", username)
            putString("role", role)
            apply()
        }

        Toast.makeText(this, "Login Berhasil sebagai $role", Toast.LENGTH_SHORT).show()

        // Routing ke dashboard masing-masing role
        val intent = when (role.lowercase()) {
            "rtrw" -> Intent(this, MenuRtRwActivity::class.java)
            "sekdes" -> Intent(this, SekdesDashboardActivity::class.java)
            "kades" -> Intent(this, KadesDashboardActivity::class.java)
            "admin" -> Intent(this, MenuAdminActivity::class.java)
            else -> Intent(this, MenuUtamaActivity::class.java)
        }
        
        intent.putExtra("EXTRA_USERNAME", username)
        intent.putExtra("EXTRA_EMAIL", username) // Untuk admin
        startActivity(intent)
        finish()
    }
}
package com.desabanggle.ovylia

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // URL API Backend (Sesuaikan dengan IP komputer yang menjalankan XAMPP/Laragon)
    // URL API Backend (10.0.2.2 digunakan khusus untuk Android Studio Emulator ke localhost)
    // Karena foldernya ada di C:\laragon\www\aplikasi-kotlin, maka URL-nya harus lengkap
    const val BASE_URL = "http://10.0.2.2/aplikasi-kotlin/api_desa/"

    val instance: ApiService by lazy {
        // Interceptor untuk melihat log request/response di Logcat
        val logging = HttpLoggingInterceptor { message ->
            android.util.Log.d("OkHttp", message)
        }
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        // Konfigurasi Client untuk menangani upload file (Timeout lebih lama)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(60, TimeUnit.SECONDS) // 60 detik untuk koneksi
            .readTimeout(60, TimeUnit.SECONDS)    // 60 detik untuk baca data
            .writeTimeout(60, TimeUnit.SECONDS)   // 60 detik untuk upload
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        retrofit.create(ApiService::class.java)
    }
}
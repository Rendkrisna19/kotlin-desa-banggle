package com.desabanggle.ovylia

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // URL API Backend Production (Hostinger Shared Hosting)
    const val BASE_URL = "https://darkcyan-dunlin-225762.hostingersite.com/"

    // Fungsi untuk mem-bypass error SSL / CertPathValidatorException
    private fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
        try {
            val trustAllCerts = arrayOf<javax.net.ssl.TrustManager>(
                object : javax.net.ssl.X509TrustManager {
                    override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                    override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                    override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
                }
            )

            val sslContext = javax.net.ssl.SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            val sslSocketFactory = sslContext.socketFactory

            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as javax.net.ssl.X509TrustManager)
            builder.hostnameVerifier { _, _ -> true }
            return builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    val instance: ApiService by lazy {
        val logging = HttpLoggingInterceptor { message ->
            android.util.Log.d("OkHttp", message)
        }
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = getUnsafeOkHttpClient()
            .addInterceptor(logging)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        retrofit.create(ApiService::class.java)
    }
}
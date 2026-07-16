package com.desabanggle.ovylia

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BeriPenilaianActivity : AppCompatActivity() {

    private lateinit var tvInstruksi: TextView
    private lateinit var spinnerLayanan: Spinner
    private lateinit var layoutRating: LinearLayout
    private lateinit var tvLayananDipilih: TextView
    private lateinit var ratingBarLayanan: RatingBar
    private lateinit var etUlasan: EditText
    private lateinit var btnKirimRating: Button

    private var daftarSuratSelesai: List<ApiService.SuratSelesaiModel> = emptyList()
    private var idPengajuanDipilih: Int? = null
    private var jenisLayananDipilih: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beri_penilaian)

        tvInstruksi    = findViewById(R.id.tvInstruksiPenilaian)
        spinnerLayanan = findViewById(R.id.spinnerLayanan)
        layoutRating   = findViewById(R.id.layoutRating)
        tvLayananDipilih = findViewById(R.id.tvLayananDipilih)
        ratingBarLayanan = findViewById(R.id.ratingBarLayanan)
        etUlasan       = findViewById(R.id.etUlasan)
        btnKirimRating = findViewById(R.id.btnKirimRating)

        val sharedPref = getSharedPreferences("SesiLogin", Context.MODE_PRIVATE)
        val username = intent.getStringExtra("EXTRA_USERNAME")
            ?: sharedPref.getString("username", "warga_anonim")
            ?: "warga_anonim"

        // Sembunyikan form rating sampai layanan dipilih (jika mode bebas)
        layoutRating.visibility = View.GONE

        // Cek apakah ada kiriman spesifik dari CekStatusSuratActivity
        idPengajuanDipilih = intent.getIntExtra("EXTRA_ID_PENGAJUAN", -1).takeIf { it != -1 }
        val intentJenis = intent.getStringExtra("EXTRA_JENIS_SURAT")

        if (idPengajuanDipilih != null && intentJenis != null) {
            jenisLayananDipilih = intentJenis
            
            // Sembunyikan instruksi & spinner, langsung tampilkan form
            tvInstruksi.visibility = View.GONE
            spinnerLayanan.visibility = View.GONE
            
            tvLayananDipilih.text = "⭐ Nilai: $jenisLayananDipilih"
            layoutRating.visibility = View.VISIBLE
            ratingBarLayanan.rating = 0f
        } else {
            // Mode Lama (fallback): Muat daftar surat yang sudah selesai
            muatDaftarSuratSelesai(username)
        }

        btnKirimRating.setOnClickListener {
            val totalRating = ratingBarLayanan.rating.toInt()
            val ulasanText = etUlasan.text.toString().trim()

            if (totalRating == 0) {
                Toast.makeText(this, "Silakan pilih bintang terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            kirimPenilaian(username, totalRating, ulasanText, jenisLayananDipilih, idPengajuanDipilih)
        }
    }

    private fun muatDaftarSuratSelesai(username: String) {
        ApiClient.instance.getSuratSelesaiWarga(username)
            .enqueue(object : Callback<ApiService.SuratSelesaiResponse> {
                override fun onResponse(
                    call: Call<ApiService.SuratSelesaiResponse>,
                    response: Response<ApiService.SuratSelesaiResponse>
                ) {
                    val body = response.body()
                    if (response.isSuccessful && body != null && body.status == "success") {
                        daftarSuratSelesai = body.data
                        setupSpinner()
                    } else {
                        // Tidak ada surat selesai → fallback ke penilaian umum
                        setupSpinnerUmum()
                    }
                }

                override fun onFailure(call: Call<ApiService.SuratSelesaiResponse>, t: Throwable) {
                    setupSpinnerUmum()
                }
            })
    }

    private fun setupSpinner() {
        val namaLayanan = daftarSuratSelesai.map { "${it.jenis_surat} (#${it.id_pengajuan})" }.toMutableList()
        namaLayanan.add(0, "— Pilih Layanan yang Ingin Dinilai —")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, namaLayanan)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLayanan.adapter = adapter

        spinnerLayanan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    layoutRating.visibility = View.GONE
                    return
                }
                val surat = daftarSuratSelesai[position - 1]
                idPengajuanDipilih = surat.id_pengajuan
                jenisLayananDipilih = surat.jenis_surat
                tvLayananDipilih.text = "⭐ Nilai: ${surat.jenis_surat}"
                layoutRating.visibility = View.VISIBLE
                ratingBarLayanan.rating = 0f
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSpinnerUmum() {
        val opsi = listOf("— Pilih Jenis Layanan —", "Surat Pindah", "Surat Tidak Mampu", "Surat Datang", "Layanan Umum Desa")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opsi)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLayanan.adapter = adapter

        spinnerLayanan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    layoutRating.visibility = View.GONE
                    return
                }
                idPengajuanDipilih = null
                jenisLayananDipilih = opsi[position]
                tvLayananDipilih.text = "⭐ Nilai: ${opsi[position]}"
                layoutRating.visibility = View.VISIBLE
                ratingBarLayanan.rating = 0f
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun kirimPenilaian(username: String, rating: Int, ulasan: String, jenis: String, idPengajuan: Int?) {
        btnKirimRating.isEnabled = false

        ApiClient.instance.kirimPenilaianLengkap(username, rating, ulasan, jenis, idPengajuan ?: 0)
            .enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    btnKirimRating.isEnabled = true
                    val res = response.body()
                    if (response.isSuccessful && res != null && res.status == "success") {
                        Toast.makeText(this@BeriPenilaianActivity, "Terima kasih atas penilaian Anda! 🌟", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@BeriPenilaianActivity, res?.message ?: "Gagal mengirim penilaian", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    btnKirimRating.isEnabled = true
                    Toast.makeText(this@BeriPenilaianActivity, "Koneksi terputus: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
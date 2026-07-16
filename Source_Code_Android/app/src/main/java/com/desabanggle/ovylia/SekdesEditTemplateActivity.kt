package com.desabanggle.ovylia

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SekdesEditTemplateActivity : AppCompatActivity() {

    private lateinit var etJenisSurat: EditText
    private lateinit var etKodeSurat: EditText
    private lateinit var etIsiPembuka: EditText
    private lateinit var etIsiPenutup: EditText
    private lateinit var btnSimpanTemplate: Button
    private var idTemplate: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sekdes_edit_template)

        etJenisSurat = findViewById(R.id.etJenisSurat)
        etKodeSurat = findViewById(R.id.etKodeSurat)
        etIsiPembuka = findViewById(R.id.etIsiPembuka)
        etIsiPenutup = findViewById(R.id.etIsiPenutup)
        btnSimpanTemplate = findViewById(R.id.btnSimpanTemplate)

        // Menerima data kiriman dari item list yang dipilih (jika mode edit)
        idTemplate = intent.getIntExtra("id_template", 0)
        etJenisSurat.setText(intent.getStringExtra("jenis_surat"))
        etKodeSurat.setText(intent.getStringExtra("kode_surat"))
        etIsiPembuka.setText(intent.getStringExtra("isi_pembuka"))
        etIsiPenutup.setText(intent.getStringExtra("isi_penutup"))

        btnSimpanTemplate.setOnClickListener {
            simpanPerubahanTemplate()
        }
    }

    private fun simpanPerubahanTemplate() {
        val jenis = etJenisSurat.text.toString().trim()
        val kode = etKodeSurat.text.toString().trim()
        val pembuka = etIsiPembuka.text.toString().trim()
        val penutup = etIsiPenutup.text.toString().trim()

        if (jenis.isEmpty() || kode.isEmpty() || pembuka.isEmpty() || penutup.isEmpty()) {
            Toast.makeText(this, "Semua kolom form wajib diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.instance.updateTemplateSurat(idTemplate, jenis, kode, pembuka, penutup)
            .enqueue(object : Callback<CRUDTemplateResponse> {
                override fun onResponse(call: Call<CRUDTemplateResponse>, response: Response<CRUDTemplateResponse>) {
                    val res = response.body()
                    if (response.isSuccessful && res != null && res.status == "success") {
                        Toast.makeText(this@SekdesEditTemplateActivity, "Template resmi berhasil dimodifikasi", Toast.LENGTH_SHORT).show()
                        finish() // Kembali ke daftar list utama kelola
                    } else {
                        Toast.makeText(this@SekdesEditTemplateActivity, "Gagal mengubah data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CRUDTemplateResponse>, t: Throwable) {
                    Toast.makeText(this@SekdesEditTemplateActivity, "Gangguan koneksi lokal", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
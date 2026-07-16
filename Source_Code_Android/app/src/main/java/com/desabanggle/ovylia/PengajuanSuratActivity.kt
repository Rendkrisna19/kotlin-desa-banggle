package com.desabanggle.ovylia

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class PengajuanSuratActivity : AppCompatActivity() {

    private lateinit var layoutPilihJenis: android.widget.LinearLayout
    private lateinit var layoutFormPengajuan: android.widget.LinearLayout
    private lateinit var tvJenisSelected: TextView
    private lateinit var btnGantiJenis: Button
    private lateinit var btnUnduhForm: Button
    private lateinit var etNamaPemohon: EditText
    private lateinit var etNoKtp: EditText

    // Dual file pickers (KTP & KK)
    private lateinit var btnUnggahKtp: Button
    private lateinit var tvStatusKtp: TextView
    private lateinit var btnUnggahKk: Button
    private lateinit var tvStatusKk: TextView

    private lateinit var btnSubmitPengajuan: Button

    private var selectedKtpUri: Uri? = null
    private var selectedKkUri: Uri? = null
    private var jenisSuratDipilih: String = ""
    private var templateFileName: String = ""

    // Peta jenis surat ke nama file template
    private val templateMap = mapOf(
        "Surat Pindah" to "form_pindah.pdf",
        "Surat Tidak Mampu" to "form_tidak_mampu.pdf",
        "Surat Datang" to "form_datang.pdf",
        "Pembuatan KIA" to "form_kia.pdf",
        "Permohonan KTP" to "form_ktp.pdf",
        "Permohonan KK" to "form_kk.pdf"
    )

    // Launcher untuk KTP
    private val ktpPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedKtpUri = result.data?.data
            selectedKtpUri?.let {
                val fileName = getFileName(it)
                tvStatusKtp.text = "✅ KTP: $fileName"
                tvStatusKtp.setTextColor(android.graphics.Color.parseColor("#2E7D32"))
            }
        }
    }

    // Launcher untuk KK
    private val kkPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedKkUri = result.data?.data
            selectedKkUri?.let {
                val fileName = getFileName(it)
                tvStatusKk.text = "✅ KK: $fileName"
                tvStatusKk.setTextColor(android.graphics.Color.parseColor("#1565C0"))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengajuan_surat)

        // Inisialisasi views
        layoutPilihJenis = findViewById(R.id.layoutPilihJenis)
        layoutFormPengajuan = findViewById(R.id.layoutFormPengajuan)
        tvJenisSelected = findViewById(R.id.tvJenisSelected)
        btnGantiJenis = findViewById(R.id.btnGantiJenis)
        btnUnduhForm = findViewById(R.id.btnUnduhForm)
        etNamaPemohon = findViewById(R.id.etNamaPemohon)
        etNoKtp = findViewById(R.id.etNoKtp)
        btnUnggahKtp = findViewById(R.id.btnUnggahKtp)
        tvStatusKtp = findViewById(R.id.tvStatusKtp)
        btnUnggahKk = findViewById(R.id.btnUnggahKk)
        tvStatusKk = findViewById(R.id.tvStatusKk)
        btnSubmitPengajuan = findViewById(R.id.btnSubmitPengajuan)

        // Kartu pemilihan jenis surat
        setupKartuJenisSurat()

        // Tombol ganti jenis
        btnGantiJenis.setOnClickListener { tampilkanPilihJenis() }

        // Tombol unduh form kosong → browser intent ke URL template
        btnUnduhForm.setOnClickListener {
            val url = "${ApiClient.BASE_URL}templates/$templateFileName"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(Intent.createChooser(intent, "Buka dengan browser"))
        }

        // Tombol unggah KTP
        btnUnggahKtp.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf", "image/*"))
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            ktpPickerLauncher.launch(intent)
        }

        // Tombol unggah KK
        btnUnggahKk.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf", "image/*"))
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            kkPickerLauncher.launch(intent)
        }

        // Tombol submit pengajuan
        btnSubmitPengajuan.setOnClickListener { submitPengajuan() }
    }

    private fun setupKartuJenisSurat() {
        val cardPindah = findViewById<CardView>(R.id.cardSuratPindah)
        val cardTidakMampu = findViewById<CardView>(R.id.cardSuratTidakMampu)
        val cardDatang = findViewById<CardView>(R.id.cardSuratDatang)
        val cardKia = findViewById<CardView>(R.id.cardSuratKia)
        val cardKtp = findViewById<CardView>(R.id.cardSuratKtp)
        val cardKk = findViewById<CardView>(R.id.cardSuratKk)

        cardPindah.setOnClickListener { pilihJenisSurat("Surat Pindah") }
        cardTidakMampu.setOnClickListener { pilihJenisSurat("Surat Tidak Mampu") }
        cardDatang.setOnClickListener { pilihJenisSurat("Surat Datang") }
        cardKia.setOnClickListener { pilihJenisSurat("Pembuatan KIA") }
        cardKtp.setOnClickListener { pilihJenisSurat("Permohonan KTP") }
        cardKk.setOnClickListener { pilihJenisSurat("Permohonan KK") }
    }

    private fun pilihJenisSurat(jenis: String) {
        jenisSuratDipilih = jenis
        templateFileName = templateMap[jenis] ?: "form_custom.pdf"
        tvJenisSelected.text = "📄 $jenis"

        // Sembunyikan pilihan, tampilkan form
        layoutPilihJenis.visibility = View.GONE
        layoutFormPengajuan.visibility = View.VISIBLE

        // Reset file selections
        selectedKtpUri = null
        selectedKkUri = null
        tvStatusKtp.text = "Belum ada file KTP dipilih"
        tvStatusKtp.setTextColor(android.graphics.Color.parseColor("#9E9E9E"))
        tvStatusKk.text = "Belum ada file KK dipilih"
        tvStatusKk.setTextColor(android.graphics.Color.parseColor("#9E9E9E"))
    }

    private fun tampilkanPilihJenis() {
        layoutPilihJenis.visibility = View.VISIBLE
        layoutFormPengajuan.visibility = View.GONE
    }

    private fun submitPengajuan() {
        val nama = etNamaPemohon.text.toString().trim()
        val noKtp = etNoKtp.text.toString().trim()

        // Validasi input
        if (nama.isEmpty()) {
            etNamaPemohon.error = "Nama lengkap wajib diisi"
            etNamaPemohon.requestFocus()
            return
        }
        if (noKtp.isEmpty() || noKtp.length < 12) {
            etNoKtp.error = "NIK minimal 12 digit"
            etNoKtp.requestFocus()
            return
        }
        if (selectedKtpUri == null) {
            Toast.makeText(this, "Silakan unggah foto KTP terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedKkUri == null) {
            Toast.makeText(this, "Silakan unggah foto Kartu Keluarga (KK) terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        val sharedPref = getSharedPreferences("SesiLogin", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "Warga") ?: "Warga"

        val fileKtp = getFileFromUri(selectedKtpUri!!)
        val fileKk  = getFileFromUri(selectedKkUri!!)

        if (fileKtp == null || fileKk == null) {
            Toast.makeText(this, "Gagal membaca salah satu file. Coba lagi.", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Mengirim pengajuan...", Toast.LENGTH_SHORT).show()
        btnSubmitPengajuan.isEnabled = false

        // Build multipart KTP
        val mimeKtp = contentResolver.getType(selectedKtpUri!!) ?: "application/octet-stream"
        val requestKtp = fileKtp.asRequestBody(mimeKtp.toMediaTypeOrNull())
        val partKtp = MultipartBody.Part.createFormData("file_ktp", fileKtp.name, requestKtp)

        // Build multipart KK
        val mimeKk = contentResolver.getType(selectedKkUri!!) ?: "application/octet-stream"
        val requestKk = fileKk.asRequestBody(mimeKk.toMediaTypeOrNull())
        val partKk = MultipartBody.Part.createFormData("file_kk", fileKk.name, requestKk)

        val actionPart    = "tambah".toRequestBody("text/plain".toMediaTypeOrNull())
        val userPart      = username.toRequestBody("text/plain".toMediaTypeOrNull())
        val jenisPart     = jenisSuratDipilih.toRequestBody("text/plain".toMediaTypeOrNull())
        val namaPart      = nama.toRequestBody("text/plain".toMediaTypeOrNull())
        val nikPart       = noKtp.toRequestBody("text/plain".toMediaTypeOrNull())
        val keperluanPart = jenisSuratDipilih.toRequestBody("text/plain".toMediaTypeOrNull())
        val keteranganPart = "Pengajuan dari Aplikasi - $jenisSuratDipilih".toRequestBody("text/plain".toMediaTypeOrNull())

        ApiClient.instance.uploadSuratDualFile(
            actionPart, userPart, jenisPart, namaPart, nikPart, keperluanPart, keteranganPart,
            partKtp, partKk
        ).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                btnSubmitPengajuan.isEnabled = true
                val res = response.body()
                if (response.isSuccessful && res != null && res.status == "success") {
                    Toast.makeText(
                        this@PengajuanSuratActivity,
                        "Pengajuan $jenisSuratDipilih berhasil dikirim! 🎉",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@PengajuanSuratActivity,
                        "Gagal mengirim: ${res?.message ?: "Error server"}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                btnSubmitPengajuan.isEnabled = true
                Toast.makeText(
                    this@PengajuanSuratActivity,
                    "Kesalahan jaringan: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val fileName = getFileName(uri)
            val file = File(cacheDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = "berkas_pengajuan"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }
}

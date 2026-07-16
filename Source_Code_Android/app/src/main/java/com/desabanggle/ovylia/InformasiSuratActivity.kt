package com.desabanggle.ovylia

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class InformasiSuratActivity : AppCompatActivity() {

    private lateinit var tvDetailSurat: TextView
    private lateinit var btnLihatSelengkapnya: Button
    private lateinit var btnUpload: Button
    private lateinit var btnHapus: Button
    private lateinit var btnKirim: Button
    private lateinit var tvFileStatus: TextView
    private var selectedFileUri: Uri? = null

    // Launcher untuk memilih file
    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            selectedFileUri = data?.data
            selectedFileUri?.let {
                val fileName = getFileName(it)
                tvFileStatus.text = "File terpilih: $fileName"
                btnHapus.visibility = View.VISIBLE // Munculkan tombol hapus
                btnKirim.visibility = View.VISIBLE // Munculkan tombol kirim
                Toast.makeText(this, "File siap diunggah", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informasi_surat)

        // Inisialisasi View
        tvDetailSurat = findViewById(R.id.tvDetailSurat)
        btnLihatSelengkapnya = findViewById(R.id.btnLihatSelengkapnya)
        btnUpload = findViewById(R.id.btnUpload)
        btnHapus = findViewById(R.id.btnHapus)
        btnKirim = findViewById(R.id.btnKirim)
        tvFileStatus = findViewById(R.id.tvFileStatus)

        // Default: Sembunyikan tombol
        btnHapus.visibility = View.GONE
        btnKirim.visibility = View.GONE

        // Logika Toggle Detail
        btnLihatSelengkapnya.setOnClickListener {
            val isVisible = tvDetailSurat.visibility == View.VISIBLE
            tvDetailSurat.visibility = if (isVisible) View.GONE else View.VISIBLE
            btnLihatSelengkapnya.text = if (isVisible) "Lihat Selengkapnya" else "Tutup Detail"
        }

        // Logika Pilih File
        btnUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "application/pdf"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            filePickerLauncher.launch(intent)
        }

        // Logika Hapus File (Reset Status)
        btnHapus.setOnClickListener {
            selectedFileUri = null
            tvFileStatus.text = "Belum ada file dipilih"
            btnHapus.visibility = View.GONE
            btnKirim.visibility = View.GONE
            Toast.makeText(this, "Pilihan file dibatalkan", Toast.LENGTH_SHORT).show()
        }

        // Logika Kirim Pengajuan
        btnKirim.setOnClickListener {
            selectedFileUri?.let { uri ->
                uploadFileKeServer(uri)
            } ?: run {
                Toast.makeText(this, "Silakan pilih file terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadFileKeServer(uri: Uri) {
        val sharedPref = getSharedPreferences("SesiLogin", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "Warga") ?: "Warga"

        val file = getFileFromUri(uri)
        if (file == null) {
            Toast.makeText(this, "Gagal membaca file", Toast.LENGTH_SHORT).show()
            return
        }

        val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file_syarat", file.name, requestFile)

        val actionPart = "tambah".toRequestBody("text/plain".toMediaTypeOrNull())
        val userPart = username.toRequestBody("text/plain".toMediaTypeOrNull())
        val jenisPart = "Surat Pengantar Umum".toRequestBody("text/plain".toMediaTypeOrNull())
        val keperluanPart = "Keperluan Administrasi".toRequestBody("text/plain".toMediaTypeOrNull())
        val keteranganPart = "Pengajuan dari Aplikasi".toRequestBody("text/plain".toMediaTypeOrNull())

        Toast.makeText(this, "Mengunggah file...", Toast.LENGTH_SHORT).show()

        ApiClient.instance.uploadSurat(
            actionPart, userPart, jenisPart, keperluanPart, keteranganPart, filePart
        ).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                val res = response.body()
                if (response.isSuccessful && res != null && res.status == "success") {
                    Toast.makeText(this@InformasiSuratActivity, "Pengajuan berhasil dikirim!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@InformasiSuratActivity, "Gagal mengirim pengajuan", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(this@InformasiSuratActivity, "Kesalahan jaringan: ${t.message}", Toast.LENGTH_LONG).show()
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

    // Fungsi untuk mendapatkan nama file
    private fun getFileName(uri: Uri): String {
        var name = "unknown.pdf"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }
}
package com.desabanggle.ovylia

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat

class NotifikasiStatusActivity : AppCompatActivity() {

    private lateinit var btnSimulasiNotif: Button
    private lateinit var tvIsiNotif: TextView

    private val CHANNEL_ID = "notif_status_surat"
    private val NOTIFICATION_ID = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifikasi_status)

        btnSimulasiNotif = findViewById(R.id.btnSimulasiNotif)
        tvIsiNotif = findViewById(R.id.tvIsiNotif)

        // Jalankan pemicu pembuatan channel notifikasi
        createNotificationChannel()

        btnSimulasiNotif.setOnClickListener {
            // Data simulasi jika status berubah di database Laragon
            val jenisSurat = "Surat Keterangan Domisili"
            val statusBaru = "DISETUJUI"
            val pesanText = "Permohonan $jenisSurat Anda telah $statusBaru oleh admin desa."

            // 1. Update teks di halaman aplikasi
            tvIsiNotif.text = pesanText

            // 2. Kirimkan notifikasi sistem ke status bar HP
            showLocalNotification(jenisSurat, statusBaru)
        }
    }

    // Fungsi membuat sistem Channel Notifikasi (Aturan Android Modern)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notifikasi Status Surat"
            val deskripsi = "Pemberitahuan perubahan status dokumen layanan desa" // ✔️ Sudah diperbaiki
            val kepentingan = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(CHANNEL_ID, name, kepentingan).apply {
                description = deskripsi
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Fungsi membangun Banner Notifikasi
    private fun showLocalNotification(jenisSurat: String, status: String) {
        // Intent agar ketika Notifikasi ditekuk/diklik, pengguna langsung diarahkan ke halaman Cek Status
        val intent = Intent(this, CekStatusSuratActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Desain blueprint notifikasi
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Ikon bawaan Android
            .setContentTitle("Perubahan Status Dokumen! 📄")
            .setContentText("Pengajuan $jenisSurat Anda kini berkategori: $status")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // Daftarkan aksi klik
            .setAutoCancel(true) // Notifikasi otomatis hilang setelah diklik

        // Tampilkan ke layar ponsel pengguna
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}
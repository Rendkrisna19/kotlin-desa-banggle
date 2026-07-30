package com.desabanggle.ovylia

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CekStatusSuratAdapter(
    private val listSurat: List<ApiService.StatusSuratModel>,
    private val context: Context
) : RecyclerView.Adapter<CekStatusSuratAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamaSurat: TextView = view.findViewById(R.id.tvNamaSurat)
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val badgeStatus: TextView = view.findViewById(R.id.badgeStatus)
        val tvCatatanAdmin: TextView = view.findViewById(R.id.tvCatatanAdmin)
        val btnUnduhSurat: Button = view.findViewById(R.id.btnUnduhSurat)
        val btnBeriPenilaian: Button = view.findViewById(R.id.btnBeriPenilaian)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_status_surat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val surat = listSurat[position]

        holder.tvNamaSurat.text = surat.jenis_surat
        holder.tvTanggal.text = "ID Pengajuan: ${surat.id_pengajuan}"
        
        switchStatusInterface(
            holder,
            surat.status ?: "pending",
            surat.catatan ?: "-",
            surat.dokumen_hasil,
            surat.id_pengajuan,
            surat.jenis_surat
        )
    }

    override fun getItemCount(): Int = listSurat.size

    private fun switchStatusInterface(
        holder: ViewHolder,
        status: String,
        catatan: String,
        dokumenHasil: String?,
        idPengajuan: Int?,
        jenisSurat: String?
    ) {
        // Default sembunyikan tombol
        holder.btnUnduhSurat.visibility = View.GONE
        holder.btnBeriPenilaian.visibility = View.GONE

        when (status.lowercase()) {
            "kosong" -> {
                holder.badgeStatus.text = "Kosong"
                holder.badgeStatus.setBackgroundColor(Color.GRAY)
                holder.tvCatatanAdmin.visibility = View.GONE
            }
            "pending", "menunggu" -> {
                holder.badgeStatus.text = "Pending"
                holder.badgeStatus.setBackgroundColor(Color.parseColor("#FFB300"))
                holder.badgeStatus.setTextColor(Color.WHITE)
                holder.tvCatatanAdmin.visibility = View.GONE
            }
            "diproses" -> {
                holder.badgeStatus.text = "Diproses"
                holder.badgeStatus.setBackgroundColor(Color.parseColor("#1E88E5"))
                holder.badgeStatus.setTextColor(Color.WHITE)
                holder.tvCatatanAdmin.visibility = View.VISIBLE
                holder.tvCatatanAdmin.text = "Catatan: $catatan"
            }
            "ditolak" -> {
                holder.badgeStatus.text = "Ditolak"
                holder.badgeStatus.setBackgroundColor(Color.parseColor("#E53935"))
                holder.badgeStatus.setTextColor(Color.WHITE)
                holder.tvCatatanAdmin.visibility = View.VISIBLE
                holder.tvCatatanAdmin.text = "Alasan Penolakan: $catatan"
            }
            "selesai", "disetujui" -> {
                holder.badgeStatus.text = "Selesai"
                holder.badgeStatus.setBackgroundColor(Color.parseColor("#43A047"))
                holder.badgeStatus.setTextColor(Color.WHITE)
                holder.tvCatatanAdmin.visibility = View.VISIBLE
                holder.tvCatatanAdmin.text = "Pesan: $catatan"

                if (!dokumenHasil.isNullOrEmpty()) {
                    holder.btnUnduhSurat.visibility = View.VISIBLE

                    holder.btnUnduhSurat.setOnClickListener {
                        val url = if (dokumenHasil.startsWith("http://") || dokumenHasil.startsWith("https://")) {
                            dokumenHasil
                        } else {
                            "${ApiClient.BASE_URL}$dokumenHasil"
                        }
                        val intentBrowser = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(Intent.createChooser(intentBrowser, "Buka Dokumen"))

                        // Tampilkan Pop-Up Ulasan
                        androidx.appcompat.app.AlertDialog.Builder(context)
                            .setTitle("Penilaian Layanan")
                            .setMessage("Dokumen sedang dibuka/diunduh. Apakah Anda bersedia memberikan ulasan untuk layanan surat ini?")
                            .setPositiveButton("Beri Ulasan") { dialog, _ ->
                                dialog.dismiss()
                                val intentPenilaian = Intent(context, BeriPenilaianActivity::class.java).apply {
                                    val sharedPref = context.getSharedPreferences("SesiLogin", Context.MODE_PRIVATE)
                                    val username = sharedPref.getString("username", "") ?: ""
                                    putExtra("EXTRA_USERNAME", username)
                                    putExtra("EXTRA_ID_PENGAJUAN", idPengajuan ?: 0)
                                    putExtra("EXTRA_JENIS_SURAT", jenisSurat)
                                }
                                context.startActivity(intentPenilaian)
                            }
                            .setNegativeButton("Nanti Saja") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                }
            }
            else -> {
                holder.badgeStatus.text = status.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                holder.badgeStatus.setBackgroundColor(Color.GRAY)
                holder.tvCatatanAdmin.visibility = View.VISIBLE
                holder.tvCatatanAdmin.text = "Catatan: $catatan"
            }
        }
    }
}

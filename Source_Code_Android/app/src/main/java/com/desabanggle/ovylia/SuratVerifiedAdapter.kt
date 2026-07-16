package com.desabanggle.ovylia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SuratVerifiedAdapter(
    private val listSurat: List<ApiService.SuratModel>
) : RecyclerView.Adapter<SuratVerifiedAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvJenisSurat: TextView = view.findViewById(R.id.tvJenisSurat)
        val tvNamaPemohon: TextView = view.findViewById(R.id.tvNamaPemohon)
        val tvNikPemohon: TextView = view.findViewById(R.id.tvNikPemohon)
        val tvTanggalMasuk: TextView = view.findViewById(R.id.tvTanggalMasuk)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_surat_terverifikasi, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val surat = listSurat[position]
        holder.tvJenisSurat.text = surat.jenis_surat
        holder.tvNamaPemohon.text = "Pemohon: ${surat.nama_pemohon}"
        holder.tvNikPemohon.text = "NIK: ${surat.nik_pemohon}"
        holder.tvTanggalMasuk.text = "Rekomendasi RT/RW: ${surat.status_rtrw} • ${surat.tanggal_pengajuan}"

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = android.content.Intent(context, SekdesDetailSuratActivity::class.java).apply {
                putExtra("id_pengajuan", surat.id_pengajuan)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listSurat.size
}
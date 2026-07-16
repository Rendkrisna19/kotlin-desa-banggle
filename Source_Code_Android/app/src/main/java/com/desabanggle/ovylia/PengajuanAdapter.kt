package com.desabanggle.ovylia

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Tipe List menggunakan ApiService.PengajuanSurat dan diubah menjadi var agar bisa difilter
class PengajuanAdapter(private var listSurat: List<ApiService.PengajuanSurat>) :
    RecyclerView.Adapter<PengajuanAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // DISESUAIKAN: ID disamakan persis dengan XML item_pengajuan_surat Anda
        val tvNamaWarga: TextView = view.findViewById(R.id.tvNamaWarga)
        val tvStatusPengajuan: TextView = view.findViewById(R.id.tvStatusPengajuan)
        val tvJenisSuratDimohon: TextView = view.findViewById(R.id.tvJenisSuratDimohon)
        val tvTglPengajuan: TextView = view.findViewById(R.id.tvTglPengajuan)
        val btnDetailPengajuan: android.widget.Button = view.findViewById(R.id.btnDetailPengajuan)
    }

    // Fungsi pembantu untuk memperbarui list data saat spinner disaring
    fun updateData(newList: List<ApiService.PengajuanSurat>) {
        this.listSurat = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pengajuan_surat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val surat = listSurat[position]

        // Memasukkan data model ke widget view yang benar
        holder.tvNamaWarga.text = "Pengaju: ${surat.username}"
        holder.tvStatusPengajuan.text = surat.status
        holder.tvJenisSuratDimohon.text = "Jenis: ${surat.jenis_surat}"
        holder.tvTglPengajuan.text = "Keperluan: ${surat.keperluan}" // Mengisi teks keperluan ke baris tanggal/keterangan

        // Aksi Klik Baris Item atau Tombol Detail untuk memproses berkas
        val clickListener = View.OnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailPengajuanActivity::class.java).apply {
                putExtra("EXTRA_NAMA", surat.username)
                putExtra("EXTRA_JENIS", surat.jenis_surat)
                putExtra("EXTRA_KEPERLUAN", surat.keperluan)
                putExtra("EXTRA_KTP", surat.file_ktp)
                putExtra("EXTRA_KK", surat.file_kk)
                putExtra("EXTRA_ID", surat.id)
            }
            context.startActivity(intent)
        }
        
        holder.itemView.setOnClickListener(clickListener)
        holder.btnDetailPengajuan.setOnClickListener(clickListener)
    }

    override fun getItemCount(): Int = listSurat.size
}
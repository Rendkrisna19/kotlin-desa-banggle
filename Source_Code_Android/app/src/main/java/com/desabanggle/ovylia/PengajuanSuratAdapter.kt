package com.desabanggle.ovylia

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PengajuanSuratAdapter(
    // 🛠️ PERBAIKAN 1: Mengubah menjadi var agar list data bisa diperbarui saat difilter spinner
    private var listPengajuan: List<ApiService.PengajuanSuratModel>,
    private val listener: OnPengajuanClickListener
) : RecyclerView.Adapter<PengajuanSuratAdapter.ViewHolder>() {

    interface OnPengajuanClickListener {
        fun onDetailKlik(pengajuan: ApiService.PengajuanSuratModel)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamaWarga: TextView = view.findViewById(R.id.tvNamaWarga)
        val tvStatusPengajuan: TextView = view.findViewById(R.id.tvStatusPengajuan)
        val tvJenisSuratDimohon: TextView = view.findViewById(R.id.tvJenisSuratDimohon)
        val tvTglPengajuan: TextView = view.findViewById(R.id.tvTglPengajuan)
        val btnDetailPengajuan: Button = view.findViewById(R.id.btnDetailPengajuan)
    }

    // 🛠️ TAMBAHAN FUNGSI: Sinkronisasi untuk fitur pencarian / filter spinner di AdminLihatPengajuanActivity
    fun updateData(newList: List<ApiService.PengajuanSuratModel>) {
        this.listPengajuan = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pengajuan_surat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = listPengajuan[position]
        holder.tvNamaWarga.text = p.nama_warga
        holder.tvJenisSuratDimohon.text = "Jenis: ${p.jenis_surat}"
        holder.tvTglPengajuan.text = "Tanggal: ${p.tanggal_pengajuan}"
        holder.tvStatusPengajuan.text = p.status

        // 🛠️ PERBAIKAN 2: Mengubah properti .textColor menjadi fungsi .setTextColor(...) yang valid
        when (p.status.lowercase()) {
            "pending" -> holder.tvStatusPengajuan.setTextColor(Color.parseColor("#FF9800"))
            "disetujui", "disetujui rt/rw" -> holder.tvStatusPengajuan.setTextColor(Color.parseColor("#4CAF50"))
            "ditolak", "ditolak rt/rw" -> holder.tvStatusPengajuan.setTextColor(Color.parseColor("#F44336"))
        }

        holder.btnDetailPengajuan.setOnClickListener { listener.onDetailKlik(p) }
    }

    override fun getItemCount(): Int = listPengajuan.size
}
package com.desabanggle.ovylia

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LaporanWargaAdapter(
    private val listLaporan: List<ApiService.LaporanWargaModel>,
    private val listener: OnLaporanClickListener
) : RecyclerView.Adapter<LaporanWargaAdapter.ViewHolder>() {

    interface OnLaporanClickListener {
        fun onTindakLanjutiKlik(laporan: ApiService.LaporanWargaModel)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSubjekLaporan: TextView = view.findViewById(R.id.tvSubjekLaporan)
        val tvStatusLaporan: TextView = view.findViewById(R.id.tvStatusLaporan)
        val tvKategoriLaporan: TextView = view.findViewById(R.id.tvKategoriLaporan)
        val tvIsiLaporanSingkat: TextView = view.findViewById(R.id.tvIsiLaporanSingkat)
        val tvTanggalLaporan: TextView = view.findViewById(R.id.tvTanggalLaporan)
        val btnTindakLanjuti: Button = view.findViewById(R.id.btnTindakLanjuti)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_laporan_warga, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val laporan = listLaporan[position]
        holder.tvSubjekLaporan.text = laporan.subjek
        holder.tvKategoriLaporan.text = "Kategori: ${laporan.kategori}"
        holder.tvIsiLaporanSingkat.text = laporan.isi_laporan
        holder.tvTanggalLaporan.text = "Oleh: ${laporan.username} • ${laporan.tanggal_kirim}"
        holder.tvStatusLaporan.text = laporan.status_tindak_lanjut

        // Set warna penanda status secara dinamis
        when (laporan.status_tindak_lanjut.lowercase()) {
            "pending" -> holder.tvStatusLaporan.setTextColor(Color.parseColor("#F44336")) // Merah Belum Diproses
            "diproses" -> holder.tvStatusLaporan.setTextColor(Color.parseColor("#FF9800")) // Oranye Sedang Ditangani
            "selesai" -> holder.tvStatusLaporan.setTextColor(Color.parseColor("#4CAF50")) // Hijau Rampung
        }

        holder.btnTindakLanjuti.setOnClickListener { listener.onTindakLanjutiKlik(laporan) }
    }

    override fun getItemCount(): Int = listLaporan.size
}
package com.desabanggle.ovylia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SahkanSuratAdapter(
    private val listSurat: List<ApiService.PengajuanSuratModel>,
    private val listener: OnSahkanClickListener
) : RecyclerView.Adapter<SahkanSuratAdapter.ViewHolder>() {

    interface OnSahkanClickListener {
        fun onUnduhKlik(pengajuan: ApiService.PengajuanSuratModel)
        fun onSahkanKlik(pengajuan: ApiService.PengajuanSuratModel)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamaPemohonSah: TextView = view.findViewById(R.id.tvNamaPemohonSah)
        val tvJenisSuratSah: TextView = view.findViewById(R.id.tvJenisSuratSah)
        val btnUnduhDraf: Button = view.findViewById(R.id.btnUnduhDraf)
        val btnSahkanDigital: Button = view.findViewById(R.id.btnSahkanDigital)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sahkan_surat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val surat = listSurat[position]
        holder.tvNamaPemohonSah.text = surat.nama_warga
        holder.tvJenisSuratSah.text = "Surat: ${surat.jenis_surat}"

        holder.btnUnduhDraf.setOnClickListener { listener.onUnduhKlik(surat) }
        holder.btnSahkanDigital.setOnClickListener { listener.onSahkanKlik(surat) }
    }

    override fun getItemCount(): Int = listSurat.size
}
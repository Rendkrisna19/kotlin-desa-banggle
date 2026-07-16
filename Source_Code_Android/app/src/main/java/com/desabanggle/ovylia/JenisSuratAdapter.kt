package com.desabanggle.ovylia

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JenisSuratAdapter(
    private val listJenis: List<ApiService.JenisSuratModel>,
    private val listener: OnJenisSuratClickListener
) : RecyclerView.Adapter<JenisSuratAdapter.ViewHolder>() {

    interface OnJenisSuratClickListener {
        fun onEditKlik(jenisSurat: ApiService.JenisSuratModel)
        fun onHapusKlik(id: Int)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamaJenisSurat: TextView = view.findViewById(R.id.tvNamaJenisSurat)
        val btnEditJenis: ImageButton = view.findViewById(R.id.btnEditJenis)
        val btnHapusJenis: ImageButton = view.findViewById(R.id.btnHapusJenis)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_jenis_surat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jenis = listJenis[position]
        holder.tvNamaJenisSurat.text = jenis.nama_surat

        // Aksi Klik Tombol Edit & Hapus
        holder.btnEditJenis.setOnClickListener { listener.onEditKlik(jenis) }
        holder.btnHapusJenis.setOnClickListener { listener.onHapusKlik(jenis.id) }

        // ⚡ TAMBAHAN BARU: Jika nama surat diklik, pindah ke halaman kelola syarat dokumen
        holder.tvNamaJenisSurat.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, AdminSyaratSuratActivity::class.java).apply {
                putExtra("EXTRA_ID_JENIS", jenis.id)
                putExtra("EXTRA_NAMA_JENIS", jenis.nama_surat)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listJenis.size
}
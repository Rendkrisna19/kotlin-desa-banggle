package com.desabanggle.ovylia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class KelolaBeritaAdapter(
    private val listBerita: List<ApiService.BeritaModel>,
    private val listener: OnBeritaClickListener
) : RecyclerView.Adapter<KelolaBeritaAdapter.ViewHolder>() {

    interface OnBeritaClickListener {
        fun onEditKlik(berita: ApiService.BeritaModel)
        fun onHapusKlik(berita: ApiService.BeritaModel)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvJudul: TextView = view.findViewById(R.id.tvJudulBerita)
        val tvTgl: TextView = view.findViewById(R.id.tvTglBerita)
        val tvIsi: TextView = view.findViewById(R.id.tvIsiBeritaSingkat)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEditBerita)
        val btnHapus: ImageButton = view.findViewById(R.id.btnHapusBerita)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_kelola_berita, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val berita = listBerita[position]
        holder.tvJudul.text = berita.judul
        holder.tvTgl.text = "Rilis: ${berita.tanggal_post}"
        holder.tvIsi.text = berita.isi_berita

        holder.btnEdit.setOnClickListener { listener.onEditKlik(berita) }
        holder.btnHapus.setOnClickListener { listener.onHapusKlik(berita) }
    }

    override fun getItemCount(): Int = listBerita.size
}
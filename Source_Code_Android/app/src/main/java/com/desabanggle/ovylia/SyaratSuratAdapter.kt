package com.desabanggle.ovylia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SyaratSuratAdapter(
    private val listSyarat: List<ApiService.SyaratSuratModel>,
    private val listener: OnSyaratClickListener
) : RecyclerView.Adapter<SyaratSuratAdapter.ViewHolder>() {

    interface OnSyaratClickListener {
        fun onHapusSyaratKlik(id: Int)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamaSyarat: TextView = view.findViewById(R.id.tvNamaSyarat)
        val btnHapusSyarat: ImageButton = view.findViewById(R.id.btnHapusSyarat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_syarat_surat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val syarat = listSyarat[position]
        holder.tvNamaSyarat.text = "• ${syarat.nama_syarat}"
        holder.btnHapusSyarat.setOnClickListener { listener.onHapusSyaratKlik(syarat.id) }
    }

    override fun getItemCount(): Int = listSyarat.size
}
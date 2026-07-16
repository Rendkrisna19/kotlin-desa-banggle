package com.desabanggle.ovylia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WargaVerifikasiAdapter(
    private val listWarga: List<ApiService.WargaModel>,
    private val listener: OnVerifikasiClickListener
) : RecyclerView.Adapter<WargaVerifikasiAdapter.ViewHolder>() {

    interface OnVerifikasiClickListener {
        fun onAksiKlik(username: String, statusBaru: String)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUsernameWarga: TextView = view.findViewById(R.id.tvUsernameWarga)
        val tvStatusAkun: TextView = view.findViewById(R.id.tvStatusAkun)
        val btnTolakAkun: Button = view.findViewById(R.id.btnTolakAkun)
        val btnSetujuiAkun: Button = view.findViewById(R.id.btnSetujuiAkun)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_verifikasi_akun, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val warga = listWarga[position]
        holder.tvUsernameWarga.text = "Username: ${warga.username}"
        holder.tvStatusAkun.text = "Status: ${warga.status_akun}"

        holder.btnSetujuiAkun.setOnClickListener {
            listener.onAksiKlik(warga.username, "Aktif")
        }

        holder.btnTolakAkun.setOnClickListener {
            listener.onAksiKlik(warga.username, "Ditolak")
        }
    }

    override fun getItemCount(): Int = listWarga.size
}
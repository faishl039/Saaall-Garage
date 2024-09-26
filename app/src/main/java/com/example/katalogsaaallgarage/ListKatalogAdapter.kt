package com.example.katalogsaaallgarage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListKatalogAdapter(private val listKatalog: ArrayList<Katalog>, private val listener: OnItemClickListener): RecyclerView.Adapter<ListKatalogAdapter.ListViewHolder>() {
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fotoBarang: ImageView = itemView.findViewById(R.id.foto_katalog)
        val namaBarang: TextView = itemView.findViewById(R.id.nama_katalog)
        val descBarang: TextView = itemView.findViewById(R.id.desc_katalog)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_katalog, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = listKatalog.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (nama, desc, foto) = listKatalog[position]
        holder.fotoBarang.setImageResource(foto)
        holder.namaBarang.text = nama
        holder.descBarang.text = desc

        holder.itemView.setOnClickListener {
            listener.onItemClick(listKatalog[position])
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: Katalog)
    }
}
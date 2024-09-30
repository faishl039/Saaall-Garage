package com.example.katalogsaaallgarage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.katalogsaaallgarage.databinding.ItemKatalogBinding
import com.example.katalogsaaallgarage.room.Barang

class KatalogAdapter(
    private val listKatalog: ArrayList<Barang>,
    private val listener: OnAdapterListener
) : RecyclerView.Adapter<KatalogAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemKatalogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount() = listKatalog.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val katalog = listKatalog[position]
        holder.bind(katalog, listener)
    }

    class ListViewHolder(private val binding: ItemKatalogBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(barang: Barang, listener: OnAdapterListener) {
            binding.textTitle.text = barang.title
            binding.textTitle.setOnClickListener {
                listener.onRead(barang)
            }
            binding.iconEdit.setOnClickListener {
                listener.onUpdate(barang)
            }
            binding.iconDelete.setOnClickListener {
                listener.onDelete(barang)
            }
        }
    }

    interface OnAdapterListener {
        fun onRead(barang: Barang)
        fun onUpdate(barang: Barang)
        fun onDelete(barang: Barang)
    }

    fun setData(list: List<Barang>) {
        listKatalog.clear()
        listKatalog.addAll(list)
        notifyDataSetChanged()
    }

}
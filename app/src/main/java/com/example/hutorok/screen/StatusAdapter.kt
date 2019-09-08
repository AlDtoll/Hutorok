package com.example.hutorok.screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hutorok.R
import com.example.hutorok.domain.model.Status
import kotlinx.android.synthetic.main.status_item.view.*

class StatusAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = ArrayList<Status>()
        set(data) {
            field = data
            notifyDataSetChanged()
        }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder =
        StatusHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.status_item, parent, false)
        )


    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.run {
            name.text = item.name
            description.text = item.description
        }
    }

    class StatusHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
package com.example.hutorok.screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hutorok.R
import com.example.hutorok.domain.model.Select
import kotlinx.android.synthetic.main.task_item.view.*

class SelectAdapter(
    private val callback: Callback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = ArrayList<Select>()
        set(data) {
            field = data
            notifyDataSetChanged()
        }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder =
        SelectHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.task_item, parent, false)
        )


    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.run {
            this.setOnClickListener {
                callback.clickSelect(item)
            }
            name.text = item.task.name
            description.text = item.task.description
            container.setCardBackgroundColor(resources.getColor(R.color.light_green_background_color))
        }
    }

    class SelectHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface Callback {

        fun clickSelect(select: Select)
    }
}
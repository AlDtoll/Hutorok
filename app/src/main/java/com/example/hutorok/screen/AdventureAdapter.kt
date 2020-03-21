package com.example.hutorok.screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hutorok.R
import com.example.hutorok.domain.model.Adventure
import kotlinx.android.synthetic.main.task_item.view.*

class AdventureAdapter(
    private val callback: Callback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = ArrayList<Adventure>()
        set(data) {
            field = data
            notifyDataSetChanged()
        }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder =
        QuestHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.task_item, parent, false)
        )


    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.run {
            this.setOnClickListener {
                callback.clickAdventure(item)
            }
            name.text = item.name
            container.setCardBackgroundColor(resources.getColor(R.color.light_green_background_color))
        }
    }

    class QuestHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface Callback {

        fun clickAdventure(adventure: Adventure)
    }
}
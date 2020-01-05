package com.example.hutorok.screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hutorok.R
import com.example.hutorok.domain.model.Task
import com.example.hutorok.ext.onClick
import kotlinx.android.synthetic.main.task_item.view.*

class TaskAdapter(
    private val callback: Callback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = ArrayList<Task>()
        set(data) {
            field = data
            notifyDataSetChanged()
        }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder =
        WorkerHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.task_item, parent, false)
        )


    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.run {
            this.onClick {
                callback.selectTask(item)
            }
            name.text = item.name
            description.text = item.description
            container.setCardBackgroundColor(resources.getColor(selectColor(item)))
        }
    }

    private fun selectColor(item: Task): Int {
        return when (item.type) {
            Task.Type.WORK -> R.color.light_green_background_color
            Task.Type.BUILD -> R.color.green_background_color
            Task.Type.PERSONAL_JOB -> R.color.light_blue_background_color
            Task.Type.PERSON -> R.color.blue_background_color
        }
    }

    class WorkerHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface Callback {
        fun selectTask(task: Task)
    }
}
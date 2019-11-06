package com.example.hutorok.screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hutorok.R
import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.ext.onClick
import kotlinx.android.synthetic.main.worker_item.view.*

class WorkerAdapter(
    private val callback: Callback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = ArrayList<Worker>()
        set(data) {
            field = data
            notifyDataSetChanged()
        }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    var isOrder = false
    var importantStatusNames = emptyList<String>()
    lateinit var task: Task

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder =
        WorkerHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.worker_item, parent, false)
        )


    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.run {
            this.onClick {
                callback.selectWorker(item)
            }
            name.text = item.name
            nickname.text = item.nickname
            val workersAge = "Возраст: " + item.age.code
            age.text = workersAge
            if (isCheckboxVisible(item)) {
                checkbox.visibility = View.VISIBLE
            } else {
                checkbox.visibility = View.GONE
            }
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                item.isSelected = isChecked
                callback.isExecuteTaskButtonEnable(isExecuteTaskButtonEnable())
            }
            var importantStatusesText = ""
            item.statuses.forEach { workerStatuses ->
                importantStatusNames.forEach {
                    if (it == workerStatuses.name) {
                        importantStatusesText = "$it; $importantStatusesText"
                    }
                }
            }
            importantStatuses.text = importantStatusesText
        }
    }

    private fun isExecuteTaskButtonEnable(): Boolean {
        if (task.type == Task.Type.BUILD) {
            return true
        }
        return if (task.type == Task.Type.PERSON) {
            val filterWorkers = items.filter { worker -> worker.isSelected }
            filterWorkers.size == 1
        } else {
            items.any { worker -> worker.isSelected }
        }
    }

    private fun isCheckboxVisible(worker: Worker): Boolean {
        if (isOrder && task.type != Task.Type.BUILD && isConditionComplete(worker.statuses)) {
            return true
        }
        return false
    }

    private fun isConditionComplete(statusesList: List<Status>): Boolean {
        if (task.enableCondition.isEmpty()) {
            return true
        }
        task.enableCondition.forEach { condition ->
            val find = statusesList.find { condition.first == it.code }
            val findValue = find?.value ?: 0.0
            when (condition.second) {
                Task.Symbol.MORE -> {
                    if (findValue <= condition.third) {
                        return false
                    }
                }
                Task.Symbol.LESS -> {
                    if (findValue >= condition.third) {
                        return false
                    }
                }
            }
        }
        return true
    }

    class WorkerHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface Callback {
        fun selectWorker(worker: Worker)

        fun isExecuteTaskButtonEnable(isEnable: Boolean)
    }
}



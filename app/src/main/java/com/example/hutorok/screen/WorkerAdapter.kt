package com.example.hutorok.screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hutorok.R
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
    var generalDisableConditions = emptyList<Triple<String, Task.Symbol, Double>>()
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
            if (isOrder) {
                this.setOnLongClickListener {
                    callback.selectWorker(item)
                    true
                }
            } else {
                this.onClick {
                    callback.selectWorker(item)
                }
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
            //todo crunch
            checkbox.setOnCheckedChangeListener(null)
            checkbox.isChecked = item.isSelected
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                notifyDataSetChanged()
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
        if (task.type == Task.Type.MASTER_SLAVE_JOB) {
            val filterWorkers = items.filter { worker -> worker.isSelected }
            return filterWorkers.size == 2
        }
        return if (task.type == Task.Type.PERSON || task.type == Task.Type.PERSONAL_JOB) {
            val filterWorkers = items.filter { worker -> worker.isSelected }
            filterWorkers.size == 1
        } else {
            items.any { worker -> worker.isSelected }
        }
    }

    private fun isCheckboxVisible(worker: Worker): Boolean {
        if (worker.isSelected) {
            return true
        }
        if (isOrder && task.type != Task.Type.BUILD
            && isTaskEnableConditionsInComplete(worker)
            && !Task.conditionsIsComplete(generalDisableConditions, worker.statuses)
            && !isExceededLimitOfSelectedWorker()
        ) {
            return true
        }
        return false
    }

    private fun isExceededLimitOfSelectedWorker(): Boolean {
        return when (task.type) {
            Task.Type.WORK -> false
            Task.Type.BUILD -> true
            Task.Type.PERSON -> items.filter { worker -> worker.isSelected }.size == 1
            Task.Type.PERSONAL_JOB -> items.filter { worker -> worker.isSelected }.size == 1
            Task.Type.MASTER_SLAVE_JOB -> items.filter { worker -> worker.isSelected }.size == 2
        }
    }

    private fun isTaskEnableConditionsInComplete(worker: Worker): Boolean {
        return if (task.type == Task.Type.MASTER_SLAVE_JOB) {
            val masterEnableConditions = mutableListOf<Triple<String, Task.Symbol, Double>>()
            task.enableConditions.filter { condition -> condition.first.contains("MASTER_") }
                .forEach {
                    masterEnableConditions.add(
                        Triple(
                            it.first.replace("MASTER_", ""),
                            it.second,
                            it.third
                        )
                    )
                }

            val slaveEnableConditions = mutableListOf<Triple<String, Task.Symbol, Double>>()
            task.enableConditions.filter { condition -> condition.first.contains("SLAVE_") }
                .forEach {
                    slaveEnableConditions.add(
                        Triple(
                            it.first.replace("SLAVE_", ""),
                            it.second,
                            it.third
                        )
                    )
                }

            return if (items.none { worker -> worker.isSelected }) {
                Task.conditionsIsComplete(masterEnableConditions, worker.statuses)
            } else {
                Task.conditionsIsComplete(slaveEnableConditions, worker.statuses)
            }
        } else {
            Task.conditionsIsComplete(task.enableConditions, worker.statuses)
        }
    }

    class WorkerHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface Callback {
        fun selectWorker(worker: Worker)

        fun isExecuteTaskButtonEnable(isEnable: Boolean)
    }
}



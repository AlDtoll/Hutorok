package com.example.hutorok.screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.hutorok.R
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.ext.onClick
import kotlinx.android.synthetic.main.worker_item.view.*

class WorkerAdapter(
    private val callback: Callback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        var MASTER_ALREADY_SELECTED = false
    }

    var items = ArrayList<Worker>()
        set(data) {
            field = data
            notifyDataSetChanged()
        }

    fun clear() {
        MASTER_ALREADY_SELECTED = false
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
                    callback.clickWorker(item)
                    true
                }
            } else {
                this.onClick {
                    callback.clickWorker(item)
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
            if (item.isMaster) {
                (this as CardView).setCardBackgroundColor(resources.getColor(R.color.light_red_background_color))
            } else {
                (this as CardView).setCardBackgroundColor(resources.getColor(R.color.white))
            }
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                notifyDataSetChanged()
                item.isSelected = isChecked
                if (item.isMaster) {
                    item.isMaster = isChecked
                    MASTER_ALREADY_SELECTED = isChecked
                }
                if (task.type == Task.Type.MASTER_SLAVE_JOB &&
                    !MASTER_ALREADY_SELECTED &&
                    Task.allConditionsIsComplete(filterByPrefix("MASTER_"), item.statuses)
                ) {
                    item.isMaster = isChecked
                    MASTER_ALREADY_SELECTED = isChecked
                    if (item.isMaster) {
                        this.setCardBackgroundColor(resources.getColor(R.color.light_red_background_color))
                    } else {
                        this.setCardBackgroundColor(resources.getColor(R.color.white))
                    }
                }
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
            && !Task.anyConditionIsComplete(generalDisableConditions, worker.statuses)
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
            val masterEnableConditions = filterByPrefix("MASTER_")
            val slaveEnableConditions = filterByPrefix("SLAVE_")

            return if (items.none { worker -> worker.isSelected }) {
                Task.allConditionsIsComplete(masterEnableConditions, worker.statuses)
            } else {
                Task.allConditionsIsComplete(slaveEnableConditions, worker.statuses)
            }
        } else {
            Task.allConditionsIsComplete(task.enableConditions, worker.statuses)
        }
    }

    private fun filterByPrefix(prefix: String): MutableList<Task.Condition> {
        val enableCondition = mutableListOf<Task.Condition>()
        task.enableConditions.filter { condition -> condition.statusCode.contains(prefix) }
            .forEach {
                enableCondition.add(
                    Task.Condition(
                        it.statusCode.replace(prefix, ""),
                        it.symbol,
                        it.statusValue
                    )
                )
            }
        return enableCondition
    }

    class WorkerHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface Callback {
        fun clickWorker(worker: Worker)

        fun isExecuteTaskButtonEnable(isEnable: Boolean)

        fun selectWorker(worker: Worker)
    }
}



package com.example.hutorok.domain.model

import java.util.*
import kotlin.random.Random

class Task(
    val code: String,
    val name: String,
    val describe: String,
    val workerFunction: TaskFunction,
    val hutorFunction: TaskFunction,
    val results: List<TaskResult>,
    val permissiveCondition: List<Triple<String, Symbol, Double>> = emptyList(),
    val type: Type = Type.WORK,
    val enableCondition: List<Triple<String, Symbol, Double>> = emptyList()
) {

    enum class Type {
        WORK,
        BUILD,
        PERSON
    }

    enum class Symbol {
        MORE,
        LESS
    }

    fun countPoint(workers: List<Worker>, statusesList: List<Status>): Double {
        var usualWorkerPoint = 0
        workers.forEach { _ ->
            usualWorkerPoint += Random(Date().time).nextInt(this.workerFunction.defaultValue) + 1
        }
        var specialWorkerPoint = 0.0
        this.workerFunction.statuses.forEach { pair ->
            workers.forEach { worker ->
                worker.statuses.forEach { workerStatus ->
                    if (workerStatus.code == pair.first) {
                        specialWorkerPoint += pair.second
                    }
                }
            }
        }
        val usualHutorPoint = this.hutorFunction.defaultValue
        var specialHutorPoint = 0.0
        this.hutorFunction.statuses.forEach { pair ->
            statusesList.forEach {
                if (it.code == pair.first) {
                    specialHutorPoint += pair.second
                }
            }
        }
        return usualWorkerPoint + specialWorkerPoint + usualHutorPoint + specialHutorPoint
    }
}

class TaskFunction(
    val statuses: List<Pair<String, Double>> = emptyList(),
    val defaultValue: Int = 6
) {

    companion object {
        fun nothing(): TaskFunction {
            return TaskFunction(emptyList())
        }
    }
}

class TaskResult(
    val target: TaskTarget = TaskTarget.HUTOR,
    val action: TaskAction = TaskAction.CHANGE_STATUS_VALUE,
    val status: Status,
    val describe: String = ""
) {

    fun makeAction(
        hutorStatuses: MutableList<Status>,
        point: Double,
        workers: List<Worker>
    ) {
        if (target == TaskTarget.HUTOR) {
            changeStatuses(hutorStatuses, point)
        } else if (target == TaskTarget.ONE_SELECTED_WORKER) {
            val findWorker = workers.find { worker -> worker.isSelected }
            if (findWorker != null) {
                val workerStatuses = findWorker.statuses.toMutableList()
                changeStatuses(workerStatuses, point)
                findWorker.statuses = workerStatuses
            }
        }
    }

    private fun changeStatuses(
        statuses: MutableList<Status>,
        point: Double
    ) {
        if (this.action == TaskAction.ADD_STATUS) {
            statuses.add(this.status)
        } else if (this.action == TaskAction.REMOVE_STATUS) {
            val findStatus = statuses.find { status -> this.status.code == status.code }
            findStatus?.run {
                statuses.remove(findStatus)
            }
        } else {
            val findStatus = statuses.find { status -> this.status.code == status.code }
            if (findStatus == null) {
                val newStatus = Status(this.status)
                newStatus.value = when (this.action) {
                    TaskAction.CHANGE_STATUS_VALUE -> this.status.value + point
                    TaskAction.SET_STATUS_VALUE -> this.status.value
                    TaskAction.CHANGE_STATUS_VALUE_BY_FIXED_POINT -> this.status.value
                    else -> 0.0
                }
                statuses.add(newStatus)
            } else {
                findStatus.value = when (this.action) {
                    TaskAction.CHANGE_STATUS_VALUE -> findStatus.value + point
                    TaskAction.SET_STATUS_VALUE -> this.status.value
                    TaskAction.CHANGE_STATUS_VALUE_BY_FIXED_POINT -> findStatus.value + this.status.value
                    else -> 0.0
                }
                if (findStatus.value == 0.0) {
                    statuses.remove(findStatus)
                }
            }
        }
    }

    fun makeMessage(
        point: Double,
        workers: List<Worker>
    ): String {
        if (this.describe.contains("#VALUE")) {
            return this.describe.replace("#VALUE", point.toString()) + "\n"
        }
        if (this.describe.contains("#WORKER")) {
            return this.describe.replace("#WORKER", workers[0].name) + "\n"
        }
        return this.describe + "\n"
    }


    enum class TaskTarget {
        HUTOR,
        ONE_SELECTED_WORKER
    }

    enum class TaskAction {
        CHANGE_STATUS_VALUE,
        SET_STATUS_VALUE,
        CHANGE_STATUS_VALUE_BY_FIXED_POINT,
        ADD_STATUS,
        REMOVE_STATUS
    }
}
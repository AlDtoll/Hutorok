package com.example.hutorok.domain.model

import com.example.hutorok.domain.model.TaskFunction.Companion.ADD_ITSELF_VALUE
import com.example.hutorok.domain.model.TaskFunction.Companion.MINUS_ITSELF_VALUE
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
    val enableCondition: List<Triple<String, Symbol, Double>> = emptyList(),
    val canBeNegative: Boolean = false
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

    fun countPoint(workers: List<Worker>, hutorStatues: List<Status>): Double {
        var workersPoints = 0.0
        workers.forEach { worker ->
            workersPoints += getPointsFromFunction(this.workerFunction, worker.statuses)
        }

        val hutorPoints: Double = getPointsFromFunction(this.hutorFunction, hutorStatues)
        return if (!this.canBeNegative && workersPoints + hutorPoints < 0) {
            0.0
        } else {
            workersPoints + hutorPoints
        }
    }

    private fun getPointsFromFunction(
        taskFunction: TaskFunction,
        statuses: List<Status>
    ): Double {
        var usualPoint = 0.0
        var specialPoint = 0.0
        val upEdge = if (taskFunction.defaultValue <= 0) {
            1
        } else {
            taskFunction.defaultValue
        }
        usualPoint += Random(Date().time).nextInt(upEdge)
        taskFunction.statuses.forEach { pair ->
            specialPoint += getPointsFromStatus(statuses, pair)
        }
        return usualPoint + specialPoint
    }

    private fun getPointsFromStatus(
        statuses: List<Status>,
        pair: Pair<String, Double>
    ): Double {
        statuses.forEach { status ->
            if (status.isCoincide(pair.first)) {
                return when (pair.second) {
                    ADD_ITSELF_VALUE -> status.value
                    MINUS_ITSELF_VALUE -> -status.value
                    else -> pair.second
                }
            }
        }
        return 0.0
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

        const val ADD_ITSELF_VALUE = 100.500
        const val MINUS_ITSELF_VALUE = -100.500
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
                    TaskAction.CHANGE_STATUS_BY_RANDOM_VALUE -> calculateRandom(this.status.value)
                    else -> 0.0
                }
                if (newStatus.canBeNegative || newStatus.value > 0.0) {
                    statuses.add(newStatus)
                }
            } else {
                findStatus.value = when (this.action) {
                    TaskAction.CHANGE_STATUS_VALUE -> findStatus.value + point
                    TaskAction.SET_STATUS_VALUE -> this.status.value
                    TaskAction.CHANGE_STATUS_VALUE_BY_FIXED_POINT -> findStatus.value + this.status.value
                    TaskAction.CHANGE_STATUS_BY_RANDOM_VALUE -> findStatus.value + calculateRandom(
                        this.status.value
                    )
                    else -> 0.0
                }
                if (!findStatus.canBeNegative && findStatus.value <= 0.0) {
                    statuses.remove(findStatus)
                }
            }
        }
    }

    private fun calculateRandom(value: Double): Double {
        val upEdge = if (value == 0.0) {
            1.0
        } else {
            kotlin.math.abs(value)
        }
        val random = Random(Date().time).nextInt(upEdge.toInt()).toDouble()
        return if (value < 0) {
            -random
        } else {
            random
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
        REMOVE_STATUS,
        CHANGE_STATUS_BY_RANDOM_VALUE
    }
}
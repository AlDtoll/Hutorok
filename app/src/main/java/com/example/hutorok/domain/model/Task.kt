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
    val permissiveConditions: List<Triple<String, Symbol, Double>> = emptyList(),
    val type: Type = Type.WORK,
    val enableConditions: List<Triple<String, Symbol, Double>> = emptyList(),
    val canBeNegative: Boolean = false
) {

    companion object {
        fun conditionsIsComplete(
            conditions: List<Triple<String, Symbol, Double>>,
            statusesList: List<Status>
        ): Boolean {
            if (conditions.isEmpty()) {
                return true
            }
            conditions.forEach { condition ->
                val find = statusesList.find { condition.first == it.code }
                val findValue = find?.value ?: 0.0
                when (condition.second) {
                    Symbol.MORE -> {
                        if (findValue <= condition.third) {
                            return false
                        }
                    }
                    Symbol.LESS -> {
                        if (findValue >= condition.third) {
                            return false
                        }
                    }
                }
            }
            return true
        }
    }

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
    val successMessage: String = "",
    val conditions: List<Pair<Triple<String, Task.Symbol, Double>, Double>> = emptyList(),
    val failMessage: String = ""
) {

    companion object {
        var IS_SUCCESS = true
        var VALUE = 0.0
    }

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
        IS_SUCCESS = true
        VALUE = 0.0
        var percent = 0.0
        if (this.conditions.isEmpty()) {
            percent = 100.0
        } else {
            this.conditions.forEach { condition ->
                if (isConditionComplete(condition.first, statuses)) {
                    percent += condition.second
                }
            }
        }
        val d = Random(Date().time).nextInt(100).toDouble() + 1
        if (percent <= d) {
            IS_SUCCESS = false
            return
        }
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
                newStatus.value = 0.0
                newStatus.value = changeStatusValue(newStatus, point)
                if (newStatus.canBeNegative || newStatus.value > 0.0) {
                    statuses.add(newStatus)
                }
            } else {
                findStatus.value = changeStatusValue(findStatus, point)
                if (!findStatus.canBeNegative && findStatus.value <= 0.0) {
                    statuses.remove(findStatus)
                }
            }
        }
    }

    private fun changeStatusValue(status: Status, point: Double): Double {
        return when (this.action) {
            TaskAction.CHANGE_STATUS_VALUE -> {
                VALUE = point
                status.value + point
            }
            TaskAction.SET_STATUS_VALUE -> {
                VALUE = this.status.value
                this.status.value
            }
            TaskAction.CHANGE_STATUS_VALUE_BY_FIXED_POINT -> {
                VALUE = this.status.value
                status.value + this.status.value
            }
            TaskAction.CHANGE_STATUS_BY_RANDOM_VALUE -> {
                VALUE = calculateRandom(this.status.value)
                status.value + calculateRandom(this.status.value)
            }
            else -> 0.0
        }
    }

    private fun isConditionComplete(
        condition: Triple<String, Task.Symbol, Double>,
        statuses: MutableList<Status>
    ): Boolean {
        val find = statuses.find { condition.first == it.code }
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
        return true
    }

    private fun calculateRandom(value: Double): Double {
        val upEdge = if (value == 0.0) {
            1.0
        } else {
            kotlin.math.abs(value)
        }
        val random = Random(Date().time).nextInt(upEdge.toInt()).toDouble() + 1.0
        VALUE = random
        return if (value < 0) {
            -random
        } else {
            random
        }
    }

    fun makeMessage(
        workers: List<Worker>
    ): String {
        var message = this.successMessage
        if (!IS_SUCCESS) {
            message = this.failMessage
        }
        if (message.contains("#VALUE")) {
            return message.replace("#VALUE", VALUE.toString()) + "\n"
        }
        if (message.contains("#WORKER")) {
            return message.replace("#WORKER", workers[0].name) + "\n"
        }
        return message + "\n"
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
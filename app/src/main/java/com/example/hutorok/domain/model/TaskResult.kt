package com.example.hutorok.domain.model

import com.example.hutorok.BuildConfig
import java.util.*
import kotlin.random.Random

class TaskResult(
    val target: TaskTarget = TaskTarget.HUTOR,
    val action: TaskAction = TaskAction.CHANGE_STATUS_VALUE,
    val status: Status = Status(),
    val successMessage: String = "",
    val conditions: List<TaskResultCondition> = emptyList(),
    val failMessage: String = "",
    val workerFunction: TaskFunction = TaskFunction.nothing(),
    val hutorFunction: TaskFunction = TaskFunction.nothing(),
    val canBeNegative: Boolean = false
) {

    class TaskResultCondition(
        val statusCode: String = "",
        val symbol: Task.Symbol = Task.Symbol.MORE,
        val statusValue: Double = 0.0,
        val value: Double = 0.0
    )

    companion object {
        var IS_REPEATED = false
        var VALUE = 0.0
        var PERCENT = ""
    }

    private fun countPoint(selectedWorkers: List<Worker>, hutorStatues: List<Status>): Double {
        var workersPoints = 0.0
        selectedWorkers.forEach { worker ->
            workersPoints += getPointsFromWorker(this.workerFunction, worker)
        }

        val hutorPoints: Double = getPointsFromHutor(this.hutorFunction, hutorStatues)
        return if (!this.canBeNegative && workersPoints + hutorPoints < 0) {
            0.0
        } else {
            workersPoints + hutorPoints
        }
    }

    private fun getPointsFromHutor(
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
        taskFunction.statuses.forEach { expression ->
            specialPoint += getPointsFromStatus(statuses, expression)
        }
        return usualPoint + specialPoint
    }

    private fun getPointsFromWorker(
        taskFunction: TaskFunction,
        worker: Worker
    ): Double {
        var usualPoint = 0.0
        var specialPoint = 0.0
        val upEdge = if (taskFunction.defaultValue <= 0) {
            1
        } else {
            taskFunction.defaultValue
        }
        usualPoint += Random(Date().time).nextInt(upEdge)
        taskFunction.statuses.forEach { expression ->
            val code = expression.code
            val masterStatus = if (code.contains("_")) code.substringBefore("_") else ""
            if (masterStatus.isEmpty() ||
                masterStatus == "MASTER" && worker.isMaster ||
                masterStatus == "SLAVE" && !worker.isMaster
            ) {
                specialPoint += getPointsFromStatus(worker.statuses, expression)
            }
        }
        return usualPoint + specialPoint
    }

    private fun getPointsFromStatus(
        statuses: List<Status>,
        expression: TaskFunction.Expression
    ): Double {
        var point = 0.0
        val codeForCompare =
            expression.code.substringAfter("_").substringAfter("*").substringBefore("%")
        val limit = if (expression.code.contains("%")) {
            expression.code.substringAfter("%").toDouble()
        } else {
            5000.0
        }
        val multi = if (expression.code.contains("*")) {
            expression.code.substringBefore("*").toDouble()
        } else {
            1.0
        }
        if (codeForCompare.isEmpty()) {
            return getPoint(multi, expression.value, limit)
        }
        statuses.forEach { status ->
            if (status.isCoincide(codeForCompare)) {
                point += multi * when (expression.value) {
                    TaskFunction.ADD_ITSELF_VALUE -> getValueWithLimit(status.value, limit)
                    TaskFunction.MINUS_ITSELF_VALUE -> -getValueWithLimit(status.value, limit)
                    else -> getValueWithLimit(expression.value, limit)
                }
            }
        }
        return point
    }

    private fun getPoint(
        multi: Double,
        value: Double,
        limit: Double
    ): Double {
        return multi * when (value) {
            TaskFunction.ADD_ITSELF_VALUE -> getValueWithLimit(status.value, limit)
            TaskFunction.MINUS_ITSELF_VALUE -> -getValueWithLimit(status.value, limit)
            else -> getValueWithLimit(value, limit)
        }
    }

    private fun getValueWithLimit(
        value: Double,
        limit: Double
    ): Double {
        return if (value > limit) {
            limit
        } else {
            value
        }
    }

    fun makeAction(
        hutorStatuses: MutableList<Status>,
        workers: MutableList<Worker>
    ): String {
        var message = ""
        IS_REPEATED = false
        if (this.action == TaskAction.ADD_WORKER) {
            addNewWorker(workers)
        }
        val selectedWorkers = workers.filter { worker -> worker.isSelected }
        val point = countPoint(selectedWorkers, hutorStatuses)
        val allStatuses = mutableListOf<Status>()
        allStatuses.addAll(hutorStatuses)
        if (selectedWorkers.isNotEmpty()) {
            selectedWorkers.forEach { worker ->
                val workerStatuses = worker.statuses
                allStatuses.addAll(workerStatuses)
            }
        }
        when (target) {
            TaskTarget.HUTOR -> message += changeStatuses(hutorStatuses, point, allStatuses, null)
            TaskTarget.ALL_SELECTED_WORKER -> {
                if (selectedWorkers.isNotEmpty()) {
                    selectedWorkers.forEach { worker ->
                        val workerStatuses = worker.statuses
                        val list = mutableListOf<Status>()
                        list.addAll(workerStatuses)
                        list.addAll(hutorStatuses)
                        message += changeStatuses(workerStatuses, point, list, worker)
                        worker.statuses = workerStatuses
                    }
                }
            }
            TaskTarget.ONE_SELECTED_WORKER -> {
                if (selectedWorkers.isNotEmpty()) {
                    selectedWorkers.forEach { worker ->
                        val workerStatuses = worker.statuses
                        val list = mutableListOf<Status>()
                        list.addAll(workerStatuses)
                        list.addAll(hutorStatuses)
                        message += changeStatuses(workerStatuses, point, list, worker)
                        worker.statuses = workerStatuses
                    }
                }
            }
            TaskTarget.SPECIAL_WORKER -> {
                val description = this.status.description
                val statusCodeForFindWorker =
                    if (description.contains("$")) description.substringBefore("$") else ""
                val findWorker =
                    workers.find { worker ->
                        worker.statuses.any { status ->
                            status.isCoincide(statusCodeForFindWorker)
                        }
                    }
                if (findWorker != null) {
                    val workerStatuses = findWorker.statuses
                    val list = mutableListOf<Status>()
                    list.addAll(workerStatuses)
                    list.addAll(hutorStatuses)
                    message += changeStatuses(workerStatuses, point, list, findWorker)
                    findWorker.statuses = workerStatuses
                }
            }
        }
        return prepareMessage(message)
    }

    private fun addNewWorker(workers: MutableList<Worker>) {
        val worker = Worker(
            createNameForNewWorker(),
            createNickNameForNewWorker(),
            createAgeForNewWorker(),
            mutableListOf(),
            true
        )
        workers.add(worker)
    }

    private fun createNameForNewWorker(): String {
        return this.status.name
    }

    private fun createNickNameForNewWorker(): String {
        return this.status.description
    }

    private fun createAgeForNewWorker(): Age {
        return Age.valueOf(this.status.code)
    }

    private fun changeStatuses(
        statusesForChange: MutableList<Status>,
        point: Double,
        statusesForCompare: MutableList<Status>,
        worker: Worker?
    ): String {
        VALUE = 0.0
        var percent = 0.0
        PERCENT = ""
        if (this.conditions.isEmpty()) {
            percent = 100.0
        } else {
            this.conditions.forEach { condition ->
                if (worker != null) {
                    val code = condition.statusCode
                    val masterStatus = if (code.contains("_")) code.substringBefore("_") else ""
                    if (masterStatus.isEmpty() ||
                        masterStatus == "MASTER" && worker.isMaster ||
                        masterStatus == "SLAVE" && !worker.isMaster
                    ) {
                        if (isConditionComplete(Task.Condition(condition), statusesForCompare)) {
                            percent += if (condition.value == TaskFunction.ADD_ITSELF_VALUE
                                || condition.value == TaskFunction.ADD_ITSELF_VALUE
                            ) {
                                getPointsFromStatus(
                                    worker.statuses,
                                    TaskFunction.Expression(
                                        condition.statusCode,
                                        condition.value
                                    )
                                )
                            } else {
                                condition.value
                            }
                        }
                    }
                } else {
                    if (isConditionComplete(Task.Condition(condition), statusesForCompare)) {
                        percent += when (condition.value) {
                            TaskFunction.ADD_ITSELF_VALUE -> getPointsFromStatus(
                                statusesForChange,
                                TaskFunction.Expression(
                                    condition.statusCode,
                                    condition.value
                                )
                            )
                            TaskFunction.MINUS_ITSELF_VALUE -> -getPointsFromStatus(
                                statusesForChange,
                                TaskFunction.Expression(
                                    condition.statusCode,
                                    condition.value
                                )
                            )
                            else -> condition.value
                        }
                    }
                }
            }
        }
        val d = Random(Date().time).nextInt(100).toDouble()
        PERCENT = "$percent/$d"
        if (percent <= d) {
            return makeMessage(worker, false)
        }
        when {
            this.action == TaskAction.ADD_STATUS -> statusesForChange.add(this.status)
            this.action == TaskAction.REMOVE_STATUS -> {
                val findStatus =
                    statusesForChange.find { status -> status.isCoincide(this.status.code) }
                findStatus?.run {
                    statusesForChange.remove(findStatus)
                }
            }
            else -> {
                val findStatus =
                    statusesForChange.find { status -> status.isCoincide(this.status.code) }
                if (findStatus == null) {
                    val newStatus = Status(this.status)
                    newStatus.value = 0.0
                    newStatus.value = changeStatusValue(newStatus, point)
                    if (newStatus.canBeNegative || newStatus.value > 0.0) {
                        statusesForChange.add(newStatus)
                    }
                } else {
                    findStatus.value = changeStatusValue(findStatus, point)
                    if (!findStatus.canBeNegative && findStatus.value <= 0.0) {
                        statusesForChange.remove(findStatus)
                    }
                }
            }
        }
        return makeMessage(worker, true)
    }

    private fun changeStatusValue(status: Status, point: Double): Double {
        return when (this.action) {
            TaskAction.CHANGE_STATUS_VALUE -> {
                VALUE = point
                status.value + point
            }
            TaskAction.CHANGE_STATUS_VALUE_MINUS -> {
                VALUE = -point
                status.value - point
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
        condition: Task.Condition,
        statuses: MutableList<Status>
    ): Boolean {
        val codeForCompare = condition.statusCode.substringAfter("_")
        if (codeForCompare.isEmpty()) {
            return true
        }
        val find = statuses.find { status -> status.isCoincide(codeForCompare) }
        val findValue = find?.value ?: 0.0
        when (condition.symbol) {
            Task.Symbol.MORE -> {
                if (findValue <= condition.statusValue) {
                    return false
                }
            }
            Task.Symbol.LESS -> {
                if (findValue >= condition.statusValue) {
                    return false
                }
            }
            Task.Symbol.EQUALS -> {
                if (findValue != condition.statusValue) {
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

    private fun makeMessage(
        worker: Worker?,
        isSuccess: Boolean
    ): String {
        var message = this.successMessage
        if (!isSuccess) {
            message = this.failMessage
        }
        if (message.isEmpty()) {
            return ""
        }
        if (message.contains("#VALUE")) {
            message = message.replace("#VALUE", VALUE.toString())
            IS_REPEATED = false
        }
        if (message.contains("#WORKER") && worker != null) {
            message = message.replace("#WORKER", worker.name)
            IS_REPEATED = false
        } else {
            if (IS_REPEATED) {
                return ""
            }
            IS_REPEATED = true
        }
        if (BuildConfig.DEBUG) {
            return "$message $PERCENT\n"
        }
        return if (message.contains("!!")) {
            ""
        } else {
            "$message\n"
        }
    }

    private fun prepareMessage(message: String): String {
        return message
    }

    enum class TaskTarget {
        HUTOR,
        ONE_SELECTED_WORKER,
        ALL_SELECTED_WORKER,
        SPECIAL_WORKER
    }

    enum class TaskAction {
        CHANGE_STATUS_VALUE,
        CHANGE_STATUS_VALUE_MINUS,
        SET_STATUS_VALUE,
        CHANGE_STATUS_VALUE_BY_FIXED_POINT,
        ADD_STATUS,
        REMOVE_STATUS,
        CHANGE_STATUS_BY_RANDOM_VALUE,
        ADD_WORKER
    }
}
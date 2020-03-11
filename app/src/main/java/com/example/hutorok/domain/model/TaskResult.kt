package com.example.hutorok.domain.model

import com.example.hutorok.BuildConfig
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.random.Random

class TaskResult(
    val target: TaskTarget = TaskTarget.HUTOR,
    val action: TaskAction = TaskAction.CHANGE_STATUS_VALUE,
    val status: Status,
    val successMessage: String = "",
    val conditions: List<Pair<Triple<String, Task.Symbol, Double>, Double>> = emptyList(),
    val failMessage: String = "",
    val workerFunction: TaskFunction,
    val hutorFunction: TaskFunction,
    val canBeNegative: Boolean = false
) {
    constructor(jsonObject: JSONObject) : this(
        target = TaskTarget.valueOf(jsonObject.optString("target")),
        action = TaskAction.valueOf(jsonObject.optString("action")),
        status = Status(jsonObject.getJSONObject("status")),
        successMessage = jsonObject.optString("successMessage"),
        conditions = parseConditions(jsonObject.optJSONArray("conditions")),
        failMessage = jsonObject.optString("failMessage"),
        workerFunction = if (jsonObject.optJSONObject("workerFunction") != null) {
            TaskFunction(jsonObject.optJSONObject("workerFunction"))
        } else {
            TaskFunction.nothing()
        },
        hutorFunction = TaskFunction(jsonObject.optJSONObject("hutorFunction")),
        canBeNegative = jsonObject.optBoolean("canBeNegative")
    )

    companion object {
        var IS_REPEATED = false
        var VALUE = 0.0
        var PERCENT = ""

        fun parseConditions(jsonArray: JSONArray?): List<Pair<Triple<String, Task.Symbol, Double>, Double>> {
            jsonArray?.run {
                val conditions = mutableListOf<Pair<Triple<String, Task.Symbol, Double>, Double>>()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    conditions.add(
                        Pair(
                            Triple(
                                jsonObject.optString("statusCode"),
                                Task.Symbol.valueOf(jsonObject.optString("symbol")),
                                jsonObject.getDouble("statusValue")
                            ),
                            jsonObject.getDouble("value")
                        )
                    )
                }
                return conditions
            }
            return emptyList()
        }
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
        taskFunction.statuses.forEach { pair ->
            specialPoint += getPointsFromStatus(statuses, pair)
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
        taskFunction.statuses.forEach { pair ->
            val code = pair.first
            val masterStatus = if (code.contains("_")) code.substringBefore("_") else ""
            if (masterStatus.isEmpty() ||
                masterStatus == "MASTER" && worker.isMaster ||
                masterStatus == "SLAVE" && !worker.isMaster
            ) {
                specialPoint += getPointsFromStatus(worker.statuses, pair)
            }
        }
        return usualPoint + specialPoint
    }

    private fun getPointsFromStatus(
        statuses: List<Status>,
        pair: Pair<String, Double>
    ): Double {
        var point = 0.0
        val codeForCompare = pair.first.substringAfter("_").substringAfter("*").substringBefore("%")
        val limit = if (pair.first.contains("%")) {
            pair.first.substringAfter("%").toDouble()
        } else {
            5000.0
        }
        val multi = if (pair.first.contains("*")) {
            pair.first.substringBefore("*").toDouble()
        } else {
            1.0
        }
        if (codeForCompare.isEmpty()) {
            return getPoint(multi, pair.second, limit)
        }
        statuses.forEach { status ->
            if (status.isCoincide(codeForCompare)) {
                point += multi * when (pair.second) {
                    TaskFunction.ADD_ITSELF_VALUE -> getValueWithLimit(status.value, limit)
                    TaskFunction.MINUS_ITSELF_VALUE -> -getValueWithLimit(status.value, limit)
                    else -> getValueWithLimit(pair.second, limit)
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
                    val code = condition.first.first
                    val masterStatus = if (code.contains("_")) code.substringBefore("_") else ""
                    if (masterStatus.isEmpty() ||
                        masterStatus == "MASTER" && worker.isMaster ||
                        masterStatus == "SLAVE" && !worker.isMaster
                    ) {
                        if (isConditionComplete(condition.first, statusesForCompare)) {
                            percent += if (condition.second == TaskFunction.ADD_ITSELF_VALUE
                                || condition.second == TaskFunction.ADD_ITSELF_VALUE
                            ) {
                                getPointsFromStatus(
                                    worker.statuses,
                                    Pair(condition.first.first, condition.second)
                                )
                            } else {
                                condition.second
                            }
                        }
                    }
                } else {
                    if (isConditionComplete(condition.first, statusesForCompare)) {
                        percent += when (condition.second) {
                            TaskFunction.ADD_ITSELF_VALUE -> getPointsFromStatus(
                                statusesForChange,
                                Pair(condition.first.first, condition.second)
                            )
                            TaskFunction.MINUS_ITSELF_VALUE -> -getPointsFromStatus(
                                statusesForChange,
                                Pair(condition.first.first, condition.second)
                            )
                            else -> condition.second
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
        condition: Triple<String, Task.Symbol, Double>,
        statuses: MutableList<Status>
    ): Boolean {
        val codeForCompare = condition.first.substringAfter("_")
        if (codeForCompare.isEmpty()) {
            return true
        }
        val find = statuses.find { status -> status.isCoincide(codeForCompare) }
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
            Task.Symbol.EQUALS -> {
                if (findValue != condition.third) {
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
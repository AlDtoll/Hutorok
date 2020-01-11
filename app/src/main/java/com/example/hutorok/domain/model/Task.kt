package com.example.hutorok.domain.model

import com.example.hutorok.domain.model.TaskFunction.Companion.ADD_ITSELF_VALUE
import com.example.hutorok.domain.model.TaskFunction.Companion.MINUS_ITSELF_VALUE
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.random.Random

class Task(
    val code: String,
    val name: String,
    val description: String,
    val workerFunction: TaskFunction,
    val hutorFunction: TaskFunction,
    val results: List<TaskResult>,
    val permissiveConditions: List<Triple<String, Symbol, Double>> = emptyList(),
    val type: Type = Type.WORK,
    val enableConditions: List<Triple<String, Symbol, Double>> = emptyList(),
    val canBeNegative: Boolean = false
) {

    constructor(jsonObject: JSONObject) : this(
        code = jsonObject.optString("code"),
        name = jsonObject.optString("name"),
        description = jsonObject.optString("description"),
        workerFunction = if (jsonObject.optJSONObject("workerFunction") != null) {
            TaskFunction(jsonObject.optJSONObject("workerFunction"))
        } else {
            TaskFunction.nothing()
        },
        hutorFunction = TaskFunction(jsonObject.optJSONObject("hutorFunction")),
        results = parseTaskResults(jsonObject.optJSONArray("results")),
        permissiveConditions = parseConditions(jsonObject.optJSONArray("permissiveConditions")),
        type = if (jsonObject.optString("type").isNotEmpty()) {
            Type.valueOf(jsonObject.optString("type"))
        } else {
            Type.WORK
        },
        enableConditions = parseConditions(jsonObject.optJSONArray("enableConditions")),
        canBeNegative = jsonObject.optBoolean("canBeNegative")
    )

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
                    Symbol.EQUALS -> {
                        if (findValue != condition.third) {
                            return false
                        }
                    }
                }
            }
            return true
        }

        fun parseTaskResults(jsonArray: JSONArray?): MutableList<TaskResult> {
            jsonArray?.run {
                val taskResults = mutableListOf<TaskResult>()
                for (i in 0 until jsonArray.length()) {
                    taskResults.add(TaskResult(jsonArray.getJSONObject(i)))
                }
                return taskResults
            }
            return mutableListOf()
        }

        fun parseConditions(jsonArray: JSONArray?): List<Triple<String, Task.Symbol, Double>> {
            jsonArray?.run {
                val conditions = mutableListOf<Triple<String, Task.Symbol, Double>>()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    conditions.add(
                        Triple(
                            jsonObject.optString("statusCode"),
                            Symbol.valueOf(jsonObject.optString("symbol")),
                            jsonObject.optDouble("statusValue")
                        )
                    )
                }
                return conditions
            }
            return mutableListOf()
        }

        fun selectAll(
            workers: List<Worker>,
            enableConditions: List<Triple<String, Symbol, Double>>
        ) {
            workers.forEach { worker ->
                if (!worker.isInvisible && conditionsIsComplete(
                        enableConditions,
                        worker.statuses
                    )
                ) {
                    worker.isSelected = true
                }
            }
        }

        fun deselectAll(workers: List<Worker>) {
            workers.forEach { worker -> worker.isSelected = false }
        }
    }

    enum class Type {
        WORK,
        BUILD,
        PERSON,
        PERSONAL_JOB,
        MASTER_SLAVE_JOB
    }

    enum class Symbol {
        MORE,
        LESS,
        EQUALS
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
        var point = 0.0
        statuses.forEach { status ->
            if (status.isCoincide(pair.first)) {
                point += when (pair.second) {
                    ADD_ITSELF_VALUE -> status.value
                    MINUS_ITSELF_VALUE -> -status.value
                    else -> pair.second
                }
            }
        }
        return point
    }
}

class TaskFunction(
    val statuses: List<Pair<String, Double>> = emptyList(),
    val defaultValue: Int = 0
) {
    constructor(jsonObject: JSONObject?) : this(
        statuses = parseStatuses(jsonObject?.optJSONArray("statuses")),
        defaultValue = jsonObject?.optInt("defaultValue") ?: 0
    )

    companion object {
        fun nothing(): TaskFunction {
            return TaskFunction(emptyList(), 0)
        }

        const val ADD_ITSELF_VALUE = 100.500
        const val MINUS_ITSELF_VALUE = -100.500

        fun parseStatuses(jsonArray: JSONArray?): List<Pair<String, Double>> {
            jsonArray?.run {
                val statuses = mutableListOf<Pair<String, Double>>()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    statuses.add(Pair(jsonObject.optString("code"), jsonObject.optDouble("value")))
                }
                return statuses
            }
            return emptyList()
        }
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
    constructor(jsonObject: JSONObject) : this(
        target = TaskTarget.valueOf(jsonObject.optString("target")),
        action = TaskAction.valueOf(jsonObject.optString("action")),
        status = Status(jsonObject.getJSONObject("status")),
        successMessage = jsonObject.optString("successMessage"),
        conditions = parseConditions(jsonObject.optJSONArray("conditions")),
        failMessage = jsonObject.optString("failMessage")
    )

    companion object {
        var IS_REPEATED = false
        var VALUE = 0.0

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

    fun makeAction(
        hutorStatuses: MutableList<Status>,
        point: Double,
        workers: MutableList<Worker>
    ): String {
        var message = ""
        IS_REPEATED = false
        if (this.action == TaskAction.ADD_WORKER) {
            addNewWorker(workers)
        }
        val allStatuses = mutableListOf<Status>()
        allStatuses.addAll(hutorStatuses)
        val selectedWorkers = workers.filter { worker -> worker.isSelected }
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
                        message += changeStatuses(workerStatuses, point, list, worker)
                        worker.statuses = workerStatuses
                    }
                }
            }
            TaskTarget.ONE_SELECTED_WORKER -> {
                val findWorker = workers.find { worker -> worker.isSelected }
                if (findWorker != null) {
                    val workerStatuses = findWorker.statuses
                    message += changeStatuses(workerStatuses, point, allStatuses, findWorker)
                    findWorker.statuses = workerStatuses
                }
            }
            TaskTarget.SPECIAL_WORKER -> {
                val statusCodeForFindWorker = this.status.description.substringBefore("$")
                val findWorker =
                    workers.find { worker ->
                        worker.statuses.any { status ->
                            status.code == statusCodeForFindWorker
                        }
                    }
                if (findWorker != null) {
                    val workerStatuses = findWorker.statuses
                    message += changeStatuses(workerStatuses, point, allStatuses, findWorker)
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
        if (this.conditions.isEmpty()) {
            percent = 100.0
        } else {
            this.conditions.forEach { condition ->
                if (isConditionComplete(condition.first, statusesForCompare)) {
                    percent += when (condition.second) {
                        ADD_ITSELF_VALUE -> condition.first.third
                        MINUS_ITSELF_VALUE -> -condition.first.third
                        else -> condition.second
                    }
                }
            }
        }
        val d = Random(Date().time).nextInt(100).toDouble() + 1
        if (percent <= d) {
            return makeMessage(worker, false)
        }
        when {
            this.action == TaskAction.ADD_STATUS -> statusesForChange.add(this.status)
            this.action == TaskAction.REMOVE_STATUS -> {
                val findStatus =
                    statusesForChange.find { status -> this.status.code == status.code }
                findStatus?.run {
                    statusesForChange.remove(findStatus)
                }
            }
            else -> {
                val findStatus =
                    statusesForChange.find { status -> this.status.code == status.code }
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
        if (condition.first.isEmpty()) {
            return true
        }
        val find = statuses.find { status -> status.code == condition.first }
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
        return message + "\n"
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
        SET_STATUS_VALUE,
        CHANGE_STATUS_VALUE_BY_FIXED_POINT,
        ADD_STATUS,
        REMOVE_STATUS,
        CHANGE_STATUS_BY_RANDOM_VALUE,
        ADD_WORKER
    }
}
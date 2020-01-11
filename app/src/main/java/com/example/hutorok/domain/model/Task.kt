package com.example.hutorok.domain.model

import org.json.JSONArray
import org.json.JSONObject

class Task(
    val code: String,
    val name: String,
    val description: String,
    val results: List<TaskResult>,
    val permissiveConditions: List<Triple<String, Symbol, Double>> = emptyList(),
    val type: Type = Type.WORK,
    val enableConditions: List<Triple<String, Symbol, Double>> = emptyList()
) {

    constructor(jsonObject: JSONObject) : this(
        code = jsonObject.optString("code"),
        name = jsonObject.optString("name"),
        description = jsonObject.optString("description"),
        results = parseTaskResults(jsonObject.optJSONArray("results")),
        permissiveConditions = parseConditions(jsonObject.optJSONArray("permissiveConditions")),
        type = if (jsonObject.optString("type").isNotEmpty()) {
            Type.valueOf(jsonObject.optString("type"))
        } else {
            Type.WORK
        },
        enableConditions = parseConditions(jsonObject.optJSONArray("enableConditions"))
    )

    companion object {
        fun allConditionsIsComplete(
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

        fun anyConditionIsComplete(
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
                        if (findValue > condition.third) {
                            return true
                        }
                    }
                    Symbol.LESS -> {
                        if (findValue < condition.third) {
                            return true
                        }
                    }
                    Symbol.EQUALS -> {
                        if (findValue == condition.third) {
                            return true
                        }
                    }
                }
            }
            return false
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
                if (!worker.isInvisible && allConditionsIsComplete(
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
}
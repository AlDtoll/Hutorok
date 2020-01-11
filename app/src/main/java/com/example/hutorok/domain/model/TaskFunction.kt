package com.example.hutorok.domain.model

import org.json.JSONArray
import org.json.JSONObject

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
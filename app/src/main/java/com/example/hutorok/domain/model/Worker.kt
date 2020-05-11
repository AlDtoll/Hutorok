package com.example.hutorok.domain.model

import org.json.JSONArray
import org.json.JSONObject

class Worker(
    val name: String = "",
    var nickname: String? = "",
    var age: Age = Age.ADULT,
    var statuses: MutableList<Status> = mutableListOf(),
    var isSelected: Boolean = false,
    var isInvisible: Boolean = false,
    var isMaster: Boolean = false
) {
    constructor() : this(
        name = "Новый хуторянин",
        nickname = "",
        age = Age.ADULT
    )

    constructor(jsonObject: JSONObject) : this(
        name = jsonObject.optString("name"),
        nickname = jsonObject.optString("nickname"),
        age = Age.valueOf(jsonObject.optString("age")),
        statuses = parseStatuses(jsonObject.optJSONArray("statuses")),
        isSelected = jsonObject.optBoolean("isSelected"),
        isInvisible = jsonObject.optBoolean("isInvisible")
    )

    companion object {
        fun parseStatuses(jsonArray: JSONArray?): MutableList<Status> {
            jsonArray?.run {
                val statuses = mutableListOf<Status>()
                for (i in 0 until jsonArray.length()) {
                    statuses.add(Status(jsonArray.getJSONObject(i)))
                }
                return statuses
            }
            return mutableListOf()
        }
    }
}

enum class Age(val code: String) {
    CHILDREN("ребенок"),
    YOUNG("подросток"),
    ADULT("взрослый"),
    OLD("старый")
}
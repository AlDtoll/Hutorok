package com.example.hutorok.domain.model

import org.json.JSONObject

class Status(
    val code: String,
    val name: String,
    var description: String = "",
    var value: Double = 0.0,
    val visible: Boolean = false,
    val canBeNegative: Boolean = false
) {
    constructor(status: Status) : this(
        code = status.code,
        name = status.name,
        description = status.description,
        value = status.value,
        visible = status.visible,
        canBeNegative = status.canBeNegative
    )

    constructor(jsonObject: JSONObject) : this(
        code = jsonObject.optString("code"),
        name = jsonObject.optString("name"),
        description = jsonObject.optString("description"),
        value = jsonObject.optDouble("value"),
        visible = jsonObject.optBoolean("visible"),
        canBeNegative = jsonObject.optBoolean("canBeNegative")
    )

    fun isCoincide(codeForCompare: String): Boolean {
        if (codeForCompare.contains("?")) {
            val keyWord = codeForCompare.replace("?", "")
            return this.code.contains(keyWord, true)
        }
        return this.code == codeForCompare
    }
}


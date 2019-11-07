package com.example.hutorok.domain.model

class Status(
    val code: String,
    val name: String,
    var description: String = "",
    var value: Double,
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

    fun isCoincide(codeForCompare: String): Boolean {
        if (codeForCompare.contains("?")) {
            val keyWord = codeForCompare.replace("?", "")
            return this.code.contains(keyWord)
        }
        return this.code == codeForCompare
    }
}


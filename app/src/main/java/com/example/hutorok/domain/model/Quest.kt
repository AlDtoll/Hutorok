package com.example.hutorok.domain.model

class Quest(
    val code: String = "",
    val name: String = "",
    val scenes: List<Scene> = emptyList()
)

class Scene(
    val code: String = "",
    val text: String = "",
    var selects: List<Select> = emptyList(),
    val type: Type = Type.STEP
) {

    enum class Type {
        TECHNICAL,
        STEP,
        END
    }
}

class Select(
    val task: Task = Task(),
    val nextScene: String = ""
)
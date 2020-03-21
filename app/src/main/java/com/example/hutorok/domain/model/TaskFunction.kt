package com.example.hutorok.domain.model

class TaskFunction(
    val statuses: List<Expression> = emptyList(),
    val defaultValue: Int = 0
) {

    class Expression(
        val code: String = "",
        val value: Double = 0.0
    )

    companion object {
        fun nothing(): TaskFunction {
            return TaskFunction(emptyList(), 0)
        }

        const val ADD_ITSELF_VALUE = 100.500
        const val MINUS_ITSELF_VALUE = -100.500
    }
}
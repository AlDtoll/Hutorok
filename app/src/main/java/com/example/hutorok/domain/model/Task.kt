package com.example.hutorok.domain.model

class Task(
    val code: String = "",
    val name: String = "",
    val description: String = "",
    val results: List<TaskResult> = emptyList(),
    val permissiveConditions: List<Condition> = emptyList(),
    val type: Type = Type.WORK,
    val enableConditions: List<Condition> = emptyList()
) {

    companion object {
        fun allConditionsIsComplete(
            conditions: List<Condition>,
            statusesList: List<Status>
        ): Boolean {
            if (conditions.isEmpty()) {
                return true
            }
            conditions.forEach { condition ->
                val find = statusesList.find { it.isCoincide(condition.statusCode) }
                val findValue = find?.value ?: 0.0
                when (condition.symbol) {
                    Symbol.MORE -> {
                        if (findValue <= condition.statusValue) {
                            return false
                        }
                    }
                    Symbol.LESS -> {
                        if (findValue >= condition.statusValue) {
                            return false
                        }
                    }
                    Symbol.EQUALS -> {
                        if (findValue != condition.statusValue) {
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
                val find = statusesList.find { it.isCoincide(condition.first) }
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

        fun selectAll(
            workers: List<Worker>,
            enableConditions: List<Condition>
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
            workers.forEach { worker ->
                worker.isSelected = false
                worker.isMaster = false
            }
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

    class Condition(
        val statusCode: String = "",
        val symbol: Symbol = Symbol.MORE,
        val statusValue: Double = 0.0
    ) {
        constructor(taskResultCondition: TaskResult.TaskResultCondition) : this(
            statusCode = taskResultCondition.statusCode,
            symbol = taskResultCondition.symbol,
            statusValue = taskResultCondition.statusValue
        )
    }
}
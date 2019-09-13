package com.example.hutorok.domain.model

class Task(
    val code: String,
    val name: String,
    val describe: String,
    val workerFunction: TaskFunction,
    val hutorFunction: TaskFunction,
    val results: List<TaskResult>
)

class TaskFunction(val statuses: List<Pair<String, Double>> = emptyList(), val defaultValue: Int = 6)

class TaskResult(
    val target: TaskTarget = TaskTarget.HUTOR,
    val action: TaskAction = TaskAction.CHANGE_STATUS_VALUE,
    val status: Status,
    val describe: String = ""
)

enum class TaskTarget {
    HUTOR
}

enum class TaskAction {
    CHANGE_STATUS_VALUE,
    SET_STATUS_VALUE
}
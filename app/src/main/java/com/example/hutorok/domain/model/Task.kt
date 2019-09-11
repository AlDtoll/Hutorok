package com.example.hutorok.domain.model

class Task(
    val code: String,
    val name: String,
    val describe: String,
    val workerFunction: TaskFunction,
    val hutorFunction: TaskFunction
)

class TaskFunction(val statuses: List<Pair<String, Double>> = emptyList(), val defaultValue: Int = 6)
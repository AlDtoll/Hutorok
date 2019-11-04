package com.example.hutorok.domain.model

import java.util.*
import kotlin.random.Random

class Worker(
    val name: String,
    var nickname: String? = "",
    var age: Age,
    var statuses: MutableList<Status>,
    var isChecked: Boolean = false
) {

    fun markAsWorked(): String {
        val workedStatus = this.statuses.find { status -> status.code == "worked" }
        if (workedStatus == null) {
            val workerStatuses = this.statuses
            workerStatuses.add(
                Status(
                    "worked",
                    "Работал",
                    "Если работать слишком много, то можно надорваться",
                    1.0,
                    true
                )
            )
            this.statuses = workerStatuses
        } else {
            workedStatus.value = workedStatus.value + 1
        }
        return "$name поработал\n"
    }

    fun fine(): String {
        val findStatus = this.statuses.find { status -> status.code == "worked" }
        if (findStatus != null) {
            //todo переделать, сделать отдельно правило или настройку
            val zeroOrOne = Random(Date().time).nextInt(2)
            if (zeroOrOne == 1) {
                val diseaseStatus = this.statuses.find { status -> status.code == "workDISEASE" }
                if (diseaseStatus == null) {
                    this.statuses.add(
                        Status(
                            "workDISEASE",
                            "Перетрудился",
                            "Недомогание, слабость и головная боль",
                            1.0,
                            true
                        )
                    )
                } else {
                    diseaseStatus.value = diseaseStatus.value + 1
                }
                return "$name перетрудился на работе\n"
            }
        }
        return ""
    }

    fun rest() {
        val findStatus = this.statuses.find { status -> status.code == "worked" }
        if (findStatus != null) {
            if (findStatus.value <= 1) {
                this.statuses.remove(findStatus)
            } else {
                findStatus.value = findStatus.value - 1
            }
        }
    }
}

enum class Age(val code: String) {
    CHILDREN("ребенок"),
    YOUNG("подросток"),
    ADULT("взрослый"),
    OLD("старый")
}
package com.example.hutorok.domain.model

class Worker(val name: String, var nickname: String, var age: Age, var statuses: List<Status>)

enum class Age(val code: String) {
    CHILDREN("ребенок"),
    YOUNG("подросток"),
    ADULT("взрослый"),
    OLD("старый")
}
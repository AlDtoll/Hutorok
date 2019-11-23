package com.example.hutorok.domain

import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Worker

interface ILoadDataInteractor {

    fun update(
        workers: MutableList<Worker>,
        tasks: MutableList<Task>
    )
}
package com.example.hutorok.domain

import com.example.hutorok.domain.model.Quest
import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Worker

class LoadDataInteractor(
) : ILoadDataInteractor {

    override fun update(
        workers: MutableList<Worker>,
        tasks: MutableList<Task>,
        hutorokStatuses: MutableList<Status>,
        endTasks: MutableList<Task>,
        events: MutableList<String>,
        turnNumber: Int,
        startQuest: Quest
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
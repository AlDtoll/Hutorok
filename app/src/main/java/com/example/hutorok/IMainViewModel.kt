package com.example.hutorok

import androidx.lifecycle.LiveData
import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Worker

interface IMainViewModel {

    fun nowScreen(): LiveData<NowScreen>

    fun onBackPressed()

    fun loadData(
        workers: MutableList<Worker>,
        tasks: MutableList<Task>,
        hutorokStatuses: MutableList<Status>,
        endTasks: MutableList<Task>,
        events: MutableList<String>,
        turnNumber: Int
    )

    fun messageData(): LiveData<String>

}
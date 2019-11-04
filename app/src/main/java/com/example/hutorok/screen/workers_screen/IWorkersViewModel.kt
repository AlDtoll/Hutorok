package com.example.hutorok.screen.workers_screen

import androidx.lifecycle.LiveData
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Worker

interface IWorkersViewModel {

    fun workersData(): LiveData<List<Worker>>

    fun clickWorker(worker: Worker)

    fun isOrderScenario(): LiveData<Boolean>

    fun clickExecute()

    fun importantStatusesData(): LiveData<List<String>>

    fun taskData(): LiveData<Task>

    fun isExecuteTaskButtonEnable(): LiveData<Boolean>

    fun clickCheckbox(worker: Worker)

}
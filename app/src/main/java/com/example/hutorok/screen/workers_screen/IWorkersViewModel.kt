package com.example.hutorok.screen.workers_screen

import androidx.lifecycle.LiveData
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Worker

interface IWorkersViewModel {

    fun workersData(): LiveData<MutableList<Worker>>

    fun clickWorker(worker: Worker)

    fun isOrderScenario(): LiveData<Boolean>

    fun clickExecute()

    fun importantStatusesData(): LiveData<List<String>>

    fun taskData(): LiveData<Task>

    fun isExecuteTaskButtonEnable(): LiveData<Boolean>

    fun clickCheckbox(worker: Worker)

    fun generalDisableStatus(): LiveData<List<Triple<String, Task.Symbol, Double>>>

}
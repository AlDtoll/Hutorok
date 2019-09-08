package com.example.hutorok.screen.workers

import androidx.lifecycle.LiveData
import com.example.hutorok.domain.model.Worker

interface IWorkersViewModel {

    fun workersData(): LiveData<List<Worker>>

    fun clickWorker(worker: Worker)

}
package com.example.hutorok.screen.worker_info

import androidx.lifecycle.LiveData
import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Worker

interface IWorkerInfoViewModel {

    fun workerData(): LiveData<Worker>

    fun statusesData(): LiveData<List<Status>>

}
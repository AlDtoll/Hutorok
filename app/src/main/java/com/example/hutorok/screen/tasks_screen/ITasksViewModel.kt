package com.example.hutorok.screen.tasks_screen

import androidx.lifecycle.LiveData
import com.example.hutorok.domain.model.Task

interface ITasksViewModel {

    fun tasksData(): LiveData<List<Task>>

    fun clickTask(task: Task)

}
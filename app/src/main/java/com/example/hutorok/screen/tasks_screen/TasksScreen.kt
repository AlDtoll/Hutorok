package com.example.hutorok.screen.tasks_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hutorok.R
import com.example.hutorok.domain.model.Task
import com.example.hutorok.screen.TaskAdapter
import kotlinx.android.synthetic.main.fragment_tasks.*
import org.koin.android.ext.android.inject

class TasksScreen : Fragment() {

    private val tasksViewModel: ITasksViewModel by inject()

    companion object {
        fun newInstance(): TasksScreen = TasksScreen()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        initToolbar()

        tasksViewModel.tasksData().observe(this, Observer {
            taskAdapter.clear()
            it?.run {
                showTasks(it)
            }
        })

        initRecyclerView()
    }

    private fun initToolbar() {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).title = getString(R.string.tasks_screen_toolbar_title)
    }

    private lateinit var taskAdapter: TaskAdapter

    private val callback = object : TaskAdapter.Callback {

        override fun selectTask(task: Task) {
            tasksViewModel.clickTask(task)
        }
    }

    private fun initRecyclerView() {
        taskAdapter = TaskAdapter(callback)
        tasksList.adapter = taskAdapter
        tasksList.itemAnimator = DefaultItemAnimator()
        context?.run {
            tasksList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }
    }

    private fun showTasks(tasks: List<Task>) {
        taskAdapter.items = ArrayList(tasks)
    }
}
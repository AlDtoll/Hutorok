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
import com.example.hutorok.App
import com.example.hutorok.App.Companion.ALREADY_LOADED
import com.example.hutorok.MainActivity
import com.example.hutorok.R
import com.example.hutorok.domain.model.Task
import com.example.hutorok.ext.addTextWatcher
import com.example.hutorok.ext.onClick
import com.example.hutorok.screen.TaskAdapter
import kotlinx.android.synthetic.main.fragment_tasks.*
import org.koin.android.ext.android.inject

class TasksScreen : Fragment() {

    private val tasksViewModel: ITasksViewModel by inject()

    companion object {
        fun newInstance(): TasksScreen = TasksScreen()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadAdventure()

        initUi()
    }

    private fun loadAdventure() {
        val prefs = App.instance.getSharedPreferences(
            MainActivity.APP_PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        )
        prefs?.run {
            this.getString(MainActivity.PATH, App.CURRENT_ADVENTURE)?.run {
                App.CURRENT_ADVENTURE = this
                if (!ALREADY_LOADED && App.CURRENT_ADVENTURE != App.ADVENTURE_DEFAULT) {
                    tasksViewModel.loadAdventure()
                    ALREADY_LOADED = true
                }
            }
        }
    }

    private fun initUi() {
        initToolbar()

        tasksScreenEndTurnButton.setOnClickListener {
            tasksViewModel.clickEndTurnButton()
        }

        tasksViewModel.tasksData().observe(this, Observer {
            taskAdapter.clear()
            it?.run {
                showTasks(it)
            }
        })

        tasksSearch.addTextWatcher { search, _, _, _ ->
            tasksViewModel.searchChange(search.toString())
        }

        tasksViewModel.searchData().observe(this, Observer {
            it?.run {
                if (it != tasksSearch.text.toString()) {
                    tasksSearch.setText(it)
                    tasksSearch.setSelection(it.length)
                }
                if (it.isNotEmpty()) {
                    tasksSearchClearButton.visibility = View.VISIBLE
                } else {
                    tasksSearchClearButton.visibility = View.GONE
                }
            }
        })

        tasksSearchClearButton.onClick {
            tasksViewModel.clickClearButton()
        }

        tasksViewModel.endTurnDataResponse().observe(viewLifecycleOwner, Observer { })

        initRecyclerView()
    }

    private fun initToolbar() {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
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
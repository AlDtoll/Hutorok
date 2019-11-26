package com.example.hutorok

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.ext.replaceFragment
import com.example.hutorok.screen.builds_screen.BuildsScreen
import com.example.hutorok.screen.history_screen.HistoryScreen
import com.example.hutorok.screen.start_screen.StartScreen
import com.example.hutorok.screen.tasks_screen.TasksScreen
import com.example.hutorok.screen.worker_info_screen.WorkerInfoScreen
import com.example.hutorok.screen.workers_screen.WorkersScreen
import org.json.JSONObject
import org.koin.android.ext.android.inject
import java.io.File


class MainActivity : AppCompatActivity() {

    private val mainViewModel: IMainViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainViewModel.nowScreen().observe(this, Observer {
            when (it) {
                NowScreen.START_SCREEN -> showStartScreen()
                NowScreen.BUILDS_SCREEN -> showBuildsScreen()
                NowScreen.TASKS_SCREEN -> showTasksScreen()
                NowScreen.WORKERS_SCREEN -> showWorkersScreen()
                NowScreen.CLOSE_SCREEN -> finish()
                NowScreen.WORKER_INFO_SCREEN -> showWorkerInfoScreen()
                NowScreen.HISTORY_SCREEN -> showHistoryScreen()
            }
        })

        loadFromResources()

        mainViewModel.messageData().observe(this, Observer {
            it?.run {
                Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            mainViewModel.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        mainViewModel.onBackPressed()
    }

    private fun showStartScreen() {
        replaceFragment(StartScreen.newInstance())
    }

    private fun showWorkersScreen() {
        replaceFragment(WorkersScreen.newInstance())
    }

    private fun showWorkerInfoScreen() {
        replaceFragment(WorkerInfoScreen.newInstance())
    }

    private fun showTasksScreen() {
        replaceFragment(TasksScreen.newInstance())
    }

    private fun showBuildsScreen() {
        replaceFragment(BuildsScreen.newInstance())
    }

    private fun showHistoryScreen() {
        replaceFragment(HistoryScreen.newInstance())
    }

    private fun loadFromResources() {
        val isWorkersFilePresent = isFilePresent(this, "currentworkers.json")
        val workersText = if (isWorkersFilePresent) {
            read("currentworkers.json")
        } else {
            resources.openRawResource(R.raw.workers)
                .bufferedReader().use { it.readText() }
        }
        val workers = mutableListOf<Worker>()
        val workersObject = JSONObject(workersText)
        val workersArray = workersObject.getJSONArray("workers")
        for (i in 0 until workersArray.length()) {
            workers.add(Worker(workersArray.getJSONObject(i)))
        }

        val tasksText = resources.openRawResource(R.raw.tasks)
            .bufferedReader().use { it.readText() }
        val tasks = mutableListOf<Task>()
        val tasksObject = JSONObject(tasksText)
        val tasksArray = tasksObject.getJSONArray("tasks")
        for (i in 0 until tasksArray.length()) {
            tasks.add(Task(tasksArray.getJSONObject(i)))
        }

        val isHutorFilePresent = isFilePresent(this, "currenthutorok.json")

        val hutorokText = if (isHutorFilePresent) {
            read("currenthutorok.json")
        } else {
            resources.openRawResource(R.raw.hutorok)
                .bufferedReader().use { it.readText() }
        }
        val hutorokStatuses = mutableListOf<Status>()
        val hutorokStatusObject = JSONObject(hutorokText)
        val statusArray = hutorokStatusObject.getJSONArray("statuses")
        for (i in 0 until statusArray.length()) {
            hutorokStatuses.add(Status(statusArray.getJSONObject(i)))
        }

        val endTaskText = resources.openRawResource(R.raw.endtasks)
            .bufferedReader().use { it.readText() }
        val endTasks = mutableListOf<Task>()
        val endTasksObject = JSONObject(endTaskText)
        val endTasksArray = endTasksObject.getJSONArray("tasks")
        for (i in 0 until endTasksArray.length()) {
            endTasks.add(Task(endTasksArray.getJSONObject(i)))
        }

        val isHistoryFilePresent = isFilePresent(this, "history.json")
        val historyText = if (isHistoryFilePresent) {
            read("history.json")
        } else {
            resources.openRawResource(R.raw.start)
                .bufferedReader().use { it.readText() }
        }
        val events = mutableListOf<String>()
        val historyObject = JSONObject(
            if (historyText.isEmpty()) {
                resources.openRawResource(R.raw.start)
                    .bufferedReader().use { it.readText() }
            } else {
                historyText
            })
        val eventsArray = historyObject.getJSONArray("events")
        val turn = historyObject.getInt("turn")
        for (i in 0 until eventsArray.length()) {
            events.add(eventsArray.getString(i))
        }

        mainViewModel.loadData(workers, tasks, hutorokStatuses, endTasks, events, turn)
    }

    private fun isFilePresent(context: Context, fileName: String): Boolean {
        val path = context.filesDir.absolutePath + "/" + fileName
        val file = File(path)
        return file.exists()
    }

    private fun read(fileName: String): String {
        var string = ""
        val path = App.instance.filesDir.absolutePath + "/" + fileName
        File(path).bufferedReader().readLines().forEach {
            string += it
        }
        return string
    }

}

package com.example.hutorok

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.hutorok.ext.replaceFragment
import com.example.hutorok.screen.builds_screen.BuildsScreen
import com.example.hutorok.screen.start_screen.StartScreen
import com.example.hutorok.screen.tasks_screen.TasksScreen
import com.example.hutorok.screen.worker_info_screen.WorkerInfoScreen
import com.example.hutorok.screen.workers_screen.WorkersScreen
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val mainViewModel: IMainViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolbar()
        mainViewModel.nowScreen().observe(this, Observer {
            when (it) {
                NowScreen.START_SCREEN -> showStartScreen()
                NowScreen.BUILDS_SCREEN -> showBuildsScreen()
                NowScreen.TASKS_SCREEN -> showTasksScreen()
                NowScreen.WORKERS_SCREEN -> showWorkersScreen()
                NowScreen.CLOSE_SCREEN -> finish()
                NowScreen.WORKER_INFO_SCREEN -> showWorkerInfoScreen()
            }
        })

        mainViewModel.loadData()

        mainViewModel.messageData().observe(this, Observer {
            it?.run {
                Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) mainViewModel.onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        mainViewModel.onBackPressed()
    }

    private fun initToolbar() {
//        setSupportActionBar(toolbar)
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


}

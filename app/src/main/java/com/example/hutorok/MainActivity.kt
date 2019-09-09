package com.example.hutorok

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.hutorok.ext.replaceFragment
import com.example.hutorok.screen.start.StartScreen
import com.example.hutorok.screen.worker_info.WorkerInfoScreen
import com.example.hutorok.screen.workers.WorkersScreen
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
                NowScreen.BUILDS_SCREEN -> TODO()
                NowScreen.TASKS_SCREEN -> TODO()
                NowScreen.WORKERS_SCREEN -> showWorkersScreen()
                NowScreen.CLOSE_SCREEN -> finish()
                NowScreen.WORKER_INFO_SCREEN -> showWorkerInfoScreen()
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
}

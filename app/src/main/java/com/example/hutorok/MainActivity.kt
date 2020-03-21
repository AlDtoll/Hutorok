package com.example.hutorok

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.hutorok.ext.replaceFragment
import com.example.hutorok.screen.builds_screen.BuildsScreen
import com.example.hutorok.screen.finish_screen.FinishScreen
import com.example.hutorok.screen.history_screen.HistoryScreen
import com.example.hutorok.screen.quest_screen.QuestScreen
import com.example.hutorok.screen.start_screen.StartScreen
import com.example.hutorok.screen.tasks_screen.TasksScreen
import com.example.hutorok.screen.worker_info_screen.WorkerInfoScreen
import com.example.hutorok.screen.workers_screen.WorkersScreen
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    companion object {
        const val FIRST_RUN = "first_run"
        const val APP_PREFERENCES = "mysettings"
        const val PATH = "path"
    }

    private val mainViewModel: IMainViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createBottomNavigationMenu()
        mainViewModel.nowScreen().observe(this, Observer {
            when (it) {
                NowScreen.START_SCREEN -> showStartScreen()
                NowScreen.BUILDS_SCREEN -> showBuildsScreen()
                NowScreen.TASKS_SCREEN -> showTasksScreen()
                NowScreen.WORKERS_SCREEN -> showWorkersScreen()
                NowScreen.CLOSE_SCREEN -> closeAll()
                NowScreen.WORKER_INFO_SCREEN -> showWorkerInfoScreen()
                NowScreen.HISTORY_SCREEN -> showHistoryScreen()
                NowScreen.QUEST_SCREEN -> showQuestScreen()
                NowScreen.FINISH_SCREEN -> showFinishScreen()
            }
        })

        mainViewModel.loadDataResponse().observe(this, Observer { })

        val prefs = App.instance.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        prefs?.run {
            if (this.getBoolean(FIRST_RUN, true)) {
                mainViewModel.showAdventures()
            }
        }

        mainViewModel.messageData().observe(this, Observer {
            it?.run {
                if (it.trim().isNotEmpty()) {
                    Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show()
                }
            }
        })

        mainViewModel.turnNumberData().observe(this, Observer {
            it?.run {
                val turnNumberText = "Ход: $it"
                turnNumber.text = turnNumberText
            }
        })

        //todo можно ли так оставить?
        mainViewModel.getNavigationElementsVisibility().observe(this, Observer {
            turnNumber.visibility =
                if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        mainViewModel.clickAction(item)
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        mainViewModel.onBackPressed()
    }

    private fun createBottomNavigationMenu() {
        bottom_navigation.itemIconTintList = null
        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            mainViewModel.clickAction(item)
            true
        }

        mainViewModel.getNavigationElementsVisibility().observe(this, Observer {
            bottom_navigation.visibility =
                if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        })
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

    private fun showQuestScreen() {
        replaceFragment(QuestScreen.newInstance())
    }

    private fun showFinishScreen() {
        replaceFragment(FinishScreen.newInstance())
    }

    private fun closeAll() {
        mainViewModel.onClose()
        finish()
    }

}

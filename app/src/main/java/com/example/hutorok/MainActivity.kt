package com.example.hutorok

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.hutorok.ext.replaceFragment
import com.example.hutorok.screen.StartScreen
import kotlinx.android.synthetic.main.activity_main.*
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
                NowScreen.WORKERS_SCREEN -> TODO()
                NowScreen.CLOSE_SCREEN -> finish()
            }
        })
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
    }

    private fun showStartScreen() {
        replaceFragment(StartScreen.newInstance())
    }
}

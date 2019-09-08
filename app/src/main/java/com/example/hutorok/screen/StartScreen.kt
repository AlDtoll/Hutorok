package com.example.hutorok.screen

import androidx.fragment.app.Fragment
import org.koin.android.ext.android.inject

class StartScreen : Fragment() {

    private val startViewModel: IStartViewModel by inject()

    companion object {
        fun newInstance(): StartScreen = StartScreen()
    }
}
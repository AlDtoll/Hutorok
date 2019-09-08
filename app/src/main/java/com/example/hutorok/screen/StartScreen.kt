package com.example.hutorok.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.hutorok.R
import org.koin.android.ext.android.inject

class StartScreen : Fragment() {

    private val startViewModel: IStartViewModel by inject()

    companion object {
        fun newInstance(): StartScreen = StartScreen()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        initToolbar()

    }

    private fun initToolbar() {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).title = ""
    }
}
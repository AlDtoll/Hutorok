package com.example.hutorok.screen.start_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.hutorok.R
import com.example.hutorok.domain.model.Adventure
import com.example.hutorok.screen.AdventureAdapter
import kotlinx.android.synthetic.main.fragment_start.*
import org.koin.android.ext.android.inject

class StartScreen : Fragment() {

    private val startViewModel: IStartViewModel by inject()

    companion object {
        fun newInstance(): StartScreen = StartScreen()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        startViewModel.loadAdventuresData().observe(viewLifecycleOwner, Observer { })

        initToolbar()

        initRecyclerView()
    }

    private fun initToolbar() {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).title = ""
    }

    private lateinit var adventureAdapter: AdventureAdapter

    private val callback = object : AdventureAdapter.Callback {

        override fun clickAdventure(adventure: Adventure) {
            startViewModel.clickAdventure(adventure)
        }
    }

    private fun initRecyclerView() {
        startViewModel.adventuresData().observe(this, Observer {
            it?.run {
                showData(it)
            }
        })
        adventureAdapter = AdventureAdapter(callback)
        questsList.adapter = adventureAdapter
    }

    private fun showData(quests: List<Adventure>) {
        adventureAdapter.items = ArrayList(quests)
    }
}
package com.example.hutorok.screen.builds_screen

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
import com.example.hutorok.domain.model.Status
import com.example.hutorok.screen.StatusAdapter
import kotlinx.android.synthetic.main.fragment_builds.*
import org.koin.android.ext.android.inject

class BuildsScreen : Fragment() {

    private val buildsViewModel: IBuildsViewModel by inject()

    companion object {
        fun newInstance(): BuildsScreen = BuildsScreen()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_builds, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()

        buildsViewModel.statusesData().observe(this, Observer {
            it?.run {
                showStatus(it)
            }
        })
    }

    private fun initUi() {
        initToolbar()

        initRecyclerView()
    }

    private fun initToolbar() {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).title = getString(R.string.builds_screen_toolbar_title)
    }

    private lateinit var statusAdapter: StatusAdapter

    private fun initRecyclerView() {
        statusAdapter = StatusAdapter()
        statusesList.adapter = statusAdapter
        statusesList.itemAnimator = DefaultItemAnimator()
        context?.run {
            statusesList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }
    }

    private fun showStatus(statuses: List<Status>) {
        statusAdapter.items = ArrayList(statuses)
    }

}
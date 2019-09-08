package com.example.hutorok.screen.worker_info

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
import kotlinx.android.synthetic.main.fragment_workers.*
import org.koin.android.ext.android.inject

class WorkerInfoScreen : Fragment() {

    private val workerInfoViewModel: IWorkerInfoViewModel by inject()

    companion object {
        fun newInstance(): WorkerInfoScreen = WorkerInfoScreen()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()

        workerInfoViewModel.statusesData().observe(this, Observer {
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
        workerInfoViewModel.workerData().observe(this, Observer {
            it?.run {
                (activity as AppCompatActivity).title = it.name + " " + it.nickname + " " + it.age
            }
        })
    }

    private lateinit var statusAdapter: StatusAdapter

    private fun initRecyclerView() {
        statusAdapter = StatusAdapter()
        workersList.adapter = statusAdapter
        workersList.itemAnimator = DefaultItemAnimator()
        context?.run {
            workersList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }
    }

    private fun showStatus(statuses: List<Status>) {
        statusAdapter.items = ArrayList(statuses)
    }
}
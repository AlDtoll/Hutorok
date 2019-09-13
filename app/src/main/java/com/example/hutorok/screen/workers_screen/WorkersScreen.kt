package com.example.hutorok.screen.workers_screen

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
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.ext.onClick
import com.example.hutorok.screen.WorkerAdapter
import kotlinx.android.synthetic.main.fragment_workers.*
import org.koin.android.ext.android.inject

class WorkersScreen : Fragment() {

    private val workersViewModel: IWorkersViewModel by inject()

    companion object {
        fun newInstance(): WorkersScreen = WorkersScreen()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        initToolbar()

        workersViewModel.workersData().observe(this, Observer {
            workerAdapter.clear()
            it?.run {
                showWorkers(it)
            }
        })

        initRecyclerView()

        workersViewModel.isOrderScenario().observe(this, Observer {
            it?.run {
                if (it) {
                    workerAdapter.isOrder = true
                    executeTaskButton.visibility = View.VISIBLE
                } else {
                    workerAdapter.isOrder = false
                    executeTaskButton.visibility = View.GONE
                }
            }
        })

        executeTaskButton.onClick {
            workersViewModel.clickExecute()
        }
    }

    private fun initToolbar() {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).title = getString(R.string.workers_screen_toolbar_title)
    }

    private lateinit var workerAdapter: WorkerAdapter

    private val callback = object : WorkerAdapter.Callback {

        override fun selectWorker(worker: Worker) {
            workersViewModel.clickWorker(worker)
        }

        override fun clickCheckBox() {
            executeTaskButton.isEnabled = workerAdapter.items.any { worker -> worker.isChecked }
        }
    }

    private fun initRecyclerView() {
        workerAdapter = WorkerAdapter(callback)
        workersList.adapter = workerAdapter
        workersList.itemAnimator = DefaultItemAnimator()
        context?.run {
            workersList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }
    }

    private fun showWorkers(workers: List<Worker>) {
        workerAdapter.items = ArrayList(workers)
    }

}
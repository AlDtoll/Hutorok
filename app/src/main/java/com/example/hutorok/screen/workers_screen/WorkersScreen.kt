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
import com.example.hutorok.domain.model.Task
import com.example.hutorok.domain.model.Worker
import com.example.hutorok.ext.onClick
import com.example.hutorok.screen.WorkerAdapter
import kotlinx.android.synthetic.main.fragment_workers.*
import org.koin.android.ext.android.inject

class WorkersScreen : Fragment() {

    private val workersViewModel: IWorkersViewModel by inject()
    private var isOrder = false

    companion object {
        fun newInstance(): WorkersScreen = WorkersScreen()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                isOrder = it
                if (it) {
                    workerAdapter.isOrder = true
                    executeTaskButton.visibility = View.VISIBLE
                } else {
                    workerAdapter.isOrder = false
                    executeTaskButton.visibility = View.GONE
                }
                workersViewModel.checkExecuteButton()
            }
        })

        executeTaskButton.onClick {
            workersViewModel.clickExecute()
            //todo crunch на самом деле нужно определять доступность кнопки, не через callback
//            activity?.onBackPressed()
            workerAdapter.notifyDataSetChanged()
        }

        workersViewModel.importantStatusesData().observe(this, Observer {
            it?.run {
                workerAdapter.importantStatusNames = it
            }
        })

        workersViewModel.taskData().observe(this, Observer {
            it?.run {
                workerAdapter.task = it
                if (isOrder) {
                    changeToolbarTitle(it.name)
                    (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
                changeExecuteButtonText(it.type)
            }
        })

        workersViewModel.isExecuteTaskButtonEnable().observe(this, Observer {
            it?.run {
                executeTaskButton.isEnabled = it
            }
        })

        workersViewModel.generalDisableStatus().observe(this, Observer {
            it?.run {
                workerAdapter.generalDisableConditions = it
            }
        })

        workersViewModel.isExecuteButtonHintVisible().observe(viewLifecycleOwner, Observer {
            workersScreenExecuteButtonHint.visibility = if (it) View.VISIBLE else View.GONE
        })

        workersViewModel.executeTaskDataResponse().observe(viewLifecycleOwner, Observer { })
    }

    private fun initToolbar() {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).title = getString(R.string.workers_screen_toolbar_title)
    }

    private fun changeToolbarTitle(name: String) {
        (activity as AppCompatActivity).title = name
    }

    private lateinit var workerAdapter: WorkerAdapter

    private val callback = object : WorkerAdapter.Callback {

        override fun clickWorker(worker: Worker) {
            workersViewModel.clickWorker(worker)
        }

        override fun selectWorker() {
            workersViewModel.checkExecuteButton()
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
        Task.deselectAll(workers)
    }


    private fun changeExecuteButtonText(type: Task.Type) {
        executeTaskButton.text = when (type) {
            Task.Type.WORK -> getString(R.string.execute_task_button_text)
            Task.Type.BUILD -> getString(R.string.execute_task_button_text_without_worker)
            Task.Type.PERSON -> getString(R.string.execute_task_button_text_for_one_worker)
            Task.Type.PERSONAL_JOB -> getString(R.string.execute_task_button_text_for_one_worker)
            Task.Type.MASTER_SLAVE_JOB -> getString(R.string.execute_task_button_text_for_two_worker)
        }
    }

}
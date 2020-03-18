package com.example.hutorok.screen.worker_info_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hutorok.R
import com.example.hutorok.domain.model.Status
import com.example.hutorok.screen.StatusAdapter
import kotlinx.android.synthetic.main.fragment_worker_info.*
import org.koin.android.ext.android.inject
import kotlin.math.abs

class WorkerInfoScreen : Fragment(), View.OnTouchListener {

    private val workerInfoViewModel: IWorkerInfoViewModel by inject()

    companion object {
        const val MIN_DISTANCE = 100
        fun newInstance(): WorkerInfoScreen = WorkerInfoScreen()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_worker_info, container, false)
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

    private var downX: Float = 0f
    private var upX: Float = 0f

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        view.performClick()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
            }
            MotionEvent.ACTION_UP -> {
                upX = event.x
                swipe()
            }
        }
        return false
    }

    private fun swipe() {
        if (abs(downX - upX) > MIN_DISTANCE) {
            if (downX < upX) {
                screen.progress = 0f
                screen.transitionToState(R.id.right)
            } else {
                screen.progress = 0f
                screen.transitionToState(R.id.left)
            }
        }
    }

    private fun initUi() {

        screen.setTransitionListener(object : TransitionAdapter() {

            override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                when (currentId) {
                    R.id.offScreenRight -> {
                        motionLayout.progress = 0f
                        motionLayout.setTransition(R.id.start, R.id.right)
                        topCard.scaleX = 1f
                        topCard.scaleY = 1f
                        workerInfoViewModel.getPreviousWorker()
                    }
                    R.id.offScreenLeft -> {
                        motionLayout.progress = 0f
                        motionLayout.setTransition(R.id.start, R.id.left)
                        topCard.scaleX = 1f
                        topCard.scaleY = 1f
                        workerInfoViewModel.getNextWorker()
                    }
                }
            }

        })

        initToolbar()

        initRecyclerView()
    }

    private fun initToolbar() {
        workerInfoViewModel.workerData().observe(this, Observer {
            it?.run {
                (activity as AppCompatActivity).title =
                    it.name + " " + it.nickname + " " + it.age.code
            }
        })
    }

    private lateinit var statusAdapter: StatusAdapter

    private fun initRecyclerView() {
        statusAdapter = StatusAdapter()
        statusesList.adapter = statusAdapter
        statusesList.itemAnimator = DefaultItemAnimator()
        context?.run {
            statusesList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }

        statusesList.setOnTouchListener(this)
    }

    private fun showStatus(statuses: List<Status>) {
        statusAdapter.items = ArrayList(statuses)
    }
}
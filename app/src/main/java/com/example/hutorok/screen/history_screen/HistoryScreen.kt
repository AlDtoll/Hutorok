package com.example.hutorok.screen.history_screen

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
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
import com.example.hutorok.ext.addTextWatcher
import com.example.hutorok.ext.onClick
import com.example.hutorok.screen.HistoryAdapter
import kotlinx.android.synthetic.main.fragment_history.*
import org.koin.android.ext.android.inject
import java.util.*
import kotlin.collections.ArrayList

class HistoryScreen : Fragment() {

    private val historyViewModel: IHistoryViewModel by inject()

    companion object {
        fun newInstance(): HistoryScreen = HistoryScreen()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                10 -> {
                    val stringArrayListExtra =
                        data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    stringArrayListExtra?.run {
                        if (this.isNotEmpty()) {
                            historyViewModel.searchChange(this[0])
                        }
                    }

                }
            }
        }
    }

    private fun initUi() {
        historyViewModel.searchChange("")

        initToolbar()

        historyViewModel.historyData().observe(this, Observer {
            it?.run {
                showHistory(it)
            }
        })

        search.addTextWatcher { search, _, _, _ ->
            historyViewModel.searchChange(search.toString())
        }

        historyViewModel.searchData().observe(this, Observer {
            it?.run {
                if (it != search.text.toString()) {
                    search.setText(it)
                    search.setSelection(it.length)
                }
                if (it.isNotEmpty()) {
                    clearButton.visibility = View.VISIBLE
                } else {
                    clearButton.visibility = View.GONE
                }
            }
        })

        clearButton.onClick {
            historyViewModel.clickClearButton()
        }

        voiceButton.onClick {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
            startActivityForResult(intent, 10)
        }

        initRecyclerView()
    }

    private fun initToolbar() {
        (activity as AppCompatActivity).title = getString(R.string.history_screen_toolbar_title)
    }

    private lateinit var historyAdapter: HistoryAdapter

    private fun initRecyclerView() {
        historyAdapter = HistoryAdapter()
        historyList.adapter = historyAdapter
        historyList.itemAnimator = DefaultItemAnimator()
        context?.run {
            historyList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }
    }

    private fun showHistory(statuses: List<String>) {
        historyAdapter.items = ArrayList(statuses)
    }
}
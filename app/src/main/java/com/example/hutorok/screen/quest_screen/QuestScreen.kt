package com.example.hutorok.screen.quest_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.hutorok.R
import com.example.hutorok.domain.model.Scene
import com.example.hutorok.domain.model.Select
import com.example.hutorok.screen.SelectAdapter
import kotlinx.android.synthetic.main.fragment_quest.*
import org.koin.android.ext.android.inject

class QuestScreen : Fragment() {

    companion object {
        fun newInstance(): QuestScreen = QuestScreen()
    }

    private val questViewModel: IQuestViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quest, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        questViewModel.sceneData().observe(this, Observer {
            it?.run {
                showData(it)
            }
        })

        questScreenEndButton.setOnClickListener {
            questViewModel.clickEndButton()
        }

        questViewModel.previousSelectResultData().observe(this, Observer {
            it?.run {
                if (it.trim().isEmpty()) {
                    previousSceneSelectResultText.visibility = View.GONE
                } else {
                    previousSceneSelectResultText.visibility = View.VISIBLE
                    previousSceneSelectResultText.text = it
                }
            }
        })

        questViewModel.executeTaskDataResponse().observe(viewLifecycleOwner, Observer { })

        initRecyclerView()
    }

    private lateinit var selectAdapter: SelectAdapter

    private val callback = object : SelectAdapter.Callback {

        override fun clickSelect(select: Select) {
            questViewModel.clickSelect(select)
        }
    }

    private fun initRecyclerView() {
        selectAdapter = SelectAdapter(callback)
        questScreenSelectsList.adapter = selectAdapter
    }

    private fun showData(scene: Scene) {
        if (scene.type == Scene.Type.TECHNICAL) {
            questViewModel.clickSelect(scene.selects[0])
        } else {
            questSceneText.text = scene.text
            selectAdapter.items = ArrayList(scene.selects)
            if (scene.type == Scene.Type.END) {
                questScreenSelectsList.visibility = View.GONE
                questScreenEndButton.visibility = View.VISIBLE
            } else {
                questScreenSelectsList.visibility = View.VISIBLE
                questScreenEndButton.visibility = View.GONE
            }
        }
    }
}
package com.example.hutorok.screen.quest_screen

import androidx.lifecycle.LiveData
import com.example.hutorok.domain.model.Scene
import com.example.hutorok.domain.model.Select

interface IQuestViewModel {

    fun sceneData(): LiveData<Scene>

    fun clickSelect(select: Select)

    fun clickEndButton()

}
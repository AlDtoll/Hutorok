package com.example.hutorok.screen.start_screen

import androidx.lifecycle.LiveData
import com.example.hutorok.domain.model.Adventure

interface IStartViewModel {

    fun clickAdventure(adventure: Adventure)

    fun adventuresData(): LiveData<List<Adventure>>

    fun loadAdventuresData(): LiveData<Unit>
}
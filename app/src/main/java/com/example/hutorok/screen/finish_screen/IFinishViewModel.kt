package com.example.hutorok.screen.finish_screen

import androidx.lifecycle.LiveData
import com.example.hutorok.domain.model.Status

interface IFinishViewModel {

    fun clickEndButton()

    fun finishData(): LiveData<Status>

}
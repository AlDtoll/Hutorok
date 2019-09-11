package com.example.hutorok.screen.builds_screen

import androidx.lifecycle.LiveData
import com.example.hutorok.domain.model.Status

interface IBuildsViewModel {

    fun statusesData(): LiveData<List<Status>>

}
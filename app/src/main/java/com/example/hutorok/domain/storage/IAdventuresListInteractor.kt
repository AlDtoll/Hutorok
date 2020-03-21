package com.example.hutorok.domain.storage

import com.example.hutorok.domain.model.Adventure
import io.reactivex.Observable

interface IAdventuresListInteractor {

    fun update(adventures: List<Adventure>)

    fun get(): Observable<List<Adventure>>
}
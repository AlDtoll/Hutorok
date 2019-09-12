package com.example.hutorok.domain

import com.example.hutorok.domain.model.Status
import com.example.hutorok.domain.storage.IHutorStatusesListInteractor

class MockExecuteTaskInteractor(
    private val hutorStatusesListInteractor: IHutorStatusesListInteractor
) : IExecuteTaskInteractor {

    override fun execute() {
        hutorStatusesListInteractor.add(
            Status(
                "houseBuild",
                "Свежий жилой дом",
                "Тут живут люди",
                1.0,
                true
            )
        )
    }

}
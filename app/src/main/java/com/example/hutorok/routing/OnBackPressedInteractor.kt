package com.example.hutorok.routing

class OnBackPressedInteractor(
    private val router: IRouter
) {

    fun execute() {
        router.onBackPressed()
    }
}
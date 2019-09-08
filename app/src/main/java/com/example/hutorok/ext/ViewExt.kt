package com.example.hutorok.ext

import android.view.View

var lastClickTime: Long = 0L

fun View.onClick(action: () -> Unit) {
    setOnClickListener {
        if (System.currentTimeMillis() - lastClickTime > 250) {
            lastClickTime = System.currentTimeMillis()
            action()
        }
    }
}
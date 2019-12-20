package com.example.hutorok.screen.finish_screen

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.hutorok.R
import kotlinx.android.synthetic.main.fragment_finish.*
import org.koin.android.ext.android.inject
import kotlin.system.exitProcess

class FinishScreen : Fragment() {

    private val finishViewModel: IFinishViewModel by inject()

    companion object {
        fun newInstance(): FinishScreen = FinishScreen()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_finish, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        finishViewModel.finishData().observe(this, Observer {
            finishScreenGameResultText.text = it.name
            finishScreenGameResultDescriptionText.text = it.description
        })

        finishScreenEndButton.setOnClickListener {
            activity?.run {
                finishViewModel.clickEndButton()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    this.finishAndRemoveTask()
                } else {
                    this.finishAffinity()
                }
                exitProcess(0)
            }
        }
    }
}
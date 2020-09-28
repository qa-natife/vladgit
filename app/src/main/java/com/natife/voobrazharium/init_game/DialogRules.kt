package com.natife.voobrazharium.init_game

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Html
import android.text.Html.FROM_HTML_MODE_COMPACT
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import com.natife.voobrazharium.R
import com.natife.voobrazharium.utils.audio.AudioUtil

class DialogRules : DialogFragment() {

    private lateinit var audio: AudioUtil

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viev = inflater.inflate(R.layout.dialog_rules, null)

        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        audio = AudioUtil.getInstance()

        val text:TextView = viev.findViewById<TextView>(R.id.textRules)
        text.movementMethod = ScrollingMovementMethod()
        text.text = getString(R.string.rules_text)

        viev.findViewById<View>(R.id.backRules).setOnClickListener{
            audio.soundClickPlayer(dialog.context)
            dismiss()
        }
        return viev
    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}

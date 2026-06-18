package com.example.worldkids.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class SpeechHelper(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var ready = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            ready = tts?.setLanguage(Locale.FRENCH) == TextToSpeech.LANG_AVAILABLE ||
                tts?.setLanguage(Locale.getDefault()) == TextToSpeech.LANG_AVAILABLE
        }
    }

    fun speakCountryName(nameFr: String) {
        if (!ready) return
        tts?.speak(nameFr, TextToSpeech.QUEUE_FLUSH, null, "country-$nameFr")
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        ready = false
    }
}

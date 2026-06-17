package com.example.worldkids.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast

object CastUtils {

    fun openCastSettings(context: Context) {
        val intents = listOf(
            Intent(Settings.ACTION_CAST_SETTINGS),
            Intent("android.settings.CAST_SETTINGS"),
            Intent(Settings.ACTION_WIRELESS_SETTINGS),
            Intent(Settings.ACTION_SETTINGS)
        )
        for (intent in intents) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                Toast.makeText(
                    context,
                    "Choisis ta TV LG via Smart View / Cast, puis reviens dans l'app.",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
        }
        Toast.makeText(
            context,
            "Ouvre les réglages Android → Connexions → Smart View / Cast.",
            Toast.LENGTH_LONG
        ).show()
    }
}

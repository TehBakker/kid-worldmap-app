package com.example.worldkids.ui

import android.app.Activity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cast
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.worldkids.utils.CastUtils

@Composable
fun CastButton(
    modifier: Modifier = Modifier,
    tvMode: Boolean = false
) {
    val context = LocalContext.current
    if (tvMode) {
        TextButton(
            onClick = { CastUtils.openCastSettings(context) },
            modifier = modifier.heightIn(min = 48.dp),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(Icons.Rounded.Cast, contentDescription = "Cast", tint = MaterialTheme.colorScheme.primary)
            Text(" Cast", style = MaterialTheme.typography.titleMedium)
        }
    } else {
        IconButton(onClick = { CastUtils.openCastSettings(context) }, modifier = modifier) {
            Icon(Icons.Rounded.Cast, contentDescription = "Cast", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

fun setImmersiveMode(activity: Activity, enabled: Boolean) {
    val controller = androidx.core.view.WindowCompat.getInsetsController(activity.window, activity.window.decorView)
    if (enabled) {
        controller.hide(androidx.core.view.WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    } else {
        controller.show(androidx.core.view.WindowInsetsCompat.Type.systemBars())
    }
}

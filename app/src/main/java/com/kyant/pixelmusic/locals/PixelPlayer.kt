package com.kyant.pixelmusic.locals

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.kyant.pixelmusic.media.PixelPlayer

val LocalPixelPlayer = compositionLocalOf<PixelPlayer> { error("No PixelPlayer was provided.") }

@Composable
fun ProvidePixelPlayer(content: @Composable () -> Unit) {
    val player = PixelPlayer(LocalContext.current).apply {
        position = remember { Animatable(0f) }
        bufferedPositionState = remember { Animatable(0f) }
    }
    CompositionLocalProvider(LocalPixelPlayer provides player, content = content)
}
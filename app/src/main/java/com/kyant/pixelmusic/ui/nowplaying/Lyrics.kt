package com.kyant.pixelmusic.ui.nowplaying

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.animation.smoothScrollBy
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kyant.inimate.shape.SuperellipseCornerShape
import com.kyant.pixelmusic.api.Lyrics
import com.kyant.pixelmusic.locals.LocalPixelPlayer
import com.kyant.pixelmusic.util.currentIndex
import com.kyant.pixelmusic.util.isCurrentLine
import com.kyant.pixelmusic.util.toMilliseconds
import kotlin.math.absoluteValue
import kotlin.math.pow

@Composable
fun Lyrics(
    lyrics: Lyrics,
    dark: Boolean,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    val player = LocalPixelPlayer.current
    val state = rememberLazyListState()
    val positions = remember { mutableStateMapOf<Int, Float>() }
    val currentIndex = lyrics.currentIndex()
    val transition = updateTransition(currentIndex)
    LaunchedEffect(Unit) {
        state.snapToItemIndex((currentIndex - 1).coerceAtLeast(0))
    }
    LaunchedEffect(currentIndex) {
        state.smoothScrollBy(
            (positions.getOrDefault(currentIndex - 1, 0f) -
                    state.firstVisibleItemScrollOffset).absoluteValue,
            spring(stiffness = 100f)
        )
    }
    LazyColumn(
        modifier.fillMaxWidth(),
        state,
        contentPadding
    ) {
        itemsIndexed(lyrics.toList(), { _, lyric -> lyric.first }) { index, (time, lyric) ->
            val deltaIndex = ((index - currentIndex).absoluteValue).coerceAtMost(6)
            val isCurrentLine = time.isCurrentLine(lyrics.toMap())
            val offset = transition.animateDp({ spring(stiffness = 20f * (6 - deltaIndex)) }) {
                deltaIndex.toFloat().pow(2.5f).dp
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { positions[index] = it.positionInParent.y }
                    .offset(y = offset.value)
                    .clip(SuperellipseCornerShape(16.dp))
                    .clickable { player.snapTo(time.toMilliseconds() + 1) }
                    .padding(16.dp)
            ) {
                Text(
                    lyric,
                    Modifier.align(Alignment.CenterStart)
                        .alpha(
                            animateFloatAsState(
                                if (isCurrentLine) 1f
                                else (0.3f * (6 - deltaIndex) / 6).coerceAtLeast(0.1f)
                            ).value
                        ),
                   if (dark) Color.White else Color.Black,
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.h5.copy(
                        fontSize = MaterialTheme.typography.h5.fontSize * animateFloatAsState(if (isCurrentLine) 1.1f else 1f).value
                    )
                )
            }
        }
    }
}
package com.example.exoplayerdemo.screen

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.PlayerView
import com.example.exoplayerdemo.viewmodel.VideoPlayerViewModel
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun VideoDualPlayerScreen() {
    val context = LocalContext.current
    val viewModel: VideoPlayerViewModel = viewModel()

    val mainPlayer = remember { ExoPlayer.Builder(context).build() }
    val subPlayer = remember { ExoPlayer.Builder(context).build() }

    val hlsUrl = "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8"

    // メインプレーヤー設定
    LaunchedEffect(Unit) {
        val dataSourceFactory = DefaultDataSource.Factory(context)
        val mediaSource = HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(hlsUrl))
        mainPlayer.setMediaSource(mediaSource)
        mainPlayer.prepare()
        mainPlayer.playWhenReady = true
    }

    // サブプレーヤー設定
    LaunchedEffect(Unit) {
        val dataSourceFactory = DefaultDataSource.Factory(context)
        val mediaSource = HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(hlsUrl))
        subPlayer.setMediaSource(mediaSource)
        subPlayer.prepare()
        subPlayer.playWhenReady = true
    }

    Row(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
        ) {
            MainPlayerView(
                mainPlayer = mainPlayer,
                subPlayer = subPlayer,
                viewModel = viewModel
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            SubPlayerView(subPlayer)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mainPlayer.release()
            subPlayer.release()
        }
    }
}

@Composable
fun MainPlayerView(
    mainPlayer: ExoPlayer,
    subPlayer: ExoPlayer,
    viewModel: VideoPlayerViewModel
) {
    val context = LocalContext.current
    val positionState = remember { mutableLongStateOf(0L) }
    val position by positionState
    val isPlaying = viewModel.isPlaying

    LaunchedEffect(mainPlayer) {
        while (true) {
            positionState.longValue = mainPlayer.currentPosition
            delay(500)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    this.player = mainPlayer
                    // デフォルトのコントローラーオフ
                    useController = false
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    viewModel.togglePlayPause(mainPlayer, subPlayer)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                // todo 一時停止ボタンについてはデフォルトのものがなかったため一旦別のもの
                imageVector = if (isPlaying) Icons.Default.Person else Icons.Default.PlayArrow,
                contentDescription = "Play/Pause",
                tint = Color.White,
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .padding(12.dp)
            )
        }

        // todo シークバー修正必要
        Slider(
            value = if (mainPlayer.duration > 0) position / mainPlayer.duration.toFloat() else 0f,
            onValueChange = {
                val seekPos = (mainPlayer.duration * it).toLong()
                viewModel.seekTo(mainPlayer, subPlayer, seekPos)
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White
            )
        )
    }
}

@Composable
fun SubPlayerView(player: ExoPlayer) {
    AndroidView(factory = { context ->
        PlayerView(context).apply {
            this.player = player
            useController = false
        }
    }, modifier = Modifier.fillMaxSize())
}

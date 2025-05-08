package com.example.exoplayerdemo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.ExoPlayer

class VideoPlayerViewModel : ViewModel() {
    var isPlaying by mutableStateOf(true)
        private set

    fun togglePlayPause(main: ExoPlayer, sub: ExoPlayer) {
        if (isPlaying) {
            main.pause()
            sub.pause()
            isPlaying = false
        } else {
            main.play()
            sub.play()
            isPlaying = true
        }
    }

    fun seekTo(main: ExoPlayer, sub: ExoPlayer, position: Long) {
        main.seekTo(position)
        sub.seekTo(position)
    }
}
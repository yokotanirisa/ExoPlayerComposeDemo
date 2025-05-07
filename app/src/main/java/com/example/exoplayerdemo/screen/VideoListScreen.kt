package com.example.exoplayerdemo.screen

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun VideoListScreen(navController: NavController) {
    val videoList = listOf(
        "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
        "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
    )

    Column {
        Text("再生する動画を選んでください")
        videoList.forEach { url ->
            Button(onClick = {
                navController.navigate("video?uri=${Uri.encode(url)}")
            }) {
                Text("再生: ${url.substringAfterLast("/")}")
            }
        }
    }
}

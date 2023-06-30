package com.lutortech.familyfundtime.ui

import android.net.Uri
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.FixedScale
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfilePic(picUrl: Uri?, modifier: Modifier = Modifier) {
    GlideImage(
        model = picUrl,
        contentDescription = "Profile Pic",
        modifier = modifier.clip(CircleShape),
        contentScale = FixedScale(0.1f)
    )
}
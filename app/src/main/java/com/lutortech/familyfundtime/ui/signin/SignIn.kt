package com.lutortech.familyfundtime.ui.signin

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FixedScale
import androidx.compose.ui.unit.Dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.lutortech.familyfundtime.Constants.LOG_TAG
import com.lutortech.familyfundtime.ui.ProfilePic

@Composable
fun SignIn(signInViewModel: SignInViewModel, modifier: Modifier = Modifier) {

    // remembered state
    val user by remember { signInViewModel.user }
    val isSignedIn = user != null

    Surface(
        modifier = modifier
            .border(width = Dp(1f), color = Color.Cyan)
            .padding(Dp(5f))
    ) {
        if (isSignedIn) {
            Row{
                SignOutButton(
                    onClick = { signInViewModel.sendEvent(SignInEvent.USER_EVENT_SIGN_OUT) },
                    modifier = modifier
                )
                Text(user?.displayName ?: "NO USER", modifier = modifier, color = Color.Cyan)
                ProfilePic(picUrl = user?.profilePicUrl, modifier = modifier)
            }
        } else {
            SignInButton(
                { signInViewModel.sendEvent(SignInEvent.USER_EVENT_LAUNCH_SIGN_IN) },
                modifier.fillMaxWidth()
            )
        }
    }
}

private fun SignInViewModel.sendEvent(event: SignInEvent) {
    Log.d(LOG_TAG, "Sending event [${event.name}]")
    onEvent(event)
}

@Composable
private fun SignInButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = onClick, modifier = modifier) {
        Text("Sign In!")
    }
}

@Composable
private fun SignOutButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = onClick, modifier = modifier) {
        Text("Sign Out!")
    }
}
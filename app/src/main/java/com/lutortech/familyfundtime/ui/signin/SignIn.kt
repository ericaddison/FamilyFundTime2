package com.lutortech.familyfundtime.ui.signin

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.lutortech.familyfundtime.Constants.LOG_TAG

@Composable
fun SignIn(signInViewModel: SignInViewModel, modifier: Modifier = Modifier) {

    // remembered state
    val user by remember { signInViewModel.user }
    val isSignedIn = user != null

    if (isSignedIn) {
        Row(
            modifier = modifier
        ) {
            SignOutButton(
                onClick = { signInViewModel.sendEvent(SignInEvent.USER_EVENT_SIGN_OUT) },
                modifier = modifier
            )
            Text(user?.displayName ?: "NO USER", modifier = modifier, color = Color.Cyan)
            ProfilePic(picUrl = user?.profilePicUrl)
        }
    } else {
        SignInButton(
            { signInViewModel.sendEvent(SignInEvent.USER_EVENT_LAUNCH_SIGN_IN) },
            modifier
        )
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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ProfilePic(picUrl: Uri?, modifier: Modifier = Modifier) {
    GlideImage(
        model = picUrl,
        contentDescription = "Profile Pic",
        modifier = modifier.clip(CircleShape)
    )
}
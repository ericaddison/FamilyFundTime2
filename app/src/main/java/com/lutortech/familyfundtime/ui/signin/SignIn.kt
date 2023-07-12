package com.lutortech.familyfundtime.ui.signin

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.lutortech.familyfundtime.Constants.LOG_TAG
import com.lutortech.familyfundtime.ui.ProfilePic

@Composable
fun SignIn(viewModel: SignInViewModel, modifier: Modifier = Modifier) {

    // remembered state
    val user by viewModel.user.collectAsState()
    val isSignedIn by viewModel.isSignedIn.collectAsState()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        val elementModifier = modifier.fillMaxHeight()
        if (isSignedIn) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = elementModifier
            ) {
                SignOutButton(
                    onClick = { viewModel.sendEvent(SignInEvent.USER_EVENT_SIGN_OUT) },
                    modifier = elementModifier.weight(3f)
                )
                Box(modifier = elementModifier.weight(5f), contentAlignment = Alignment.Center) {
                    Text(
                        user?.displayName ?: "NO USER",
                        modifier = Modifier,
                        color = Color.Cyan,
                        textAlign = TextAlign.Center
                    )
                }
                ProfilePic(picUrl = user?.profilePicUrl, modifier = elementModifier.weight(2f))
            }
        } else {
            SignInButton(
                { viewModel.sendEvent(SignInEvent.USER_EVENT_LAUNCH_SIGN_IN) },
                elementModifier
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
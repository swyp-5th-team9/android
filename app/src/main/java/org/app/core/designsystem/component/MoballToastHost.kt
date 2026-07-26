package org.app.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.delay

private const val TOAST_DURATION_MS = 2000L
private const val TOAST_ANIM_MS = 180

@Immutable
data class MoballToastData(
    val message: String,
    @DrawableRes val leadingIconRes: Int?,
)

@Stable
class MoballToastHostState {
    var current by mutableStateOf<MoballToastData?>(null)
        private set

    fun show(
        message: String,
        @DrawableRes leadingIconRes: Int? = null,
    ) {
        current = MoballToastData(message = message, leadingIconRes = leadingIconRes)
    }

    fun dismiss() {
        current = null
    }
}

val LocalMoballToastHostState = staticCompositionLocalOf<MoballToastHostState> {
    error("MoballToastHostState is not provided")
}

@Composable
fun rememberMoballToastHostState(): MoballToastHostState = remember { MoballToastHostState() }

@Composable
fun MoballToastHost(
    hostState: MoballToastHostState,
    modifier: Modifier = Modifier,
) {
    val data = hostState.current

    var mounted by remember { mutableStateOf(false) }
    var lastData by remember { mutableStateOf<MoballToastData?>(null) }

    LaunchedEffect(data) {
        if (data != null) {
            lastData = data
            mounted = true
            delay(TOAST_DURATION_MS)
            hostState.dismiss()
        }
    }

    if (mounted) {
        Popup(
            alignment = Alignment.BottomCenter,
            properties = PopupProperties(focusable = false, clippingEnabled = false),
        ) {
            val transitionState = remember { MutableTransitionState(false) }
            transitionState.targetState = data != null

            LaunchedEffect(transitionState.isIdle, transitionState.currentState) {
                if (transitionState.isIdle && !transitionState.currentState) {
                    mounted = false
                    lastData = null
                }
            }

            AnimatedVisibility(
                visibleState = transitionState,
                enter = fadeIn(tween(TOAST_ANIM_MS)) +
                    slideInVertically(tween(TOAST_ANIM_MS)) { it / 3 },
                exit = fadeOut(tween(TOAST_ANIM_MS)) +
                    slideOutVertically(tween(TOAST_ANIM_MS)) { it / 3 },
            ) {
                lastData?.let { toast ->
                    MoballToast(
                        message = toast.message,
                        leadingIconRes = toast.leadingIconRes,
                        modifier = modifier
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 24.dp),
                    )
                }
            }
        }
    }
}

package org.app.presentation.onboarding.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moball.app.R
import kotlinx.coroutines.delay
import org.app.core.designsystem.theme.MoballTheme

private val splashBackground = Brush.verticalGradient(
    colorStops = arrayOf(
        0.0f to Color(0xFF31353B),
        0.56f to Color(0xFF31353B),
        1.0f to Color(0xFF1C1C1C),
    ),
)

private val SLOGAN_RES_LIST = listOf(
    R.drawable.img_splash_slogan1,
    R.drawable.img_splash_slogan2,
    R.drawable.img_splash_slogan3,
)

private const val SPLASH_DELAY_MS = 2000L

@Composable
fun SplashRoute(
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        delay(SPLASH_DELAY_MS)
        navigateToLogin()
    }
    SplashScreen(modifier = modifier)
}

@Composable
private fun SplashScreen(modifier: Modifier = Modifier) {
    val sloganRes = remember { SLOGAN_RES_LIST.random() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(splashBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(0.31f))

        Image(
            painter = painterResource(R.drawable.ic_moball_logo),
            contentDescription = null,
        )

        Spacer(Modifier.height(18.dp))

        Image(
            painter = painterResource(R.drawable.img_moball_text),
            contentDescription = null,
        )

        Spacer(Modifier.weight(0.20f))

        Image(
            painter = painterResource(R.drawable.img_splash_subtitle),
            contentDescription = null,
        )

        Spacer(Modifier.height(45.dp))

        Image(
            painter = painterResource(sloganRes),
            contentDescription = null,
        )

        Spacer(Modifier.weight(0.12f))
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    MoballTheme {
        SplashScreen()
    }
}

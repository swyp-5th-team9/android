package org.app.presentation.onboarding.signup.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.app.core.designsystem.theme.MoballTheme

/**
 * 온보딩 단계 표시 점(dot) 프로그레스바
 *
 * @param totalSteps  전체 단계 수
 * @param currentStep 현재 단계 (1부터 시작)
 */
@Composable
fun OnboardingProgressBar(
    totalSteps: Int,
    currentStep: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(totalSteps) { index ->
            val isActive = index + 1 == currentStep
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = if (isActive) MoballTheme.colors.borderActive else Color(0xFF9A9A9A),
                        shape = CircleShape,
                    ),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun OnboardingProgressBarStep1Preview() {
    MoballTheme {
        OnboardingProgressBar(totalSteps = 2, currentStep = 1)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun OnboardingProgressBarStep2Preview() {
    MoballTheme {
        OnboardingProgressBar(totalSteps = 2, currentStep = 2)
    }
}

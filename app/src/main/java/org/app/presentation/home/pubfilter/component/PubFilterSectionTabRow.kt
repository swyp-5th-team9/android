package org.app.presentation.home.pubfilter.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable

@Composable
fun PubFilterTabRow(
    tabs: List<String>,
    activeIndex: Int,
    onTabClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val indicatorPosition by animateFloatAsState(
        targetValue = activeIndex.toFloat(),
        label = "pubFilterTabIndicator",
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            tabs.forEachIndexed { idx, label ->
                val isActive = idx == activeIndex
                Text(
                    text = label,
                    style = if (isActive) {
                        MoballTheme.typography.body.semibold14
                    } else {
                        MoballTheme.typography.body.regular14
                    },
                    color = if (isActive) {
                        MoballTheme.colors.textPrimary
                    } else {
                        MoballTheme.colors.textTertiary
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .noRippleClickable { onTabClick(idx) }
                        .padding(vertical = 11.dp),
                )
            }
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp),
        ) {
            val tabWidth = maxWidth / tabs.size
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MoballTheme.colors.borderNormal),
            )
            Box(
                modifier = Modifier
                    .width(tabWidth)
                    .fillMaxHeight()
                    .offset(x = tabWidth * indicatorPosition)
                    .background(MoballTheme.colors.borderActive),
            )
        }
    }
}

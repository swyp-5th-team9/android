package org.app.presentation.mypage.report

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moball.app.R
import org.app.core.designsystem.component.MoballButton
import org.app.core.designsystem.component.UrlImage
import org.app.core.designsystem.component.textfield.MoballAreaTextField
import org.app.core.designsystem.component.topbar.MoballTopBar
import org.app.core.designsystem.component.topbar.TopBarState
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable
import org.app.presentation.mypage.report.component.ReportCategoryChip
import org.app.presentation.mypage.report.component.ReportSuccessDialog

@Composable
fun ReportRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                ReportContract.SideEffect.NavigateBack -> {
                    onBack()
                }

                ReportContract.SideEffect.ShowSuccessDialog -> {
                    showSuccessDialog = true
                }

                is ReportContract.SideEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    ReportScreen(
        state = state,
        onBack = onBack,
        onCategorySelected = viewModel::selectCategory,
        onDetailTextChange = viewModel::onDetailTextChange,
        onImagesPicked = viewModel::addScreenshots,
        onImageRemoved = viewModel::removeScreenshot,
        onSubmit = viewModel::submit,
        modifier = modifier,
    )

    // 제보 접수 완료 모달 (딤 + 팝업)
    if (showSuccessDialog) {
        ReportSuccessDialog(
            onConfirm = {
                showSuccessDialog = false
                onBack()
            },
        )
    }
}

@Composable
private fun ReportScreen(
    state: ReportContract.State,
    onBack: () -> Unit,
    onCategorySelected: (ReportCategory) -> Unit,
    onDetailTextChange: (String) -> Unit,
    onImagesPicked: (List<String>) -> Unit,
    onImageRemoved: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val detailTextState = rememberTextFieldState(state.detailText)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(3 - state.screenshots.size),
    ) { uris ->
        if (uris.isNotEmpty()) {
            onImagesPicked(uris.map { it.toString() })
        }
    }

    LaunchedEffect(detailTextState) {
        snapshotFlow { detailTextState.text.toString() }
            .collect { onDetailTextChange(it) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MoballTheme.colors.backgroundBase),
    ) {
        MoballTopBar(state = TopBarState.Back(title = "제보하기", onBackClick = onBack))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "어떤 문제인가요?",
                    style = MoballTheme.typography.heading3.semibold20,
                    color = MoballTheme.colors.textPrimary,
                )
                Text(
                    text = "*",
                    style = MoballTheme.typography.heading5.semibold18,
                    color = Color.Red,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ReportCategory.entries.forEach { category ->
                    ReportCategoryChip(
                        label = category.label,
                        isSelected = state.selectedCategory == category,
                        onClick = { onCategorySelected(category) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(33.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "자세히 알려주세요",
                    style = MoballTheme.typography.heading3.semibold20,
                    color = MoballTheme.colors.textPrimary,
                )
                Text(text = "*", style = MoballTheme.typography.heading5.semibold18, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(12.dp))

            MoballAreaTextField(
                state = detailTextState,
                placeholder = "예: 영업시간이 실제와 달라요.",
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(33.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = "스크린샷 첨부",
                    style = MoballTheme.typography.heading3.semibold20,
                    color = MoballTheme.colors.textPrimary,
                )
                Text(
                    text = "(선택)",
                    style = MoballTheme.typography.caption.regular12,
                    color = MoballTheme.colors.textSecondary,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.screenshots.size < 3) {
                    item {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .noRippleClickable {
                                    launcher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                                    )
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            val borderColor = MoballTheme.colors.borderStrong
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawRoundRect(
                                    color = borderColor,
                                    style = Stroke(
                                        width = 1.5.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(
                                            intervals = floatArrayOf(10f, 10f),
                                            phase = 0f,
                                        ),
                                    ),
                                    cornerRadius = androidx.compose.ui.geometry
                                        .CornerRadius(12.dp.toPx()),
                                )
                            }
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_camera),
                                contentDescription = null,
                                tint = Color.Unspecified,
                            )
                        }
                    }
                }

                items(state.screenshots) { uri ->
                    Box(
                        modifier = Modifier.size(60.dp),
                    ) {
                        UrlImage(
                            url = uri,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop,
                        )
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_close_circle),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 6.dp, y = (-6).dp)
                                .size(24.dp)
                                .noRippleClickable { onImageRemoved(uri) },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "10MB 이하의 이미지만 업로드 해주세요.\n최대 3장까지 등록 가능합니다.",
                style = MoballTheme.typography.caption.regular12,
                color = MoballTheme.colors.textTertiary,
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        MoballButton(
            text = "제보 보내기",
            onClick = onSubmit,
            // enabled = state.canSubmit && !state.isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun ReportScreenPreview() {
    MoballTheme {
        ReportScreen(
            state = ReportContract.State(),
            onBack = {},
            onCategorySelected = {},
            onDetailTextChange = {},
            onImagesPicked = {},
            onImageRemoved = {},
            onSubmit = {},
        )
    }
}

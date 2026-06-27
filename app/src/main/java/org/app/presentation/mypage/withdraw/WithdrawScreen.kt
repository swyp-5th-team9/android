package org.app.presentation.mypage.withdraw

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.app.core.designsystem.component.MoballButton
import org.app.core.designsystem.component.MoballDialog
import org.app.core.designsystem.component.textfield.MoballAreaTextField
import org.app.core.designsystem.component.topbar.MoballTopBar
import org.app.core.designsystem.component.topbar.TopBarState
import org.app.core.designsystem.theme.MoballTheme
import org.app.presentation.mypage.withdraw.component.WithdrawReasonItem

@Composable
fun WithdrawRoute(
    onBack: () -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WithdrawViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showSuccessDialog by remember { mutableStateOf(false) }

    val etcTextState = rememberTextFieldState(state.etcText)

    LaunchedEffect(etcTextState) {
        snapshotFlow { etcTextState.text.toString() }
            .collect { viewModel.onEvent(WithdrawContract.Event.OnEtcTextChanged(it)) }
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                WithdrawContract.SideEffect.NavigateToLogin -> {
                    navigateToLogin()
                }

                WithdrawContract.SideEffect.NavigateBack -> {
                    onBack()
                }

                WithdrawContract.SideEffect.ShowSuccessDialog -> {
                    showSuccessDialog = true
                }

                is WithdrawContract.SideEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    WithdrawScreen(
        state = state,
        etcTextState = etcTextState,
        showSuccessDialog = showSuccessDialog,
        onBack = onBack,
        onReasonSelected = { viewModel.onEvent(WithdrawContract.Event.OnReasonSelected(it)) },
        onAgreementToggle = { viewModel.onEvent(WithdrawContract.Event.OnAgreementToggle) },
        onWithdrawClick = { viewModel.onEvent(WithdrawContract.Event.OnWithdrawClick) },
        onConfirmDialog = {
            showSuccessDialog = false
            viewModel.onEvent(WithdrawContract.Event.OnWithdrawConfirm)
        },
        modifier = modifier,
    )
}

@Composable
private fun WithdrawScreen(
    state: WithdrawContract.State,
    etcTextState: TextFieldState,
    showSuccessDialog: Boolean,
    onBack: () -> Unit,
    onReasonSelected: (WithdrawReason) -> Unit,
    onAgreementToggle: () -> Unit,
    onWithdrawClick: () -> Unit,
    onConfirmDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MoballTheme.colors.backgroundBase),
    ) {
        MoballTopBar(state = TopBarState.Back(title = "회원탈퇴", onBackClick = onBack))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(27.dp))

            Text(
                text = "모여볼을 떠난다니\n너무 아쉬워요",
                style = MoballTheme.typography.heading1.extrabold26,
                color = MoballTheme.colors.textPrimary,
            )

            Spacer(modifier = Modifier.height(78.dp))

            Text(
                text = "탈퇴하시는 이유를 알려주세요.",
                style = MoballTheme.typography.heading3.semibold20,
                color = MoballTheme.colors.textPrimary,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
                WithdrawReason.entries.forEach { reason ->
                    WithdrawReasonItem(
                        label = reason.label,
                        isSelected = state.selectedReason == reason,
                        onClick = { onReasonSelected(reason) },
                    )
                    if (reason == WithdrawReason.ETC && state.selectedReason == WithdrawReason.ETC) {
                        Spacer(modifier = Modifier.height(10.dp))
                        MoballAreaTextField(
                            state = etcTextState,
                            placeholder = "예: 영업시간이 실제와 달라요.",
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAgreementToggle() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Checkbox(
                    checked = state.isAgreed,
                    onCheckedChange = { onAgreementToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MoballTheme.colors.accentPrimary,
                        checkmarkColor = MoballTheme.colors.textPrimary,
                        uncheckedColor = MoballTheme.colors.borderStrong,
                    ),
                )
                Text(
                    text = "탈퇴하면 모여볼 계정 이용이 종료돼요.",
                    style = MoballTheme.typography.body.medium14,
                    color = MoballTheme.colors.textPrimary,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            MoballButton(
                text = "탈퇴하기",
                onClick = onWithdrawClick,
                enabled = state.canWithdraw,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            )
        }
    }
    if (showSuccessDialog) {
        MoballDialog(
            title = "탈퇴가 완료되었어요.",
            subtitle = "이용해 주셔서 감사합니다.",
            onConfirm = onConfirmDialog,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WithdrawScreenPreview() {
    MoballTheme {
        WithdrawScreen(
            state = WithdrawContract.State(
                selectedReason = WithdrawReason.REASON_1,
                isAgreed = false,
            ),
            etcTextState = rememberTextFieldState(),
            showSuccessDialog = false,
            onBack = {},
            onReasonSelected = {},
            onAgreementToggle = {},
            onWithdrawClick = {},
            onConfirmDialog = {},
        )
    }
}

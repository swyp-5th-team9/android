package org.app.presentation.mypage.editprofile

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import org.app.core.designsystem.component.textfield.MoballLineTextField
import org.app.core.designsystem.component.topbar.MoballTopBar
import org.app.core.designsystem.component.topbar.TopBarState
import org.app.core.designsystem.theme.MoballTheme
import org.app.core.extension.noRippleClickable
import org.app.presentation.mypage.editprofile.component.EditProfileNicknameCard

@Composable
fun EditProfileRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val nicknameState = rememberTextFieldState(state.nickname)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            viewModel.onEvent(EditProfileContract.Event.OnProfileImageChange(uri.toString()))
        }
    }
    LaunchedEffect(nicknameState) {
        snapshotFlow { nicknameState.text.toString() }
            .collect { viewModel.onEvent(EditProfileContract.Event.OnNicknameChange(it)) }
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                EditProfileContract.SideEffect.NavigateBack -> {
                    onBack()
                }

                is EditProfileContract.SideEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    EditProfileScreen(
        state = state,
        nicknameState = nicknameState,
        onBack = onBack,
        onPickImage = {
            launcher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
        },
        onEditNicknameClick = { viewModel.onEvent(EditProfileContract.Event.OnEditNicknameClick) },
        onSave = { viewModel.onEvent(EditProfileContract.Event.OnSaveClick) },
        modifier = modifier,
    )
}

@Composable
private fun EditProfileScreen(
    state: EditProfileContract.State,
    nicknameState: TextFieldState,
    onBack: () -> Unit,
    onPickImage: () -> Unit,
    onEditNicknameClick: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MoballTheme.colors.backgroundBase),
    ) {
        MoballTopBar(state = TopBarState.Back(title = "내정보 수정", onBackClick = onBack))

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(31.dp))

            Box(
                modifier = Modifier.size(121.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MoballTheme.colors.backgroundSurface),
                    contentAlignment = Alignment.Center,
                ) {
                    UrlImage(
                        url = state.profileImageUrl.takeIf { !it.isNullOrBlank() } ?: R.drawable.img_profile,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .align(Alignment.TopEnd)
                        .noRippleClickable(onClick = onPickImage),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_edit_pencil),
                        contentDescription = null,
                        tint = Color.Unspecified,
                    )
                }
            }

            Spacer(modifier = Modifier.height(47.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "닉네임",
                    style = MoballTheme.typography.heading5.bold18,
                    color = MoballTheme.colors.textPrimary,
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (state.isEditingNickname) {
                    MoballLineTextField(
                        state = nicknameState,
                        placeholder = "사용할 닉네임을 적어주세요",
                        modifier = Modifier.fillMaxWidth(),
                        showCharCount = true,
                    )
                } else {
                    EditProfileNicknameCard(
                        nickname = state.nickname,
                        onEditClick = onEditNicknameClick,
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))

            if (state.isEditingNickname || state.hasChanged) {
                MoballButton(
                    text = "이걸로 할래요",
                    onClick = onSave,
                    enabled = state.hasChanged && !state.isLoading,
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditProfileScreenPreview() {
    MoballTheme {
        EditProfileScreen(
            state = EditProfileContract.State(
                originalNickname = "홍길동",
                nickname = "홍길동",
            ),
            nicknameState = rememberTextFieldState("홍길동"),
            onBack = {},
            onPickImage = {},
            onEditNicknameClick = {},
            onSave = {},
        )
    }
}

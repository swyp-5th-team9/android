package org.app.presentation.mypage

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import org.app.core.designsystem.theme.MoballTheme

@Composable
fun MyPageRoute(
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is MyPageContract.SideEffect.NavigateToLogin -> navigateToLogin()
                is MyPageContract.SideEffect.ShowToast -> {
                    Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    MyPageScreen(
        onLogoutClick = viewModel::logout,
        onWithdrawClick = viewModel::withdraw,
        modifier = modifier,
    )
}

@Composable
private fun MyPageScreen(
    onLogoutClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(width = 100.dp, height = 60.dp)
                .background(Color.Black)
                .clickable(onClick = onLogoutClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "로그아웃",
                color = Color.White,
                fontSize = 16.sp,
            )
        }

        Box(
            modifier = Modifier
                .size(width = 100.dp, height = 60.dp)
                .background(Color.Black)
                .clickable(onClick = onWithdrawClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "회원 탈퇴",
                color = Color.White,
                fontSize = 16.sp,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageScreenPreview() {
    MoballTheme {
        MyPageScreen(
            onLogoutClick = {},
            onWithdrawClick = {},
        )
    }
}

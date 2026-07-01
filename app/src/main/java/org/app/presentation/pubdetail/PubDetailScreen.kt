package org.app.presentation.pubdetail

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.app.core.designsystem.theme.MoballTheme
import org.app.presentation.pubdetail.component.PubBottomBar
import org.app.presentation.pubdetail.component.PubHeroCarousel
import org.app.presentation.pubdetail.component.PubInfoSection
import org.app.presentation.pubdetail.component.PubPhotoGallery
import org.app.presentation.pubdetail.component.PubPhotoSection
import org.app.presentation.pubdetail.model.BusinessHour
import org.app.presentation.pubdetail.model.KboTeam
import org.app.presentation.pubdetail.model.PubDetail
import org.app.presentation.pubdetail.model.PubStatus

@Composable
fun PubDetailRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PubDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is PubDetailContract.SideEffect.NavigateBack -> onBack()

                is PubDetailContract.SideEffect.CallPhone -> {
                    context.startActivity(
                        Intent(Intent.ACTION_DIAL, "tel:${effect.phoneNumber}".toUri()),
                    )
                }

                is PubDetailContract.SideEffect.OpenMap -> {
                    val appIntent = Intent(Intent.ACTION_VIEW, effect.url.toUri())
                    val webIntent = Intent(Intent.ACTION_VIEW, effect.webFallbackUrl.toUri())
                    try {
                        context.startActivity(appIntent)
                    } catch (_: Exception) {
                        context.startActivity(webIntent)
                    }
                }

                is PubDetailContract.SideEffect.ShowToast -> { /* TODO: Toast */ }
            }
        }
    }

    PubDetailScreen(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

@Composable
internal fun PubDetailScreen(
    state: PubDetailContract.State,
    onEvent: (PubDetailContract.Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (state.isLoading || state.pubDetail == null) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MoballTheme.colors.accentPrimary,
            )
            return@Box
        }

        val detail = state.pubDetail

        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                PubHeroCarousel(
                    imageUrls = detail.imageUrls,
                    currentPage = state.currentImageIndex,
                    onBack = { onEvent(PubDetailContract.Event.OnBack) },
                    onPageChanged = { onEvent(PubDetailContract.Event.OnImagePageChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                )

                PubInfoSection(
                    pubName = detail.name,
                    teams = detail.teams,
                    businessHours = detail.businessHours,
                    status = detail.status,
                    address = detail.address,
                    phoneNumber = detail.phoneNumber,
                    groupSeatMaxPeople = detail.groupSeatMaxPeople,
                    styleCodes = detail.styleCodes,
                    facilityCodes = detail.facilityCodes,
                    isHoursExpanded = state.isHoursExpanded,
                    onHoursToggle = { onEvent(PubDetailContract.Event.OnHoursToggle) },
                    onPhoneCall = { onEvent(PubDetailContract.Event.OnPhoneCall) },
                    isWished = detail.isWishlisted,
                    favoriteCount = detail.favoriteCount,
                    onWishToggle = { onEvent(PubDetailContract.Event.OnWishlistToggle) },
                )

                Spacer(modifier = Modifier.height(4.dp))
                Divider()
                Spacer(modifier = Modifier.height(20.dp))

                PubPhotoSection(
                    imageUrls = detail.imageUrls,
                    onPhotoClick = { index -> onEvent(PubDetailContract.Event.OnPhotoClick(index)) },
                )

                Spacer(modifier = Modifier.navigationBarsPadding())
            }

            PubBottomBar(
                hasPhoneNumber = detail.phoneNumber != null,
                onPhoneCall = { onEvent(PubDetailContract.Event.OnPhoneCall) },
                onKakaoMapClick = { onEvent(PubDetailContract.Event.OnKakaoMapClick) },
                onNaverMapClick = { onEvent(PubDetailContract.Event.OnNaverMapClick) },
            )
        }

        if (state.showPhotoGallery && detail.imageUrls.isNotEmpty()) {
            PubPhotoGallery(
                imageUrls = detail.imageUrls,
                initialPage = state.selectedPhotoIndex,
                onClose = { onEvent(PubDetailContract.Event.OnPhotoGalleryClose) },
                onPageChanged = { onEvent(PubDetailContract.Event.OnPhotoGalleryPageChanged(it)) },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun Divider() {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        color = MoballTheme.colors.borderNormal,
        thickness = 1.dp,
    )
}

@Preview(showBackground = true)
@Composable
private fun PubDetailScreenPreview() {
    MoballTheme {
        PubDetailScreen(
            state = PubDetailContract.State(
                pubDetail = PubDetail(
                    pubId = 1L,
                    name = "시그니처 펍",
                    address = "서울특별시 마포구 월드컵북로 396",
                    region = "MAPO",
                    latitude = 37.5516,
                    longitude = 126.9086,
                    phoneNumber = "02-1234-5678",
                    status = PubStatus.OPEN,
                    capacityRange = null,
                    groupSeatMaxPeople = 30,
                    favoriteCount = 12,
                    description = null,
                    imageUrls = emptyList(),
                    teams = listOf(
                        KboTeam(teamId = 3L, shortName = "LG", name = "LG 트윈스"),
                        KboTeam(teamId = 6L, shortName = "두산", name = "두산 베어스"),
                    ),
                    facilityCodes = listOf("GROUP_SEAT", "BIG_SCREEN"),
                    styleCodes = listOf("OFFICIAL_PUB"),
                    themeCodes = emptyList(),
                    foodCodes = listOf("CHICKEN", "BEER"),
                    businessHours = listOf(
                        BusinessHour(1, "17:00", "02:00", false),
                        BusinessHour(2, "17:00", "02:00", false),
                        BusinessHour(3, "17:00", "02:00", false),
                        BusinessHour(4, "17:00", "02:00", false),
                        BusinessHour(5, "17:00", "03:00", false),
                        BusinessHour(6, "15:00", "03:00", false),
                        BusinessHour(7, null, null, true),
                    ),
                    menus = emptyList(),
                    isWishlisted = false,
                ),
            ),
            onEvent = {},
        )
    }
}

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
import org.app.presentation.pubdetail.component.PubFacilitySection
import org.app.presentation.pubdetail.component.PubHeroCarousel
import org.app.presentation.pubdetail.component.PubInfoSection
import org.app.presentation.pubdetail.component.PubParkingSection
import org.app.presentation.pubdetail.component.PubPhotoGallery
import org.app.presentation.pubdetail.component.PubPhotoSection
import org.app.presentation.pubdetail.model.BusinessHours
import org.app.presentation.pubdetail.model.BusinessStatus
import org.app.presentation.pubdetail.model.FacilityItem
import org.app.presentation.pubdetail.model.FacilityType
import org.app.presentation.pubdetail.model.KboTeam
import org.app.presentation.pubdetail.model.ParkingItem
import org.app.presentation.pubdetail.model.ParkingType
import org.app.presentation.pubdetail.model.PubDetail

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
                is PubDetailContract.SideEffect.NavigateBack -> {
                    onBack()
                }

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
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
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
                    address = detail.address,
                    phoneNumber = detail.phoneNumber,
                    seatCount = detail.seatCount,
                    isHoursExpanded = state.isHoursExpanded,
                    onHoursToggle = { onEvent(PubDetailContract.Event.OnHoursToggle) },
                    onPhoneCall = { onEvent(PubDetailContract.Event.OnPhoneCall) },
                    isWished = detail.isWishlisted,
                    wishCount = detail.wishlistCount,
                    onWishToggle = { onEvent(PubDetailContract.Event.OnWishlistToggle) },
                )

                Divider()

                if (detail.facilities.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    PubFacilitySection(facilities = detail.facilities)
                }

                if (detail.parkingOptions.isNotEmpty()) {
                    PubParkingSection(parkingItems = detail.parkingOptions)
                }

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
                    id = "preview",
                    name = "시그니처 펍",
                    imageUrls = emptyList(),
                    teams = listOf(
                        KboTeam(id = 3, shortName = "LG", fullName = "LG 트윈스"),
                        KboTeam(id = 6, shortName = "두산", fullName = "두산 베어스"),
                    ),
                    isWishlisted = false,
                    wishlistCount = 12,
                    address = "서울특별시 마포구 월드컵북로 396",
                    phoneNumber = "02-1234-5678",
                    businessHours = BusinessHours(
                        openTime = "17:00",
                        closeTime = "02:00",
                        lastOrder = "01:30",
                        isOpenNow = true,
                        status = BusinessStatus.OPEN,
                    ),
                    seatCount = 60,
                    facilities = listOf(
                        FacilityItem(FacilityType.GROUP_SEATING),
                        FacilityItem(FacilityType.PROJECTOR),
                    ),
                    parkingOptions = listOf(ParkingItem(ParkingType.NEARBY, "도보 3분 내 공영주차장")),
                    latitude = 37.5516,
                    longitude = 126.9086,
                ),
            ),
            onEvent = {},
        )
    }
}

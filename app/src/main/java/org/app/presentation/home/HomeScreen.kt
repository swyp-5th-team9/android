package org.app.presentation.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.util.FusedLocationSource
import org.app.core.designsystem.theme.MoballTheme
import org.app.presentation.home.component.HomeFilterBottomSheet
import org.app.presentation.home.component.HomeFilterChipBar
import org.app.presentation.home.component.HomeMyLocationButton
import org.app.presentation.home.component.HomeReportButton
import org.app.presentation.home.component.HomeSearchTextField

private const val LOCATION_PERMISSION_REQUEST_CODE = 1000

@Composable
fun HomeRoute(
    onNavigateToPubDetail: (pubId: String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToPubFilter: () -> Unit,
    onNavigateToReport: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                HomeContract.SideEffect.NavigateToSearch -> onNavigateToSearch()
                HomeContract.SideEffect.NavigateToPubFilter -> onNavigateToPubFilter()
                HomeContract.SideEffect.NavigateToReport -> onNavigateToReport()
                is HomeContract.SideEffect.NavigateToPubDetail -> onNavigateToPubDetail(effect.pubId)
                is HomeContract.SideEffect.ShowToast -> { /* TODO: Toast */ }
            }
        }
    }

    HomeScreen(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    state: HomeContract.State,
    onEvent: (HomeContract.Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED,
        )
    }
    var naverMap by remember { mutableStateOf<NaverMap?>(null) }

    val locationSource = remember {
        context.findActivity()?.let { FusedLocationSource(it, LOCATION_PERMISSION_REQUEST_CODE) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(hasLocationPermission, naverMap) {
        if (hasLocationPermission) {
            naverMap?.locationTrackingMode = LocationTrackingMode.Follow
        }
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }
    }

    val mapView = remember { MapView(context) }

    DisposableEffect(lifecycleOwner) {
        mapView.onCreate(null)
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                mapView.apply {
                    getMapAsync { map ->
                        naverMap = map
                        map.locationSource = locationSource
                        map.uiSettings.isLocationButtonEnabled = false // 커스텀 버튼 사용
                        map.locationOverlay.isVisible = true
                        map.locationOverlay.setOnClickListener {
                            // TODO: 내 위치 마커 클릭 시 동작
                            false
                        }
                        // TODO: state.pubMarkers 로 Marker 추가
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
        )

        // ── 2. 상단 오버레이 (검색바 + 필터 칩) ───────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .align(Alignment.TopCenter),
        ) {
            HomeSearchTextField(
                onSearchClick = { onEvent(HomeContract.Event.OnSearchBarClick) },
                modifier = Modifier.fillMaxWidth(),
            )
            HomeFilterChipBar(
                teamChipLabel = state.teamChipLabel,
                regionChipLabel = state.regionChipLabel,
                isTeamSelected = state.filter.isTeamFilterActive,
                isRegionSelected = state.filter.isRegionFilterActive,
                onMenuClick = { onEvent(HomeContract.Event.OnMenuFilterClick) },
                onTeamClick = { onEvent(HomeContract.Event.OnTeamChipClick) },
                onRegionClick = { onEvent(HomeContract.Event.OnRegionChipClick) },
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HomeReportButton(
                onClick = { onEvent(HomeContract.Event.OnReportClick) },
            )
            HomeMyLocationButton(
                onClick = {
                    onEvent(HomeContract.Event.OnMyLocationClick)
                    naverMap?.locationTrackingMode = LocationTrackingMode.Follow
                },
            )
        }

        // ── 5. 필터 바텀시트 ──────────────────────────────────────────
        if (state.showFilterBottomSheet) {
            HomeFilterBottomSheet(
                initialTab = state.filterBottomSheetTab,
                userFavoriteTeamIds = state.userFavoriteTeamIds,
                initialTeamIds = state.filter.selectedTeamIds,
                initialRegion = state.filter.selectedRegion,
                onApply = { teamIds, teamNames, region ->
                    onEvent(HomeContract.Event.OnFilterApply(teamIds, teamNames, region))
                },
                onDismiss = { onEvent(HomeContract.Event.OnFilterBottomSheetDismiss) },
            )
        }
    }
}

private fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    MoballTheme {
        HomeScreen(
            state = HomeContract.State(
                userFavoriteTeamNames = listOf("한화"),
            ),
            onEvent = {},
        )
    }
}

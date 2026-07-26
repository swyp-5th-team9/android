package org.app.presentation.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PointF
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moball.app.R
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import org.app.core.common.util.CollectSideEffect
import org.app.core.designsystem.component.LocalMoballToastHostState
import org.app.core.designsystem.theme.MoballTheme
import org.app.presentation.home.component.HomeFilterBottomSheet
import org.app.presentation.home.component.HomeFilterChipBar
import org.app.presentation.home.component.HomeMyLocationButton
import org.app.presentation.home.component.HomePubDetailBottomSheet
import org.app.presentation.home.component.HomePubListBottomSheet
import org.app.presentation.home.component.HomeReportButton
import org.app.presentation.home.component.HomeSearchTextField
import org.app.presentation.home.component.clusterOverlayImage
import org.app.presentation.home.model.PubCluster
import org.app.presentation.home.model.PubMarker
import org.app.presentation.home.model.PubMarkerType

private const val LOCATION_PERMISSION_REQUEST_CODE = 1000

@Composable
fun HomeRoute(
    onNavigateToPubDetail: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToPubFilter: () -> Unit,
    onNavigateToReport: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val toastHostState = LocalMoballToastHostState.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Screen에서 지도 준비 완료 시 올려주는(호이스팅) NaverMap 참조 — 카메라 이동 SideEffect 처리에 사용
    var naverMap by remember { mutableStateOf<NaverMap?>(null) }

    CollectSideEffect(viewModel.sideEffect) { effect ->
        when (effect) {
            is HomeContract.SideEffect.NavigateToSearch -> onNavigateToSearch()
            is HomeContract.SideEffect.NavigateToPubFilter -> onNavigateToPubFilter()
            is HomeContract.SideEffect.NavigateToReport -> onNavigateToReport()
            is HomeContract.SideEffect.NavigateToPubDetail -> onNavigateToPubDetail(effect.pubId)
            is HomeContract.SideEffect.MoveCameraToBounds -> {
                val latLngs = effect.points.map { LatLng(it.first, it.second) }
                if (latLngs.size == 1) {
                    naverMap?.moveCamera(CameraUpdate.scrollTo(latLngs.first()).animate(CameraAnimation.Easing))
                } else if (latLngs.size > 1) {
                    val bounds = LatLngBounds.Builder().include(latLngs).build()
                    naverMap?.moveCamera(CameraUpdate.fitBounds(bounds, 150).animate(CameraAnimation.Easing))
                }
            }

            is HomeContract.SideEffect.ShowToast ->
                toastHostState.show(effect.message)

            is HomeContract.SideEffect.OpenMap -> {
                val intent = Intent(Intent.ACTION_VIEW, effect.url.toUri())
                val isAppInstalled = context.packageManager
                    .queryIntentActivities(
                        intent,
                        PackageManager.MATCH_DEFAULT_ONLY,
                    ).isNotEmpty()

                if (isAppInstalled) {
                    context.startActivity(intent)
                } else {
                    context.startActivity(Intent(Intent.ACTION_VIEW, effect.webFallbackUrl.toUri()))
                }
            }
        }
    }

    HomeScreen(
        state = state,
        onEvent = viewModel::onEvent,
        naverMap = naverMap,
        onMapReady = { naverMap = it },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeContract.State,
    onEvent: (HomeContract.Event) -> Unit,
    naverMap: NaverMap?,
    onMapReady: (NaverMap) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val locationSource =
        remember { FusedLocationSource(context.findActivity()!!, LOCATION_PERMISSION_REQUEST_CODE) }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED,
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        hasLocationPermission = permissions.values.all { it }
    }

    // 펍 상세 등 네비게이션 왕복 시 지도(MapView)가 재생성되므로, 카메라 위치를 저장해 복원한다.
    // (rememberSaveable 은 백스택 복귀 시 값이 보존된다)
    var savedCameraLat by rememberSaveable { mutableStateOf(Double.NaN) }
    var savedCameraLng by rememberSaveable { mutableStateOf(Double.NaN) }
    var savedCameraZoom by rememberSaveable { mutableStateOf(Double.NaN) }
    val hasSavedCamera = !savedCameraLat.isNaN()

    LaunchedEffect(hasLocationPermission, naverMap) {
        val map = naverMap ?: return@LaunchedEffect
        if (hasSavedCamera) {
            // 복귀 상황: 저장된 위치를 유지하고 현위치로 카메라가 튀지 않도록 추적만 표시(NoFollow)
            map.locationTrackingMode =
                if (hasLocationPermission) LocationTrackingMode.NoFollow else LocationTrackingMode.None
        } else if (hasLocationPermission) {
            // 최초 진입: 현위치로 카메라 이동
            map.locationTrackingMode = LocationTrackingMode.Follow
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
    val activeMarkers = remember { mutableListOf<Marker>() }
    val activeClusters = remember { mutableListOf<Marker>() }

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

    LaunchedEffect(naverMap, state.pubMarkers, state.pubClusters) {
        val map = naverMap ?: return@LaunchedEffect
        renderPubMarkers(
            context = context,
            map = map,
            markers = state.pubMarkers,
            currentMarkers = activeMarkers,
            onMarkerClick = { pubId -> onEvent(HomeContract.Event.OnPubMarkerClick(pubId)) },
        )
        renderPubClusters(
            context = context,
            map = map,
            clusters = state.pubClusters,
            currentClusters = activeClusters,
            onClusterClick = { cluster -> onEvent(HomeContract.Event.OnClusterClick(cluster)) },
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                mapView.apply {
                    getMapAsync { map ->
                        map.locationSource = locationSource
                        map.uiSettings.isLocationButtonEnabled = false // 커스텀 버튼 사용
                        map.locationOverlay.isVisible = true

                        // 재생성된 지도에 이전 카메라 위치를 즉시 복원(기본 위치로 깜빡이는 것 방지)
                        if (!savedCameraLat.isNaN()) {
                            map.moveCamera(
                                CameraUpdate.toCameraPosition(
                                    CameraPosition(
                                        LatLng(savedCameraLat, savedCameraLng),
                                        savedCameraZoom,
                                    ),
                                ),
                            )
                        }

                        map.addOnCameraIdleListener {
                            val cameraPos = map.cameraPosition
                            savedCameraLat = cameraPos.target.latitude
                            savedCameraLng = cameraPos.target.longitude
                            savedCameraZoom = cameraPos.zoom
                            val bounds = map.contentBounds
                            onEvent(
                                HomeContract.Event.OnMapBoundsChanged(
                                    swLat = bounds.southWest.latitude,
                                    swLng = bounds.southWest.longitude,
                                    neLat = bounds.northEast.latitude,
                                    neLng = bounds.northEast.longitude,
                                    zoom = cameraPos.zoom,
                                ),
                            )
                        }

                        map.locationOverlay.setOnClickListener { false }

                        onMapReady(map)
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
        )

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
                onClick = { naverMap?.locationTrackingMode = LocationTrackingMode.Follow },
            )
        }

        if (state.showFilterBottomSheet) {
            HomeFilterBottomSheet(
                initialTab = state.filterBottomSheetTab,
                userFavoriteTeamIds = state.userFavoriteTeamIds,
                initialTeamIds = state.filter.selectedTeamIds,
                initialRegions = state.filter.selectedRegions,
                onApply = { teamIds, teamNames, regions ->
                    onEvent(
                        HomeContract.Event.OnFilterApply(
                            teamIds,
                            teamNames,
                            regions,
                            state.filter.openNow,
                            state.filter.businessDay,
                        ),
                    )
                },
                onDismiss = { onEvent(HomeContract.Event.OnFilterBottomSheetDismiss) },
            )
        }

        if (state.showPubListSheet && !state.showPubDetailSheet) {
            val displayItems = remember(state.selectedPubList, state.pubMapItems) {
                if (state.selectedPubList.isNotEmpty()) {
                    state.selectedPubList.mapNotNull { selected ->
                        state.pubMapItems.find { it.pubId == selected.pubId }
                    }
                } else {
                    state.pubMapItems
                }
            }

            HomePubListBottomSheet(
                pubItems = displayItems,
                favoritePubIds = state.favoritePubIds,
                filter = state.filter,
                pubDetails = state.listPubDetails,
                onItemAppear = { pubId -> onEvent(HomeContract.Event.OnPubListItemAppear(pubId)) },
                onItemClick = { pubId -> onEvent(HomeContract.Event.OnPubListItemClick(pubId)) },
                onFavoriteClick = { pubId -> onEvent(HomeContract.Event.OnPubListFavoriteClick(pubId)) },
                onFilterClick = { filterKey -> onEvent(HomeContract.Event.OnQuickFilterClick(filterKey)) },
                onDismiss = { onEvent(HomeContract.Event.OnPubListSheetDismiss) },
            )
        }

        if (state.showPubDetailSheet) {
            HomePubDetailBottomSheet(
                detail = state.selectedPubDetail,
                isLoading = state.isPubDetailLoading,
                isFavoriteLoading = state.isPubFavoriteLoading,
                onFavoriteClick = { onEvent(HomeContract.Event.OnFavoriteClick) },
                onKakaoMapClick = {
                    val d = state.selectedPubDetail ?: return@HomePubDetailBottomSheet
                    onEvent(HomeContract.Event.OnKakaoMapClick(d.latitude, d.longitude, d.name))
                },
                onNaverMapClick = {
                    val d = state.selectedPubDetail ?: return@HomePubDetailBottomSheet
                    onEvent(HomeContract.Event.OnNaverMapClick(d.latitude, d.longitude, d.name))
                },
                onCardClick = { pubId -> onEvent(HomeContract.Event.OnPubDetailCardClick(pubId)) },
                onDismiss = { onEvent(HomeContract.Event.OnPubDetailSheetDismiss) },
            )
        }
    }
}

// 마커 아이콘은 종류별로 동일하므로 매 마커마다 재생성하지 않고 한 번만 만들어 재사용한다.
private val matchPinImage by lazy { OverlayImage.fromResource(R.drawable.img_pin) }
private val favoritePinImage by lazy { OverlayImage.fromResource(R.drawable.img_favorite) }

private fun renderPubMarkers(
    context: Context,
    map: NaverMap,
    markers: List<PubMarker>,
    currentMarkers: MutableList<Marker>,
    onMarkerClick: (String) -> Unit,
) {
    // PNG 마커는 원본 픽셀 크기로 렌더되므로, 원래 벡터(dp) 크기를 명시해 확대되지 않게 한다.
    val density = context.resources.displayMetrics.density
    val markerSize = (68 * density).toInt()
    currentMarkers.forEach { it.map = null }
    currentMarkers.clear()
    markers.forEach { pubMarker ->
        val marker = Marker().apply {
            position = LatLng(pubMarker.latitude, pubMarker.longitude)
            icon = when (pubMarker.type) {
                PubMarkerType.MATCH -> matchPinImage
                PubMarkerType.FAVORITE -> favoritePinImage
            }
            width = markerSize
            height = markerSize
            this.map = map
            setOnClickListener {
                onMarkerClick(pubMarker.pubId)
                true
            }
        }
        currentMarkers.add(marker)
    }
}

private fun renderPubClusters(
    context: Context,
    map: NaverMap,
    clusters: List<PubCluster>,
    currentClusters: MutableList<Marker>,
    onClusterClick: (PubCluster) -> Unit,
) {
    currentClusters.forEach { it.map = null }
    currentClusters.clear()
    clusters.forEach { cluster ->
        val marker = Marker().apply {
            position = LatLng(cluster.latitude, cluster.longitude)
            icon = clusterOverlayImage(context, cluster.count)
            anchor = PointF(0.5f, 0.5f)
            this.map = map
            setOnClickListener {
                onClusterClick(cluster)
                true
            }
        }
        currentClusters.add(marker)
    }
}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MoballTheme {
        HomeScreen(
            state = HomeContract.State(),
            onEvent = {},
            naverMap = null,
            onMapReady = {},
        )
    }
}

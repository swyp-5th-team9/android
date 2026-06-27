package org.app.presentation.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.util.FusedLocationSource

private const val LOCATION_PERMISSION_REQUEST_CODE = 1000

@Composable
fun HomeRoute(
    onPubClick: (pubId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
            // SideEffect 처리 (현재는 정의된 효과가 없음)
        }
    }

    HomeScreen(
        onPubClick = onPubClick,
        modifier = modifier,
    )
}

@Composable
internal fun HomeScreen(
    onPubClick: (pubId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 위치 권한 상태 관리
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED,
        )
    }

    // NaverMap 객체 저장
    var naverMap by remember { mutableStateOf<NaverMap?>(null) }

    // FusedLocationSource 초기화
    val locationSource = remember {
        context.findActivity()?.let {
            FusedLocationSource(it, LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    // 권한 요청 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        hasLocationPermission = granted
    }

    // 권한 상태나 지도 객체가 변경될 때 위치 추적 모드 처리
    LaunchedEffect(hasLocationPermission, naverMap) {
        if (hasLocationPermission) {
            naverMap?.locationTrackingMode = LocationTrackingMode.Follow
        }
    }

    // 처음 진입 시 권한 체크 및 요청
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

    // MapView 라이프사이클 관리
    val mapView = remember { MapView(context) }

    DisposableEffect(lifecycleOwner) {
        mapView.onCreate(null)
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    mapView.onStart()
                }

                Lifecycle.Event.ON_RESUME -> {
                    mapView.onResume()
                }

                Lifecycle.Event.ON_PAUSE -> {
                    mapView.onPause()
                }

                Lifecycle.Event.ON_STOP -> {
                    mapView.onStop()
                }

                Lifecycle.Event.ON_DESTROY -> {
                    mapView.onDestroy()
                }

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
            factory = { ctx ->
                mapView.apply {
                    getMapAsync { map ->
                        naverMap = map
                        map.locationSource = locationSource
                        map.uiSettings.isLocationButtonEnabled = true
                        map.locationOverlay.run {
                            isVisible = true
                            setOnClickListener {
                                // TODO: 실제 마커 클릭 시 선택된 pubId 전달
                                onPubClick("")
                                true
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
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

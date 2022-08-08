package com.igrocery.overpriced.presentation.scanbarcode

import android.Manifest
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.CornerPathEffect
import android.hardware.camera2.CameraManager
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.igrocery.overpriced.presentation.selectcurrency.SelectCurrencyScreenStateHolder
import com.igrocery.overpriced.presentation.shared.BackButton
import com.igrocery.overpriced.shared.Logger
import com.igrocery.overpriced.presentation.R
import java.util.*

@Suppress("unused")
private val log = Logger { }

@Composable
fun ScanBarcodeScreen(
    viewModel: ScanBarcodeScreenViewModel,
    navigateUp: () -> Unit,
    navigateDone: (String) -> Unit,
) {
    log.debug("Composing ScanBarcodeScreen")

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            Color.Transparent,
            transformColorForLightContent = { color -> color })
        systemUiController.setNavigationBarColor(
            Color.Transparent,
            navigationBarContrastEnforced = false,
            transformColorForLightContent = { color -> color })
    }

    val activity = LocalContext.current as ComponentActivity
    DisposableEffect(key1 = Unit) {
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        onDispose {
            activity.requestedOrientation = originalOrientation
        }
    }

    val state by rememberScanBarcodeScreenState()
    MainContent(
        state = state,
        onBackButtonClick = navigateUp,
    )

    state.scannedBarcode?.let { barcode ->
        LaunchedEffect(key1 = Unit) {
            navigateDone(barcode)
        }
    }

    BackHandler {
        navigateUp()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MainContent(
    state: ScanBarcodeScreenStateHolder,
    onBackButtonClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                navigationIcon = {
                    BackButton(
                        onClick = onBackButtonClick,
                        modifier = Modifier
                            .padding(14.dp)
                            .size(24.dp, 24.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Color.White
                        )
                    )
                },
                actions = {

                },
                title = {},
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { scaffoldPadding ->
        val cameraPermissionsState =
            rememberPermissionState(permission = Manifest.permission.CAMERA)
        when (cameraPermissionsState.status) {
            PermissionStatus.Granted -> {
                val activity = LocalContext.current as ComponentActivity
                AndroidView(
                    factory = {
                        PreviewView(activity)
                    },
                    modifier = Modifier.fillMaxSize()
                ) { previewView ->
                    if (!state.isCameraStarted()) {
                        state.preview.setSurfaceProvider(previewView.surfaceProvider)
                        state.startCamera(activity)
                    }
                }

                val primaryColor = MaterialTheme.colorScheme.primary
                val helperRectColor by remember {
                    derivedStateOf {
                        if (state.scannedBarcode == null) {
                            Color.White
                        } else {
                            primaryColor
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(scaffoldPadding)
                        .navigationBarsPadding()
                        .fillMaxSize()
                ) {
                    Box(
                        contentAlignment = Alignment.TopCenter,
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 30.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(id = R.string.scan_barcode_scan_help_text),
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                        )
                    }

                    var center by remember { mutableStateOf(IntOffset(0, 0)) }
                    var shortEdgeLength by remember { mutableStateOf(0) }
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .onGloballyPositioned {
                                center = it.size.center
                                shortEdgeLength = minOf(it.size.width, it.size.height)
                            }
                    ) {
                        val left = center.x - shortEdgeLength * 0.35f
                        val top = center.y - shortEdgeLength * 0.35f
                        val right = center.x + shortEdgeLength * 0.35f
                        val bottom = center.y + shortEdgeLength * 0.35f

                        val cornerLength = (right - left) * 0.2f

                        val path = Path().apply {
                            moveTo(left, top + cornerLength)
                            lineTo(left, top)
                            lineTo(left + cornerLength, top)

                            moveTo(right - cornerLength, top)
                            lineTo(right, top)
                            lineTo(right, top + cornerLength)

                            moveTo(right, bottom - cornerLength)
                            lineTo(right, bottom)
                            lineTo(right - cornerLength, bottom)

                            moveTo(left + cornerLength, bottom)
                            lineTo(left, bottom)
                            lineTo(left, bottom - cornerLength)
                        }

                        drawPath(
                            path,
                            helperRectColor,
                            style = Stroke(
                                width = 2.dp.toPx(),
                                cap = StrokeCap.Round,
                                pathEffect = PathEffect.cornerPathEffect(12.dp.toPx())
                            )
                        )
                    }
                }
            }
            is PermissionStatus.Denied -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .background(Color.Black)
                        .padding(scaffoldPadding)
                        .navigationBarsPadding()
                        .fillMaxSize()
                ) {
                    Text(
                        text = stringResource(id = R.string.scan_barcode_no_permission_message_text),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 40.dp),
                        style = MaterialTheme.typography.labelLarge
                    )

                    TextButton(
                        onClick = { cameraPermissionsState.launchPermissionRequest() },
                        shape = RoundedCornerShape(100),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.secondary,
                            disabledContentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 18.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.scan_barcode_no_permission_button_text)
                        )
                    }
                }
            }
        }

        LaunchedEffect(key1 = Unit) {
            cameraPermissionsState.launchPermissionRequest()
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun DefaultPreview() {
    MainContent(
        state = ScanBarcodeScreenStateHolder(),
        onBackButtonClick = {},
    )
}

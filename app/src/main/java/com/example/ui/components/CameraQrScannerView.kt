package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ui.theme.FlowAccentCyan
import com.example.ui.theme.FlowAccentEmerald
import com.example.ui.theme.FlowDarkBackground
import com.example.ui.theme.FlowDeepNavy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.util.concurrent.Executors

@Composable
fun CameraQrScannerView(
  onQrCodeScanned: (String) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  var hasCameraPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
      ) == PackageManager.PERMISSION_GRANTED
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    hasCameraPermission = isGranted
  }

  LaunchedEffect(Unit) {
    if (!hasCameraPermission) {
      permissionLauncher.launch(Manifest.permission.CAMERA)
    }
  }

  var isTorchOn by remember { mutableStateOf(false) }
  var cameraInstance by remember { mutableStateOf<Camera?>(null) }
  var isScanningEnabled by remember { mutableStateOf(true) }

  val infiniteTransition = rememberInfiniteTransition(label = "laserScan")
  val scanLineProgress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "scanLine"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color.Black)
      .testTag("camera_qr_scanner_root")
  ) {
    if (hasCameraPermission) {
      // CameraX Live Preview View
      AndroidView(
        factory = { ctx ->
          val previewView = PreviewView(ctx).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
          }

          val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
          val cameraExecutor = Executors.newSingleThreadExecutor()

          cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
              it.surfaceProvider = previewView.surfaceProvider
            }

            val imageAnalysis = ImageAnalysis.Builder()
              .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
              .build()

            val reader = MultiFormatReader().apply {
              setHints(
                mapOf(
                  DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE),
                  DecodeHintType.TRY_HARDER to true
                )
              )
            }

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
              if (!isScanningEnabled) {
                imageProxy.close()
                return@setAnalyzer
              }

              val qrText = processImageProxyWithZxing(imageProxy, reader)
              imageProxy.close()

              if (qrText != null && isScanningEnabled) {
                isScanningEnabled = false
                ContextCompat.getMainExecutor(ctx).execute {
                  onQrCodeScanned(qrText)
                }
              }
            }

            try {
              cameraProvider.unbindAll()
              val cam = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalysis
              )
              cameraInstance = cam
            } catch (e: Exception) {
              e.printStackTrace()
            }
          }, ContextCompat.getMainExecutor(ctx))

          previewView
        },
        modifier = Modifier.fillMaxSize()
      )

      // Viewfinder Overlay & Framing Grid
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        // Darkened surrounding mask
        Canvas(modifier = Modifier.fillMaxSize()) {
          val boxSize = 250.dp.toPx()
          val left = (size.width - boxSize) / 2f
          val top = (size.height - boxSize) / 2f

          // Top, Bottom, Left, Right dark bars
          drawRect(Color.Black.copy(alpha = 0.55f), topLeft = Offset(0f, 0f), size = Size(size.width, top))
          drawRect(Color.Black.copy(alpha = 0.55f), topLeft = Offset(0f, top + boxSize), size = Size(size.width, size.height - (top + boxSize)))
          drawRect(Color.Black.copy(alpha = 0.55f), topLeft = Offset(0f, top), size = Size(left, boxSize))
          drawRect(Color.Black.copy(alpha = 0.55f), topLeft = Offset(left + boxSize, top), size = Size(size.width - (left + boxSize), boxSize))
        }

        // Viewfinder targeting Box
        Box(
          modifier = Modifier
            .size(250.dp)
            .border(2.dp, FlowAccentCyan.copy(alpha = 0.8f), RoundedCornerShape(20.dp)),
          contentAlignment = Alignment.Center
        ) {
          // Sweeping Laser Beam
          Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            val y = size.height * scanLineProgress
            drawLine(
              color = FlowAccentEmerald,
              start = Offset(0f, y),
              end = Offset(size.width, y),
              strokeWidth = 3.dp.toPx()
            )
          }

          // Corner Guides
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(8.dp)
          ) {
            Surface(
              shape = CircleShape,
              color = FlowAccentCyan,
              modifier = Modifier
                .size(6.dp)
                .align(Alignment.TopStart)
            ) {}
            Surface(
              shape = CircleShape,
              color = FlowAccentCyan,
              modifier = Modifier
                .size(6.dp)
                .align(Alignment.TopEnd)
            ) {}
            Surface(
              shape = CircleShape,
              color = FlowAccentCyan,
              modifier = Modifier
                .size(6.dp)
                .align(Alignment.BottomStart)
            ) {}
            Surface(
              shape = CircleShape,
              color = FlowAccentCyan,
              modifier = Modifier
                .size(6.dp)
                .align(Alignment.BottomEnd)
            ) {}
          }
        }
      }
    } else {
      // Permission Denied / Request Fallback Card
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Surface(
          shape = CircleShape,
          color = FlowAccentCyan.copy(alpha = 0.2f),
          modifier = Modifier.size(72.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.CameraAlt,
              contentDescription = null,
              tint = FlowAccentCyan,
              modifier = Modifier.size(38.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
          text = "Camera Access Required",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = Color.White,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "FlowTransfer uses your camera to scan the other device's QR code and securely exchange encrypted session keys.",
          style = MaterialTheme.typography.bodyMedium,
          color = Color.White.copy(alpha = 0.8f),
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
          onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
          colors = ButtonDefaults.buttonColors(
            containerColor = FlowAccentCyan,
            contentColor = FlowDeepNavy
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("grant_camera_permission_button")
        ) {
          Text("Allow Camera Access", fontWeight = FontWeight.Bold)
        }
      }
    }

    // Top Bar (Back button, Title, Torch toggle)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 44.dp, start = 16.dp, end = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onDismiss,
        modifier = Modifier
          .background(Color.Black.copy(alpha = 0.5f), CircleShape)
          .testTag("close_scanner_button")
      ) {
        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
      }

      Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.Black.copy(alpha = 0.6f)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Security, contentDescription = null, tint = FlowAccentEmerald, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "CameraX Optical Scanner",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
        }
      }

      IconButton(
        onClick = {
          isTorchOn = !isTorchOn
          cameraInstance?.cameraControl?.enableTorch(isTorchOn)
        },
        modifier = Modifier
          .background(Color.Black.copy(alpha = 0.5f), CircleShape)
          .testTag("toggle_torch_button")
      ) {
        Icon(
          imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
          contentDescription = "Flashlight",
          tint = if (isTorchOn) FlowAccentCyan else Color.White
        )
      }
    }

    // Bottom Guidance Card & Simulator / Direct Connect Fallback
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
        .padding(bottom = 36.dp, start = 24.dp, end = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "Align QR code inside the frame to pair automatically",
        style = MaterialTheme.typography.bodySmall,
        color = Color.White.copy(alpha = 0.9f),
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Instant Simulator Trigger for rapid testing without physical second device
      OutlinedButton(
        onClick = {
          onQrCodeScanned("flowtransfer://p2p?session=849201&ip=192.168.49.1&device=iPhone+16+Pro&auth=aes256&pin=741289")
        },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = FlowAccentCyan),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("simulate_qr_scan_trigger")
      ) {
        Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Test Scan Instant Handshake", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
      }
    }
  }
}

/**
 * Extracts and decodes QR Code byte buffers from CameraX ImageProxy
 */
private fun processImageProxyWithZxing(
  imageProxy: ImageProxy,
  reader: MultiFormatReader
): String? {
  return try {
    val plane = imageProxy.planes[0]
    val buffer = plane.buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)

    val width = imageProxy.width
    val height = imageProxy.height

    val source = PlanarYUVLuminanceSource(
      bytes,
      width,
      height,
      0,
      0,
      width,
      height,
      false
    )

    val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
    val result = reader.decodeWithState(binaryBitmap)
    result.text
  } catch (e: Exception) {
    null
  } finally {
    reader.reset()
  }
}

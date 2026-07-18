package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.GeminiApiClient
import com.example.data.MockProducts
import com.example.data.Product
import com.example.ui.components.FrostedAmbientBackground
import com.example.ui.components.FrostedGlassCard
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.FrostedCardBg
import com.example.ui.theme.FrostedCardBorder
import com.example.ui.theme.TextSlate
import com.example.ui.theme.TextLightSlate
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerView(
    onProductScanned: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    var searchInput by remember { mutableStateOf("") }
    var isLoadingProduct by remember { mutableStateOf(false) }
    var apiErrorMessage by remember { mutableStateOf<String?>(null) }

    // Camera permission state
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

    // Animation for scanning laser line
    val infiniteTransition = rememberInfiniteTransition(label = "laser_anim")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )

    // Function to analyze text input / custom qr
    fun triggerTextScan(query: String) {
        if (query.trim().isEmpty()) return
        keyboardController?.hide()
        isLoadingProduct = true
        apiErrorMessage = null

        scope.launch {
            // First check if it matches a pre-defined mock item ID or name
            val localMatch = MockProducts.findById(query) ?: MockProducts.list.find {
                it.name.lowercase().contains(query.lowercase()) || it.brand.lowercase().contains(query.lowercase())
            }

            if (localMatch != null) {
                onProductScanned(localMatch)
                isLoadingProduct = false
            } else {
                // Call Gemini for real-time analysis!
                val apiProduct = GeminiApiClient.analyzeProduct(query)
                isLoadingProduct = false
                if (apiProduct != null) {
                    onProductScanned(apiProduct)
                } else {
                    apiErrorMessage = "Unable to process product details. Please try another code."
                }
            }
        }
    }

    FrostedAmbientBackground(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // App top identity
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "NutriScan ",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Light,
                            color = Color.White
                        )
                        Text(
                            text = "Pro v2.4",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = EmeraldAccent
                        )
                    }
                    Text(
                        text = "Align the product code within the square frame",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSlate,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color.White.copy(alpha = 0.08f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.20f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "App Logo",
                        tint = EmeraldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Camera / Simulated Viewport Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.3f)
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.35f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (hasCameraPermission) {
                        CameraPreviewView(modifier = Modifier.fillMaxSize())
                    } else {
                        // Friendly virtual camera stream fallback
                        VirtualConveyorAnimation(modifier = Modifier.fillMaxSize())
                    }

                    // Elegant viewfinder overlay box with glowing emerald borders
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Scanning corner borders with Emerald brackets!
                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .border(width = 1.dp, color = Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(24.dp))
                                .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(24.dp))
                        ) {
                            // Top-left bracket
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .size(32.dp)
                                    .border(width = 4.dp, color = EmeraldAccent, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 0.dp, bottomStart = 0.dp, bottomEnd = 0.dp))
                            )
                            // Top-right bracket
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(32.dp)
                                    .border(width = 4.dp, color = EmeraldAccent, shape = RoundedCornerShape(topStart = 0.dp, topEnd = 24.dp, bottomStart = 0.dp, bottomEnd = 0.dp))
                            )
                            // Bottom-left bracket
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .size(32.dp)
                                    .border(width = 4.dp, color = EmeraldAccent, shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 24.dp, bottomEnd = 0.dp))
                            )
                            // Bottom-right bracket
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(32.dp)
                                    .border(width = 4.dp, color = EmeraldAccent, shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 0.dp, bottomEnd = 24.dp))
                            )
                        }

                        // Moving laser scanning beam
                        Box(
                            modifier = Modifier
                                .size(232.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .offset(y = (232.dp * laserOffset))
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(Color.Transparent, EmeraldAccent.copy(alpha = 0.8f), EmeraldAccent.copy(alpha = 0.8f), Color.Transparent)
                                        )
                                    )
                            )
                        }

                        // Scanner feedback text
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 12.dp)
                                .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (hasCameraPermission) "Camera Active - scanning..." else "Virtual simulator mode active",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // If loading, overlay progress indicator
                    if (isLoadingProduct) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.75f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    color = EmeraldAccent,
                                    strokeWidth = 4.dp,
                                    modifier = Modifier.size(54.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Focusing QR Code...",
                                    color = EmeraldAccent,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Text(
                                    text = "Gemini AI is analyzing product specs...",
                                    color = TextLightSlate,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Camera permission requester button (if not granted)
            if (!hasCameraPermission) {
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp).padding(top = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldAccent.copy(alpha = 0.2f),
                        contentColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldAccent.copy(alpha = 0.4f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp), tint = EmeraldAccent)
                        Text(
                            text = "Enable Live Camera Scan",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            // Manual Query Scan / Search input
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "MANUAL CODE SCAN / SEARCH",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldAccent,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = searchInput,
                    onValueChange = { searchInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter QR code text or product name...", color = TextSlate) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = EmeraldAccent
                        )
                    },
                    trailingIcon = {
                        if (searchInput.isNotEmpty()) {
                            IconButton(onClick = { searchInput = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = Color.White)
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            triggerTextScan(searchInput)
                        }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = EmeraldAccent,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedContainerColor = Color.White.copy(alpha = 0.08f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.04f),
                        cursorColor = EmeraldAccent
                    ),
                    singleLine = true
                )

                if (apiErrorMessage != null) {
                    Text(
                        text = apiErrorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp, start = 6.dp)
                    )
                }
            }

            // Quick Market Shelf (Predefined Demo Items to click and scan)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "TOUCH TO SCAN SHELF PRODUCTS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextSlate,
                    modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 8.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(MockProducts.list) { item ->
                        FrostedGlassCard(
                            modifier = Modifier
                                .width(150.dp)
                                .clickable {
                                    isLoadingProduct = true
                                    scope.launch {
                                        kotlinx.coroutines.delay(800) // Realistic scanning delay
                                        isLoadingProduct = false
                                        onProductScanned(item)
                                    }
                                },
                            cornerRadius = 16.dp,
                            borderColor = if (item.isEatable) EmeraldAccent.copy(alpha = 0.25f) else Color(0xFFF87171).copy(alpha = 0.25f),
                            backgroundColor = Color.White.copy(alpha = 0.05f)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                // Category Tag & Edible Indicator Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (item.isEatable) EmeraldAccent.copy(alpha = 0.2f) else Color(0xFFF87171).copy(alpha = 0.2f),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = item.category.split(" ").firstOrNull() ?: "",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (item.isEatable) EmeraldAccent else Color(0xFFF87171)
                                        )
                                    }

                                    // Veg/Non-Veg style green dot for eatable
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .border(1.dp, if (item.isEatable) EmeraldAccent else Color(0xFFF87171))
                                            .padding(2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape)
                                                .background(if (item.isEatable) EmeraldAccent else Color(0xFFF87171))
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = item.brand,
                                    fontSize = 10.sp,
                                    color = TextSlate
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "₹${item.price}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = EmeraldAccent
                                    )

                                    Icon(
                                        imageVector = Icons.Default.QrCode,
                                        contentDescription = "Scan",
                                        tint = EmeraldAccent.copy(alpha = 0.8f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun borderStrokeForItem(product: Product): androidx.compose.foundation.BorderStroke? {
    return if (product.isEatable) {
        androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.15f))
    } else {
        androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935).copy(alpha = 0.15f))
    }
}

// Concrete implementation of CameraX AndroidView preview
@Composable
fun CameraPreviewView(modifier: Modifier = Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = { context ->
            PreviewView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
        modifier = modifier,
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(previewView.context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview
                    )
                } catch (exc: Exception) {
                    Log.e("ScannerView", "Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(previewView.context))
        }
    )
}

// Visual simulation of a beautiful moving conveyor belt with products passing under a scanning beam
@Composable
fun VirtualConveyorAnimation(modifier: Modifier = Modifier) {
    val shelfItems = listOf(
        "Organic Fruit Basket" to Icons.Default.ShoppingBag,
        "Milky Gold Carton" to Icons.Default.WaterDrop,
        "Crispy Crunch Bag" to Icons.Default.LocalMall,
        "Detergent Power Tub" to Icons.Default.CleanHands,
        "Hand Sanitizer Sanitizer" to Icons.Default.Spa
    )

    val infiniteTransition = rememberInfiniteTransition(label = "conveyor_anim")
    val conveyorX by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "item_x"
    )

    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF121212), Color(0xFF232323))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Laser grids
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, Color(0xFF00E676).copy(alpha = 0.12f), RoundedCornerShape(20.dp))
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Virtual QR Simulator Running",
                color = Color.White.copy(alpha = 0.5f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Using local mock catalogs & Gemini engine",
                color = Color.White.copy(alpha = 0.35f),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Animated conveyer belt belt line
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(38.dp)
                    .background(Color(0xFF2A2A2A), RoundedCornerShape(12.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.CenterStart
            ) {
                // Moving product icon
                val currentIdx = remember(conveyorX) { (0 until shelfItems.size).random() }
                val currentItem = shelfItems[currentIdx % shelfItems.size]

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.35f)
                        .fillMaxHeight()
                        .offset(x = (180.dp * conveyorX))
                        .background(Color(0xFF00E676).copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFF00E676).copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 6.dp)
                    ) {
                        Icon(
                            imageVector = currentItem.second,
                            contentDescription = null,
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = currentItem.first.split(" ").first(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

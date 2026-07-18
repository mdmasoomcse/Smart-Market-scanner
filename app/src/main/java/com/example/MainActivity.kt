package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.Product
import com.example.ui.screens.FssaiVerifyView
import com.example.ui.screens.HistoryView
import com.example.ui.screens.ProductDetailSheet
import com.example.ui.screens.ScannerView
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.components.FrostedAmbientBackground
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.TextSlate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen()
            }
        }
    }
}

enum class NavigationTab {
    SCANNER,
    FSSAI_AUDITOR,
    HISTORY
}

@Composable
fun MainAppScreen() {
    var currentTab by remember { mutableStateOf(NavigationTab.SCANNER) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    val scannedHistory = remember { mutableStateListOf<Product>() }

    // Scaffold for managing the general page structure, Bottom Bar, and Sheet Overlay
    Scaffold(
        bottomBar = {
            NavigationBar(
                tonalElevation = 0.dp,
                containerColor = Color(0xFF0F172A).copy(alpha = 0.85f),
                modifier = Modifier.border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
            ) {
                NavigationBarItem(
                    selected = currentTab == NavigationTab.SCANNER,
                    onClick = { currentTab = NavigationTab.SCANNER },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scanner Tab"
                        )
                    },
                    label = { Text("Scanner") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldAccent,
                        selectedTextColor = EmeraldAccent,
                        unselectedIconColor = TextSlate,
                        unselectedTextColor = TextSlate,
                        indicatorColor = Color.White.copy(alpha = 0.08f)
                    )
                )

                NavigationBarItem(
                    selected = currentTab == NavigationTab.FSSAI_AUDITOR,
                    onClick = { currentTab = NavigationTab.FSSAI_AUDITOR },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "FSSAI Auditor"
                        )
                    },
                    label = { Text("FSSAI Checker") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldAccent,
                        selectedTextColor = EmeraldAccent,
                        unselectedIconColor = TextSlate,
                        unselectedTextColor = TextSlate,
                        indicatorColor = Color.White.copy(alpha = 0.08f)
                    )
                )

                NavigationBarItem(
                    selected = currentTab == NavigationTab.HISTORY,
                    onClick = { currentTab = NavigationTab.HISTORY },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Log Tab"
                        )
                    },
                    label = { Text("History Log") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldAccent,
                        selectedTextColor = EmeraldAccent,
                        unselectedIconColor = TextSlate,
                        unselectedTextColor = TextSlate,
                        indicatorColor = Color.White.copy(alpha = 0.08f)
                    )
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        FrostedAmbientBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
            ) {
                when (currentTab) {
                    NavigationTab.SCANNER -> {
                        ScannerView(
                            onProductScanned = { product ->
                                selectedProduct = product
                                // Add to history log if not already present to avoid redundancy
                                if (scannedHistory.none { it.name == product.name }) {
                                    scannedHistory.add(0, product)
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    NavigationTab.FSSAI_AUDITOR -> {
                        FssaiVerifyView(
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    NavigationTab.HISTORY -> {
                        HistoryView(
                            scannedProducts = scannedHistory,
                            onProductSelected = { product ->
                                selectedProduct = product
                            },
                            onClearHistory = {
                                scannedHistory.clear()
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Sheet/Overlay popup when a product is scanned/viewed
                selectedProduct?.let { product ->
                    ProductDetailSheet(
                        product = product,
                        onDismiss = { selectedProduct = null },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

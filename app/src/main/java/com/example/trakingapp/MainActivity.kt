package com.example.trakingapp

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trakingapp.ui.screens.AnalyticsScreen
import com.example.trakingapp.ui.screens.HistoryScreen
import com.example.trakingapp.ui.screens.NightstandScreen
import com.example.trakingapp.ui.theme.AmberPrimary
import com.example.trakingapp.ui.theme.DarkSurface
import com.example.trakingapp.ui.theme.DarkSurfaceVariant
import com.example.trakingapp.ui.theme.NightstandTheme
import com.example.trakingapp.ui.theme.PureBlack
import com.example.trakingapp.ui.theme.TextMuted
import com.example.trakingapp.viewmodel.SleepViewModel

data class NavTab(
    val title: String,
    val icon: ImageVector
)

class MainActivity : ComponentActivity() {

    private val viewModel: SleepViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NightstandTheme {
                val state by viewModel.uiState.collectAsState()

                // Keep screen on while tracking in Nightstand mode
                LaunchedEffect(state.isTracking) {
                    if (state.isTracking) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    } else {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }
                }

                MainScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: SleepViewModel) {
    val state by viewModel.uiState.collectAsState()
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    val tabs = listOf(
        NavTab("Nightstand", Icons.Default.Bedtime),
        NavTab("Riwayat", Icons.Default.History),
        NavTab("Analisis", Icons.Default.AutoGraph)
    )

    // When tracking is active, ensure we stay on the nightstand screen and hide bottom bar for true OLED dark mode
    val isTracking = state.isTracking

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = PureBlack,
        bottomBar = {
            if (!isTracking) {
                NavigationBar(
                    containerColor = DarkSurface,
                    tonalElevation = 0.dp
                ) {
                    tabs.forEachIndexed { index, tab ->
                        val isSelected = selectedTabIndex == index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { selectedTabIndex = index },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AmberPrimary,
                                selectedTextColor = AmberPrimary,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                                indicatorColor = DarkSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isTracking) androidx.compose.foundation.layout.PaddingValues(0.dp) else innerPadding)
        ) {
            if (isTracking) {
                NightstandScreen(state = state, viewModel = viewModel)
            } else {
                when (selectedTabIndex) {
                    0 -> NightstandScreen(state = state, viewModel = viewModel)
                    1 -> HistoryScreen(state = state, viewModel = viewModel)
                    2 -> AnalyticsScreen(state = state)
                }
            }
        }
    }
}

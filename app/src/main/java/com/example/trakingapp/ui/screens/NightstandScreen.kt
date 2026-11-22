package com.example.trakingapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trakingapp.sensor.LuxCategory
import com.example.trakingapp.ui.components.SummaryDialog
import com.example.trakingapp.ui.components.TimePickerModal
import com.example.trakingapp.ui.theme.AmberGlow
import com.example.trakingapp.ui.theme.AmberPrimary
import com.example.trakingapp.ui.theme.CardBorder
import com.example.trakingapp.ui.theme.DarkBackground
import com.example.trakingapp.ui.theme.DarkSurface
import com.example.trakingapp.ui.theme.DarkSurfaceVariant
import com.example.trakingapp.ui.theme.MintOptimal
import com.example.trakingapp.ui.theme.PureBlack
import com.example.trakingapp.ui.theme.RedBright
import com.example.trakingapp.ui.theme.TextMuted
import com.example.trakingapp.ui.theme.TextPrimary
import com.example.trakingapp.ui.theme.TextSecondary
import com.example.trakingapp.ui.theme.YellowDim
import com.example.trakingapp.viewmodel.SleepUiState
import com.example.trakingapp.viewmodel.SleepViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NightstandScreen(
    state: SleepUiState,
    viewModel: SleepViewModel
) {
    var showTimePicker by remember { mutableStateOf(false) }

    // Summary dialog shown when tracking ends
    state.completedSessionSummary?.let { summary ->
        SummaryDialog(
            session = summary,
            onDismiss = { viewModel.dismissSummaryDialog() }
        )
    }

    if (showTimePicker) {
        TimePickerModal(
            initialHour = state.targetHour,
            initialMinute = state.targetMinute,
            onTimeSelected = { h, m -> viewModel.setWakeTime(h, m) },
            onDismiss = { showTimePicker = false }
        )
    }

    if (state.isTracking) {
        ActiveNightstandDisplay(
            state = state,
            onStop = { viewModel.stopSleepTracking() },
            onSnooze = { viewModel.snoozeAlarm() },
            onDismissAlarm = { viewModel.dismissAlarm() },
            onSimulateLux = { lux -> viewModel.simulateLuxReading(lux) },
            onSimulateDisturbance = { viewModel.simulateDisturbance() },
            onSimulateFlip = { viewModel.simulateFlipToSnooze() }
        )
    } else {
        NightstandSetupDisplay(
            state = state,
            onOpenTimePicker = { showTimePicker = true },
            onStartTracking = { viewModel.startSleepTracking() },
            onTestAlarm = { viewModel.triggerAlarmManuallyForTesting() }
        )
    }
}

@Composable
private fun NightstandSetupDisplay(
    state: SleepUiState,
    onOpenTimePicker: () -> Unit,
    onStartTracking: () -> Unit,
    onTestAlarm: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        // App Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceVariant)
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bedtime,
                    contentDescription = null,
                    tint = AmberPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Nightstand Smart Alarm",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Sleep Environment Monitor",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Alarm Wake-up Time Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                .clickable { onOpenTimePicker() },
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = AmberPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Target Jam Bangun",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = AmberPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Ubah",
                                fontSize = 11.sp,
                                color = AmberPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = state.formattedTargetTime,
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "WIB",
                        fontSize = 16.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Alarm dilengkapi fitur Flip-to-Snooze otomatis menggunakan sensor gerak.",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hardware Sensors Readiness Section
        Text(
            text = "Status Sensor Perangkat",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Environment Sensor (Light)
            SensorStatusBadge(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.BrightnessLow,
                category = "Environment Sensor",
                sensorName = "Light Sensor",
                isAvailable = state.hasHardwareLightSensor,
                description = "Pantau intensitas lux kamar",
                accentColor = MintOptimal
            )

            // Motion Sensor (Accelerometer)
            SensorStatusBadge(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Vibration,
                category = "Motion Sensor",
                sensorName = "Accelerometer",
                isAvailable = state.hasHardwareAccelerometer,
                description = "Deteksi gerakan & Flip-to-Snooze",
                accentColor = AmberPrimary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Nightstand Feature Highlights
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Keunggulan Mode Nightstand",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                FeatureBulletItem(
                    icon = Icons.Default.Bedtime,
                    title = "OLED Pitch Black Display",
                    desc = "Tampilan serba hitam hemat daya & tidak menyilaukan mata di kamar tidur."
                )
                FeatureBulletItem(
                    icon = Icons.Default.LightMode,
                    title = "Pemantauan Cahaya Kamar",
                    desc = "Kategori Pitch Dark (<2 lux) memastikan kondisi terbaik hormon melatonin."
                )
                FeatureBulletItem(
                    icon = Icons.Default.ScreenRotation,
                    title = "Flip-to-Snooze",
                    desc = "Saat alarm bunyi, cukup balikkan ponsel menghadap meja untuk tunda 5 menit."
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main CTA: Start Monitoring
        Button(
            onClick = onStartTracking,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AmberPrimary,
                contentColor = PureBlack
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Mulai Pantau Tidur (Start Monitoring)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Optional quick test button
        OutlinedButton(
            onClick = onTestAlarm,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = AmberPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Uji Alarm & Gerakan Flip Sekarang",
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun ActiveNightstandDisplay(
    state: SleepUiState,
    onStop: () -> Unit,
    onSnooze: () -> Unit,
    onDismissAlarm: () -> Unit,
    onSimulateLux: (Float) -> Unit,
    onSimulateDisturbance: () -> Unit,
    onSimulateFlip: () -> Unit
) {
    var currentTimeString by remember { mutableStateOf("") }
    var showTools by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTimeString = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            kotlinx.coroutines.delay(1000)
        }
    }

    val luxColor = when (state.luxCategory) {
        LuxCategory.PITCH_DARK -> MintOptimal
        LuxCategory.DIM -> YellowDim
        LuxCategory.TOO_BRIGHT -> RedBright
    }

    val infiniteTransition = rememberInfiniteTransition(label = "alarm")
    val alarmPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alarmPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Status Bar: Target Wake & Battery/OLED friendly indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (state.isAlarmRinging) RedBright else MintOptimal)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (state.isAlarmRinging) "ALARM AKTIF" else "MONITORING TIDUR",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (state.isAlarmRinging) RedBright else TextSecondary,
                        letterSpacing = 1.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = null,
                        tint = AmberPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = state.formattedTargetTime,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                }
            }

            // Notification / Snooze Banner
            AnimatedVisibility(
                visible = state.snoozeBannerMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                state.snoozeBannerMessage?.let { msg ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceVariant)
                            .border(1.dp, AmberPrimary, RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = msg,
                            color = AmberPrimary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Center: Big Nightstand Clock & Elapsed Time
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentTimeString.ifEmpty { "00:00:00" },
                    fontSize = 62.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (state.isAlarmRinging) RedBright else AmberPrimary,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Durasi Sesi: ${state.formattedElapsedTime}",
                    fontSize = 14.sp,
                    color = TextMuted
                )
            }

            // ALARM RINGING CARD (if active)
            if (state.isAlarmRinging) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(alarmPulseAlpha)
                        .border(2.dp, RedBright, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = RedBright,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "WAKTUNYA BANGUN!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = RedBright
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceVariant)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ScreenRotation,
                                    contentDescription = null,
                                    tint = AmberPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Flip / Balikkan HP ke bawah untuk Snooze",
                                    fontSize = 12.sp,
                                    color = AmberPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onSnooze,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AmberPrimary,
                                    contentColor = PureBlack
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Snooze (5m)", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = onDismissAlarm,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = RedBright,
                                    contentColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Matikan", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // Live Sensor Metrics in Dark OLED style
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Light Sensor Monitor
                    NightstandMetricBox(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.BrightnessLow,
                        label = "Cahaya Kamar",
                        value = "${String.format("%.1f", state.currentLux)} lux",
                        subtext = state.luxCategory.label,
                        accentColor = luxColor
                    )

                    // Disturbance Monitor
                    NightstandMetricBox(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Vibration,
                        label = "Gangguan Gerak",
                        value = "${state.disturbanceCount} kali",
                        subtext = if (state.lastDisturbanceTime.isNotEmpty()) "Pukul ${state.lastDisturbanceTime}" else "Kondisi Tenang",
                        accentColor = if (state.disturbanceCount > 5) RedBright else MintOptimal
                    )
                }
            }

            // Bottom Actions: Testing Toggle & Stop Session
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Collapsible Simulation Panel for testing sensors
                AnimatedVisibility(visible = showTools) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Panel Simulasi Sensor (Pengujian)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    onClick = { onSimulateLux(0.5f) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("0.5 lux (Gelap)", fontSize = 11.sp, color = MintOptimal)
                                }
                                Button(
                                    onClick = { onSimulateLux(35.0f) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("35 lux (Terang)", fontSize = 11.sp, color = RedBright)
                                }
                                Button(
                                    onClick = onSimulateDisturbance,
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("+1 Gerak", fontSize = 11.sp, color = AmberPrimary)
                                }
                            }
                            if (state.isAlarmRinging) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = onSimulateFlip,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Simulasi Gerakan Flip Ponsel", fontSize = 12.sp, color = PureBlack)
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showTools = !showTools }
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (showTools) "Tutup Panel Uji" else "Uji Sensor",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Button(
                        onClick = onStop,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkSurfaceVariant,
                            contentColor = RedBright
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Akhiri Tidur",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NightstandMetricBox(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    subtext: String,
    accentColor: Color
) {
    Card(
        modifier = modifier
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                fontSize = 11.sp,
                color = accentColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SensorStatusBadge(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    category: String,
    sensorName: String,
    isAvailable: Boolean,
    description: String,
    accentColor: Color
) {
    Card(
        modifier = modifier
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isAvailable) MintOptimal.copy(alpha = 0.2f) else YellowDim.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isAvailable) "Aktif" else "Simulasi",
                        fontSize = 10.sp,
                        color = if (isAvailable) MintOptimal else YellowDim,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = category,
                fontSize = 10.sp,
                color = TextMuted
            )
            Text(
                text = sensorName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun FeatureBulletItem(
    icon: ImageVector,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AmberPrimary,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = desc,
                fontSize = 11.sp,
                color = TextMuted
            )
        }
    }
}

package com.example.trakingapp.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trakingapp.ui.theme.AmberPrimary
import com.example.trakingapp.ui.theme.CardBorder
import com.example.trakingapp.ui.theme.DarkBackground
import com.example.trakingapp.ui.theme.DarkSurface
import com.example.trakingapp.ui.theme.DarkSurfaceVariant
import com.example.trakingapp.ui.theme.MintOptimal
import com.example.trakingapp.ui.theme.RedBright
import com.example.trakingapp.ui.theme.TextMuted
import com.example.trakingapp.ui.theme.TextPrimary
import com.example.trakingapp.ui.theme.TextSecondary
import com.example.trakingapp.ui.theme.YellowDim
import com.example.trakingapp.viewmodel.SleepUiState

@Composable
fun AnalyticsScreen(
    state: SleepUiState
) {
    val scrollState = rememberScrollState()
    val stats = state.summaryStats

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoGraph,
                    contentDescription = null,
                    tint = AmberPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Statistik & Wawasan Tidur",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Ringkasan data SQLite & Analisis Sensor",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Aggregate Stats Grid
        Text(
            text = "Ringkasan Riwayat Lingkungan",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.NightlightRound,
                label = "Total Sesi",
                value = "${stats.totalNights} malam",
                accentColor = AmberPrimary
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Timeline,
                label = "Rerata Durasi",
                value = if (stats.totalNights > 0) stats.formattedAvgDuration else "0m",
                accentColor = MintOptimal
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.BrightnessLow,
                label = "Rerata Cahaya",
                value = "${String.format("%.1f", stats.avgRoomBrightness)} lux",
                accentColor = YellowDim
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Vibration,
                label = "Rerata Gangguan",
                value = "${String.format("%.1f", stats.avgDisturbances)}x",
                accentColor = RedBright
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sensor Diagnostic Lab Card
        Text(
            text = "Diagnostik Sensor Real-Time",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Light sensor real-time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BrightnessLow,
                            contentDescription = null,
                            tint = YellowDim,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Environment: Light Sensor",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (state.hasHardwareLightSensor) "Sensor Fisik Terhubung" else "Mode Simulasi",
                                fontSize = 10.sp,
                                color = if (state.hasHardwareLightSensor) MintOptimal else YellowDim
                            )
                        }
                    }

                    Text(
                        text = "${String.format("%.1f", state.currentLux)} lux",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = YellowDim
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(CardBorder)
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Accelerometer real-time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = null,
                            tint = AmberPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Motion: Accelerometer",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (state.hasHardwareAccelerometer) "Sensor Fisik Terhubung" else "Mode Simulasi",
                                fontSize = 10.sp,
                                color = if (state.hasHardwareAccelerometer) MintOptimal else YellowDim
                            )
                        }
                    }

                    Text(
                        text = if (state.isFaceDown) "Posisi: Face Down" else "Posisi: Normal",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (state.isFaceDown) MintOptimal else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Guidelines & Sleep Science
        Text(
            text = "Panduan Lingkungan Tidur Nyenyak",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        GuidelineItem(
            icon = Icons.Default.BrightnessLow,
            category = "< 2 lux (Pitch Dark)",
            title = "Kondisi Ideal Produksi Melatonin",
            content = "Kamar gelap total merangsang kelenjar pineal memproduksi hormon tidur melatonin secara maksimal, membantu masuk ke fase REM lebih cepat.",
            color = MintOptimal
        )

        Spacer(modifier = Modifier.height(10.dp))

        GuidelineItem(
            icon = Icons.Default.Lightbulb,
            category = "2 - 15 lux (Dim)",
            title = "Pencahayaan Remang / Lampu Tidur",
            content = "Dapat diterima jika takut gelap, namun disarankan memakai lampu bernuansa hangat (amber/merah) tanpa gelombang cahaya biru.",
            color = YellowDim
        )

        Spacer(modifier = Modifier.height(10.dp))

        GuidelineItem(
            icon = Icons.Default.ScreenRotation,
            category = "Sensor Gerak",
            title = "Kenapa Memakai Flip-to-Snooze?",
            content = "Menekan tombol di layar memaksa mata melihat cahaya terang HP saat baru bangun tidur. Gerakan flip mematikan suara tanpa merusak adaptasi mata gelap.",
            color = AmberPrimary
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun GuidelineItem(
    icon: ImageVector,
    category: String,
    title: String,
    content: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = category,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = content,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}

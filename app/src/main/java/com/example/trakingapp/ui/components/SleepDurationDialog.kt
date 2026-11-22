package com.example.trakingapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.trakingapp.ui.theme.AmberPrimary
import com.example.trakingapp.ui.theme.CardBorder
import com.example.trakingapp.ui.theme.DarkSurface
import com.example.trakingapp.ui.theme.DarkSurfaceVariant
import com.example.trakingapp.ui.theme.MintOptimal
import com.example.trakingapp.ui.theme.PureBlack
import com.example.trakingapp.ui.theme.TextMuted
import com.example.trakingapp.ui.theme.TextPrimary
import com.example.trakingapp.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class SleepPreset(
    val label: String,
    val subtitle: String,
    val totalMinutes: Int
)

@Composable
fun SleepDurationDialog(
    initialHours: Int = 7,
    initialMinutes: Int = 30,
    onStartMonitoring: (durationMinutes: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var hours by remember { mutableIntStateOf(initialHours) }
    var minutes by remember { mutableIntStateOf(initialMinutes) }

    val totalMinutes = (hours * 60 + minutes).coerceAtLeast(1)

    // Calculate wake-up time dynamically
    val wakeUpDate = remember(totalMinutes) {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, totalMinutes)
        cal.time
    }
    val wakeTimeString = remember(wakeUpDate) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(wakeUpDate)
    }

    val presets = listOf(
        SleepPreset("20 mnt", "Power Nap", 20),
        SleepPreset("45 mnt", "Refresh", 45),
        SleepPreset("1.5 jam", "1 Siklus", 90),
        SleepPreset("6 jam", "4 Siklus", 360),
        SleepPreset("7.5 jam", "5 Siklus (Ideal)", 450),
        SleepPreset("8 jam", "Standar", 480)
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon & Title
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(AmberPrimary.copy(alpha = 0.15f))
                        .border(1.dp, AmberPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = AmberPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Mau Tidur Berapa Lama?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "Pilih durasi tidur untuk timer alarm bangun pintar",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Custom Duration Adjuster
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurfaceVariant)
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hours
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DurationUnitPicker(
                            value = hours,
                            unit = "Jam",
                            onIncrement = { if (hours < 16) hours++ },
                            onDecrement = { if (hours > 0) hours-- }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        DurationUnitPicker(
                            value = minutes,
                            unit = "Menit",
                            onIncrement = {
                                if (minutes + 5 <= 55) {
                                    minutes += 5
                                } else {
                                    minutes = 0
                                    if (hours < 16) hours++
                                }
                            },
                            onDecrement = {
                                if (minutes - 5 >= 0) {
                                    minutes -= 5
                                } else if (hours > 0) {
                                    minutes = 55
                                    hours--
                                }
                            }
                        )
                    }

                    // Total Duration display
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (hours > 0) "${hours}j ${minutes}m" else "${minutes}m",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberPrimary
                        )
                        Text(
                            text = "Durasi Timer",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick presets grid
                Text(
                    text = "Pilihan Durasi Cepat (Siklus Tidur):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(115.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(presets) { preset ->
                        val isSelected = totalMinutes == preset.totalMinutes
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) AmberPrimary else DarkSurfaceVariant)
                                .border(
                                    1.dp,
                                    if (isSelected) AmberPrimary else CardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    hours = preset.totalMinutes / 60
                                    minutes = preset.totalMinutes % 60
                                }
                                .padding(vertical = 8.dp, horizontal = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = preset.label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) PureBlack else TextPrimary
                                )
                                Text(
                                    text = preset.subtitle,
                                    fontSize = 9.sp,
                                    color = if (isSelected) PureBlack.copy(alpha = 0.8f) else TextMuted,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Calculated Wake Up Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MintOptimal.copy(alpha = 0.12f))
                        .border(1.dp, MintOptimal.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = MintOptimal,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Alarm akan berdering pada:",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "Pukul $wakeTimeString WIB ($hours jam $minutes menit lagi)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MintOptimal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkSurfaceVariant,
                            contentColor = TextSecondary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = {
                            onStartMonitoring(totalMinutes)
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1.4f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberPrimary,
                            contentColor = PureBlack
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Mulai Sekarang",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DurationUnitPicker(
    value: Int,
    unit: String,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = unit,
            fontSize = 10.sp,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onDecrement,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(DarkSurface)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Kurang",
                    tint = TextPrimary,
                    modifier = Modifier.size(14.dp)
                )
            }

            Text(
                text = String.format(Locale.getDefault(), "%02d", value),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            IconButton(
                onClick = onIncrement,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(DarkSurface)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah",
                    tint = TextPrimary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

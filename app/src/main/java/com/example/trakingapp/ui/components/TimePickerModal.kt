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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.trakingapp.ui.theme.AmberPrimary
import com.example.trakingapp.ui.theme.CardBorder
import com.example.trakingapp.ui.theme.DarkSurface
import com.example.trakingapp.ui.theme.DarkSurfaceVariant
import com.example.trakingapp.ui.theme.PureBlack
import com.example.trakingapp.ui.theme.TextMuted
import com.example.trakingapp.ui.theme.TextPrimary
import com.example.trakingapp.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun TimePickerModal(
    initialHour: Int,
    initialMinute: Int,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedHour by remember { mutableIntStateOf(initialHour) }
    var selectedMinute by remember { mutableIntStateOf(initialMinute) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Atur Waktu Bangun",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "Alarm pintar akan berdering pada waktu ini",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Time Pickers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hours Column
                    NumberPickerColumn(
                        value = selectedHour,
                        range = 0..23,
                        onValueChange = { selectedHour = it }
                    )

                    Text(
                        text = ":",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Minutes Column
                    NumberPickerColumn(
                        value = selectedMinute,
                        range = 0..59,
                        onValueChange = { selectedMinute = it }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Quick presets
                Text(
                    text = "Pilihan Cepat",
                    fontSize = 12.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(
                        5 to 0,
                        5 to 30,
                        6 to 0,
                        6 to 30,
                        7 to 0
                    ).forEach { (h, m) ->
                        val label = String.format(Locale.getDefault(), "%02d:%02d", h, m)
                        val isSelected = selectedHour == h && selectedMinute == m
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AmberPrimary else DarkSurfaceVariant)
                                .clickable {
                                    selectedHour = h
                                    selectedMinute = m
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) PureBlack else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                            onTimeSelected(selectedHour, selectedMinute)
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberPrimary,
                            contentColor = PureBlack
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Simpan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun NumberPickerColumn(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = {
                val next = if (value + 1 > range.last) range.first else value + 1
                onValueChange(next)
            },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(DarkSurfaceVariant)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Tambah",
                tint = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(width = 80.dp, height = 70.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceVariant)
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = String.format(Locale.getDefault(), "%02d", value),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        IconButton(
            onClick = {
                val prev = if (value - 1 < range.first) range.last else value - 1
                onValueChange(prev)
            },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(DarkSurfaceVariant)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Kurang",
                tint = TextPrimary
            )
        }
    }
}

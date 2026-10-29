package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.prayer.QiblaInfo
import com.example.sensor.QiblaCompassManager
import com.example.ui.theme.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun QiblaScreen(
    qiblaInfo: QiblaInfo?,
    compassManager: QiblaCompassManager,
    modifier: Modifier = Modifier
) {
    val azimuth by compassManager.azimuth.collectAsState()
    val isSensorAvailable by compassManager.isSensorAvailable.collectAsState()

    // Smooth rotation animation
    val animatedAzimuth by animateFloatAsState(targetValue = azimuth, label = "compass_rotation")

    DisposableEffect(Unit) {
        compassManager.startListening()
        onDispose {
            compassManager.stopListening()
        }
    }

    val targetBearing = qiblaInfo?.qiblaBearing ?: 295f
    val difference = remember(azimuth, targetBearing) {
        abs((azimuth - targetBearing + 180f) % 360f - 180f)
    }
    val isAligned = difference <= 4f

    LaunchedEffect(azimuth) {
        if (isAligned) {
            compassManager.checkAndTriggerHaptic(targetBearing)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 100.dp)
            .testTag("qibla_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title and city info
        Text(
            text = "Kompas Arah Kiblat",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${qiblaInfo?.cityName ?: "Indonesia"} • Menghadap Ka'bah",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Status alignment banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = if (isAligned) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if (isAligned) GoldAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isAligned) Icons.Default.CheckCircle else Icons.Default.Explore,
                    contentDescription = null,
                    tint = if (isAligned) GoldAccent else EmeraldPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isAligned) "Alhamdulillah! Menghadap Kiblat Tepat" else "Putar ponsel hingga jarum emas tegak lurus",
                    color = if (isAligned) WarmWhite else MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Interactive Compass Canvas Dial
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ForestDark,
                            if (isAligned) EmeraldPrimary else Color(0xFF031A14)
                        )
                    )
                )
                .border(4.dp, if (isAligned) GoldAccent else AyahBorderGold, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.width / 2

                // Draw dial ticks
                for (i in 0 until 360 step 15) {
                    val angleRad = Math.toRadians((i - animatedAzimuth).toDouble())
                    val isMajor = i % 90 == 0
                    val tickLen = if (isMajor) 18f else 10f
                    val strokeW = if (isMajor) 3f else 1.5f
                    val tickColor = if (isMajor) Color(0xFFD4AF37) else Color(0x66FFFFFF)

                    val startX = (center.x + (radius - tickLen) * sin(angleRad)).toFloat()
                    val startY = (center.y - (radius - tickLen) * cos(angleRad)).toFloat()
                    val endX = (center.x + radius * sin(angleRad)).toFloat()
                    val endY = (center.y - radius * cos(angleRad)).toFloat()

                    drawLine(
                        color = tickColor,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = strokeW
                    )
                }

                // Draw Qibla Pointer Needle (Gold)
                val qiblaRelativeAngle = targetBearing - animatedAzimuth
                val qiblaRad = Math.toRadians(qiblaRelativeAngle.toDouble())

                val tipX = (center.x + (radius - 28f) * sin(qiblaRad)).toFloat()
                val tipY = (center.y - (radius - 28f) * cos(qiblaRad)).toFloat()

                // Gold Kaaba pointer indicator
                drawCircle(
                    color = Color(0xFFD4AF37),
                    radius = 8f,
                    center = Offset(tipX, tipY)
                )

                // Qibla direction arrow
                val arrowPath = Path().apply {
                    moveTo(tipX, tipY)
                    lineTo(
                        (center.x + 16f * cos(qiblaRad)).toFloat(),
                        (center.y + 16f * sin(qiblaRad)).toFloat()
                    )
                    lineTo(
                        (center.x - 16f * cos(qiblaRad)).toFloat(),
                        (center.y - 16f * sin(qiblaRad)).toFloat()
                    )
                    close()
                }
                drawPath(arrowPath, color = Color(0xFFD4AF37))

                // Compass Center circle
                drawCircle(
                    color = Color(0xFFD4AF37),
                    radius = 16f,
                    center = center
                )
                drawCircle(
                    color = Color(0xFF022C22),
                    radius = 8f,
                    center = center
                )
            }

            // Top North indicator fixed on device top
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
            ) {
                Surface(
                    color = if (isAligned) GoldAccent else EmeraldPrimary,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "▲ KIBLAT",
                        color = if (isAligned) ForestDark else WarmWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Center Degree Display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 80.dp)
            ) {
                Text(
                    text = "${animatedAzimuth.toInt()}°",
                    color = WarmWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Arah Kompas",
                    color = GoldLight,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Info Cards (Degrees & Distance)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Sudut Kiblat", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${String.format("%.1f", targetBearing)}°",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                    Text("Dari Utara Sejati", fontSize = 11.sp, color = GoldDark)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Jarak ke Ka'bah", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    val distKm = qiblaInfo?.distanceKm ?: 7920.0
                    Text(
                        text = "${distKm.toInt()} km",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                    Text("Makkah Al-Mukarramah", fontSize = 11.sp, color = GoldDark)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calibration tip
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Letakkan ponsel di permukaan datar dan jauhkan dari benda berbahan logam atau magnet untuk akurasi optimal.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

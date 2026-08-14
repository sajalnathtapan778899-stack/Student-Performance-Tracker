package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GradeDistribution
import com.example.data.model.StudentProgressPoint
import com.example.data.model.StudentSubjectAverage
import com.example.ui.theme.Amber500
import com.example.ui.theme.Amber50
import com.example.ui.theme.Amber600
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Navy900
import com.example.ui.theme.Rose500
import com.example.ui.theme.Rose50
import com.example.ui.theme.Rose600
import com.example.ui.theme.RoyalBlue50
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.RoyalBlue700
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import java.util.Locale

@Composable
fun StudentAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    gender: String = "Male"
) {
    val initial = name.trim().take(1).uppercase(Locale.getDefault()).ifEmpty { "S" }
    val bgColor = when (gender.lowercase(Locale.getDefault())) {
        "female" -> Color(0xFFF43F5E)
        else -> RoyalBlue600
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.42).sp
        )
    }
}

@Composable
fun GradeBadge(
    grade: String,
    modifier: Modifier = Modifier,
    size: String = "medium"
) {
    val (bgColor, textColor) = when (grade.uppercase(Locale.getDefault())) {
        "A+" -> Emerald50 to Emerald600
        "A" -> Emerald50 to Emerald600
        "B" -> RoyalBlue50 to RoyalBlue700
        "C" -> Amber50 to Amber600
        "D" -> Amber50 to Amber600
        "F" -> Rose50 to Rose600
        "ABS" -> Slate100 to Slate500
        else -> Slate100 to Slate700
    }

    val (paddingH, paddingV, fontSize) = when (size) {
        "small" -> Triple(6.dp, 2.dp, 11.sp)
        "large" -> Triple(12.dp, 6.dp, 16.sp)
        else -> Triple(8.dp, 4.dp, 13.sp)
    }

    Surface(
        modifier = modifier.clip(RoundedCornerShape(6.dp)),
        color = bgColor,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = grade,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize,
            modifier = Modifier.padding(horizontal = paddingH, vertical = paddingV)
        )
    }
}

@Composable
fun RankBadge(
    rank: Int,
    modifier: Modifier = Modifier,
    totalStudents: Int? = null
) {
    if (rank <= 0) {
        Surface(
            modifier = modifier.clip(RoundedCornerShape(6.dp)),
            color = Slate100
        ) {
            Text(
                text = "—",
                color = Slate400,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
        return
    }

    val (bgColor, textColor, icon) = when (rank) {
        1 -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), "🥇")
        2 -> Triple(Color(0xFFF1F5F9), Color(0xFF475569), "🥈")
        3 -> Triple(Color(0xFFFFEDD5), Color(0xFFC2410C), "🥉")
        else -> Triple(Slate100, Slate700, "#$rank")
    }

    Surface(
        modifier = modifier.clip(RoundedCornerShape(6.dp)),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (rank <= 3) {
                Text(text = icon, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "${rank}${getOrdinalSuffix(rank)}",
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            } else {
                Text(
                    text = if (totalStudents != null) "#$rank / $totalStudents" else "#$rank",
                    color = textColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

private fun getOrdinalSuffix(n: Int): String {
    return when {
        n in 11..13 -> "th"
        n % 10 == 1 -> "st"
        n % 10 == 2 -> "nd"
        n % 10 == 3 -> "rd"
        else -> "th"
    }
}

@Composable
fun PassFailChip(
    isPassed: Boolean,
    isAbsent: Boolean = false,
    modifier: Modifier = Modifier
) {
    val (text, bg, fg) = when {
        isAbsent -> Triple("ABSENT", Slate100, Slate500)
        isPassed -> Triple("PASS", Emerald50, Emerald600)
        else -> Triple("FAIL", Rose50, Rose600)
    }

    Surface(
        modifier = modifier.clip(RoundedCornerShape(4.dp)),
        color = bg
    ) {
        Text(
            text = text,
            color = fg,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun MetricStatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector,
    accentColor: Color = RoyalBlue600,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate400,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun TrendLineChart(
    points: List<StudentProgressPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = RoyalBlue600,
    passingPct: Double = 40.0
) {
    if (points.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Slate50),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No exam records available yet for trend analysis",
                style = MaterialTheme.typography.bodySmall,
                color = Slate400
            )
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, Slate200, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Performance Trend Over Time",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            val latest = points.lastOrNull()?.percentage ?: 0.0
            val first = points.firstOrNull()?.percentage ?: 0.0
            val diff = latest - first
            val diffColor = if (diff >= 0) Emerald600 else Rose600
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (diff >= 0) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = diffColor,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = String.format(Locale.getDefault(), "%+.1f%% overall", diff),
                    color = diffColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val width = size.width
                val height = size.height
                val padBottom = 20.dp.toPx()
                val padTop = 10.dp.toPx()
                val usableHeight = height - padBottom - padTop

                // Draw Grid Lines (100%, 75%, 50%, 40% Pass, 0%)
                val gridLevels = listOf(100f, 75f, 50f, 40f, 0f)
                gridLevels.forEach { level ->
                    val y = padTop + (1f - (level / 100f)) * usableHeight
                    drawLine(
                        color = if (level == passingPct.toFloat()) Rose500.copy(alpha = 0.5f) else Slate200,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = if (level == passingPct.toFloat()) 2f else 1f,
                        pathEffect = if (level == passingPct.toFloat()) PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f) else null
                    )
                }

                if (points.size == 1) {
                    val p = points.first()
                    val y = padTop + (1f - (p.percentage.toFloat() / 100f).coerceIn(0f, 1f)) * usableHeight
                    drawCircle(
                        color = lineColor,
                        radius = 8.dp.toPx(),
                        center = Offset(width / 2f, y)
                    )
                } else {
                    val stepX = width / (points.size - 1)
                    val coords = points.mapIndexed { index, pt ->
                        val x = index * stepX
                        val y = padTop + (1f - (pt.percentage.toFloat() / 100f).coerceIn(0f, 1f)) * usableHeight
                        Offset(x, y)
                    }

                    // Fill Gradient under curve
                    val fillPath = Path().apply {
                        moveTo(coords.first().x, coords.first().y)
                        coords.forEach { lineTo(it.x, it.y) }
                        lineTo(coords.last().x, height - padBottom)
                        lineTo(coords.first().x, height - padBottom)
                        close()
                    }

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(lineColor.copy(alpha = 0.25f), Color.Transparent),
                            startY = padTop,
                            endY = height - padBottom
                        )
                    )

                    // Stroke Path
                    val strokePath = Path().apply {
                        moveTo(coords.first().x, coords.first().y)
                        coords.forEach { lineTo(it.x, it.y) }
                    }

                    drawPath(
                        path = strokePath,
                        color = lineColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw Data Points
                    coords.forEachIndexed { i, offset ->
                        drawCircle(
                            color = Color.White,
                            radius = 5.dp.toPx(),
                            center = offset
                        )
                        drawCircle(
                            color = lineColor,
                            radius = 3.5.dp.toPx(),
                            center = offset
                        )
                    }
                }
            }
        }

        // Labels at bottom
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            points.forEach { pt ->
                Text(
                    text = pt.examTitle.take(8),
                    fontSize = 10.sp,
                    color = Slate400,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SubjectPerformanceBarChart(
    averages: List<StudentSubjectAverage>,
    modifier: Modifier = Modifier
) {
    if (averages.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, Slate200, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Text(
            text = "Subject-Wise Strength & Weakness",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))

        averages.forEach { sub ->
            val barColor = when {
                sub.averagePercentage >= 80.0 -> Emerald500
                sub.averagePercentage >= 60.0 -> RoyalBlue600
                sub.averagePercentage >= 40.0 -> Amber500
                else -> Rose500
            }

            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = sub.subjectName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Slate700
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f%%", sub.averagePercentage),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = barColor
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Slate100)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = (sub.averagePercentage / 100.0).toFloat().coerceIn(0f, 1f))
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(barColor)
                    )
                }
            }
        }
    }
}

@Composable
fun GradeDistributionCard(
    distributions: List<GradeDistribution>,
    modifier: Modifier = Modifier
) {
    val totalCount = distributions.sumOf { it.count }
    if (totalCount == 0) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, Slate200, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Text(
            text = "Grade Distribution",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Multi-segment horizontal bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(Slate100)
        ) {
            distributions.forEach { dist ->
                if (dist.count > 0) {
                    val color = try {
                        Color(android.graphics.Color.parseColor(dist.colorHex))
                    } catch (e: Exception) {
                        RoyalBlue600
                    }
                    Box(
                        modifier = Modifier
                            .weight(dist.count.toFloat())
                            .height(14.dp)
                            .background(color)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Grade Legend Badges
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            distributions.forEach { dist ->
                val color = try {
                    Color(android.graphics.Color.parseColor(dist.colorHex))
                } catch (e: Exception) {
                    RoyalBlue600
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = dist.grade,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Slate700
                        )
                    }
                    Text(
                        text = "${dist.count} (${dist.percentageOfClass.toInt()}%)",
                        fontSize = 10.sp,
                        color = Slate400
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    actionButton: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(RoyalBlue50),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = RoyalBlue600,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate700,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Slate400,
                textAlign = TextAlign.Center
            )
            if (actionButton != null) {
                Spacer(modifier = Modifier.height(16.dp))
                actionButton()
            }
        }
    }
}

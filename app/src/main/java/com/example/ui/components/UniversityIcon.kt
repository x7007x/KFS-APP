package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun CustomUniversityIcon(
    name: String,
    color: Color,
    modifier: Modifier = Modifier.size(24.dp)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        when (name) {
            "graduation_cap" -> {
                // Draw a beautiful academic hat
                val path = Path().apply {
                    // Diamond top head
                    moveTo(w * 0.5f, h * 0.15f)
                    lineTo(w * 0.9f, h * 0.4f)
                    lineTo(w * 0.5f, h * 0.65f)
                    lineTo(w * 0.1f, h * 0.4f)
                    close()

                    // Hat base cap
                    moveTo(w * 0.25f, h * 0.52f)
                    lineTo(w * 0.25f, h * 0.75f)
                    quadraticTo(w * 0.5f, h * 0.9f, w * 0.75f, h * 0.75f)
                    lineTo(w * 0.75f, h * 0.52f)
                }
                drawPath(path = path, color = color, style = Stroke(width = 2.dp.toPx()))

                // Tassel
                val tasselPath = Path().apply {
                    moveTo(w * 0.5f, h * 0.4f)
                    lineTo(w * 0.82f, h * 0.45f)
                    lineTo(w * 0.85f, h * 0.75f)
                }
                drawPath(path = tasselPath, color = color, style = Stroke(width = 1.5f.dp.toPx()))
            }
            "teacher" -> {
                // Drawing a person with background board/screen (representing a teacher/faculty)
                // Board
                drawRect(
                    color = color.copy(alpha = 0.2f),
                    topLeft = Offset(w * 0.1f, h * 0.1f),
                    size = Size(w * 0.8f, h * 0.5f)
                )
                drawRect(
                    color = color,
                    topLeft = Offset(w * 0.1f, h * 0.1f),
                    size = Size(w * 0.8f, h * 0.5f),
                    style = Stroke(width = 2.dp.toPx())
                )
                // Person head
                drawCircle(
                    color = color,
                    radius = w * 0.15f,
                    center = Offset(w * 0.5f, h * 0.58f)
                )
                // Person body arch
                val bodyPath = Path().apply {
                    moveTo(w * 0.2f, h * 0.95f)
                    quadraticTo(w * 0.5f, h * 0.65f, w * 0.8f, h * 0.95f)
                    close()
                }
                drawPath(path = bodyPath, color = color)
            }
            "eye" -> {
                // Vision Eye outer
                val eyePath = Path().apply {
                    moveTo(w * 0.1f, h * 0.5f)
                    quadraticTo(w * 0.5f, h * 0.15f, w * 0.9f, h * 0.5f)
                    quadraticTo(w * 0.5f, h * 0.85f, w * 0.1f, h * 0.5f)
                    close()
                }
                drawPath(path = eyePath, color = color, style = Stroke(width = 2.dp.toPx()))
                // Pupil inner
                drawCircle(
                    color = color,
                    radius = w * 0.15f,
                    center = Offset(w * 0.5f, h * 0.5f)
                )
            }
            "building" -> {
                // University structure columns & roof
                val path = Path().apply {
                    // Roof triangle
                    moveTo(w * 0.1f, h * 0.35f)
                    lineTo(w * 0.5f, h * 0.1f)
                    lineTo(w * 0.9f, h * 0.35f)
                    close()

                    // Foundation base
                    moveTo(w * 0.15f, h * 0.85f)
                    lineTo(w * 0.85f, h * 0.85f)
                    lineTo(w * 0.85f, h * 0.95f)
                    lineTo(w * 0.15f, h * 0.95f)
                    close()
                }
                drawPath(path = path, color = color)

                // Columns
                drawRect(color = color, topLeft = Offset(w * 0.22f, h * 0.35f), size = Size(w * 0.1f, h * 0.5f))
                drawRect(color = color, topLeft = Offset(w * 0.45f, h * 0.35f), size = Size(w * 0.1f, h * 0.5f))
                drawRect(color = color, topLeft = Offset(w * 0.68f, h * 0.35f), size = Size(w * 0.1f, h * 0.5f))
            }
            "diamond" -> {
                // Diamond shape
                val path = Path().apply {
                    moveTo(w * 0.5f, h * 0.1f) // top center
                    lineTo(w * 0.9f, h * 0.35f) // right corner
                    lineTo(w * 0.5f, h * 0.9f) // bottom center
                    lineTo(w * 0.1f, h * 0.35f) // left corner
                    close()

                    // Facet lines
                    moveTo(w * 0.1f, h * 0.35f)
                    lineTo(w * 0.9f, h * 0.35f)

                    moveTo(w * 0.3f, h * 0.35f)
                    lineTo(w * 0.5f, h * 0.9f)

                    moveTo(w * 0.7f, h * 0.35f)
                    lineTo(w * 0.5f, h * 0.9f)
                }
                drawPath(path = path, color = color, style = Stroke(width = 2.dp.toPx()))
            }
            "group" -> {
                // Draw 3 nested individuals representing council/group
                // Middle main person
                drawCircle(color = color, radius = w * 0.12f, center = Offset(w * 0.5f, h * 0.35f))
                val mainBody = Path().apply {
                    moveTo(w * 0.25f, h * 0.85f)
                    quadraticTo(w * 0.5f, h * 0.5f, w * 0.75f, h * 0.85f)
                    close()
                }
                drawPath(path = mainBody, color = color)

                // Left secondary person
                drawCircle(color = color.copy(alpha = 0.6f), radius = w * 0.1f, center = Offset(w * 0.25f, h * 0.45f))
                val leftBody = Path().apply {
                    moveTo(w * 0.08f, h * 0.9f)
                    quadraticTo(w * 0.25f, h * 0.6f, w * 0.42f, h * 0.9f)
                    close()
                }
                drawPath(path = leftBody, color = color.copy(alpha = 0.6f))

                // Right secondary person
                drawCircle(color = color.copy(alpha = 0.6f), radius = w * 0.1f, center = Offset(w * 0.75f, h * 0.45f))
                val rightBody = Path().apply {
                    moveTo(w * 0.58f, h * 0.9f)
                    quadraticTo(w * 0.75f, h * 0.6f, w * 0.92f, h * 0.9f)
                    close()
                }
                drawPath(path = rightBody, color = color.copy(alpha = 0.6f))
            }
            "newspaper" -> {
                // Newspaper sheet style with lines
                val path = Path().apply {
                    moveTo(w * 0.15f, h * 0.15f)
                    lineTo(w * 0.85f, h * 0.15f)
                    lineTo(w * 0.85f, h * 0.85f)
                    lineTo(w * 0.15f, h * 0.85f)
                    close()
                }
                drawPath(path = path, color = color, style = Stroke(width = 2.dp.toPx()))

                // Headline placeholder box
                drawRect(
                    color = color,
                    topLeft = Offset(w * 0.25f, h * 0.25f),
                    size = Size(w * 0.5f, h * 0.2f)
                )

                // Sub-text lines
                drawLine(color = color, start = Offset(w * 0.25f, h * 0.55f), end = Offset(w * 0.75f, h * 0.55f), strokeWidth = 2.dp.toPx())
                drawLine(color = color, start = Offset(w * 0.25f, h * 0.68f), end = Offset(w * 0.75f, h * 0.68f), strokeWidth = 2.dp.toPx())
            }
            "clipboard_results" -> {
                // Exam results clipboard
                val path = Path().apply {
                    moveTo(w * 0.25f, h * 0.25f)
                    lineTo(w * 0.75f, h * 0.25f)
                    lineTo(w * 0.75f, h * 0.85f)
                    lineTo(w * 0.25f, h * 0.85f)
                    close()
                }
                drawPath(path = path, color = color, style = Stroke(width = 2.dp.toPx()))

                // Clip top
                drawRect(
                    color = color,
                    topLeft = Offset(w * 0.4f, h * 0.13f),
                    size = Size(w * 0.2f, h * 0.12f)
                )

                // Checkmarks and score lines
                drawLine(color = color, start = Offset(w * 0.35f, h * 0.45f), end = Offset(w * 0.65f, h * 0.45f), strokeWidth = 1.5f.dp.toPx())
                drawLine(color = color, start = Offset(w * 0.35f, h * 0.6f), end = Offset(w * 0.65f, h * 0.6f), strokeWidth = 1.5f.dp.toPx())
                drawCircle(color = Color.Green, radius = 2.5f.dp.toPx(), center = Offset(w * 0.35f, h * 0.45f))
                drawCircle(color = Color.Green, radius = 2.5f.dp.toPx(), center = Offset(w * 0.35f, h * 0.6f))
            }
            "globe" -> {
                // Custom web globe
                drawCircle(color = color, radius = w * 0.38f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(width = 2.dp.toPx()))
                // Horizontal equator line
                drawLine(color = color, start = Offset(w * 0.12f, h * 0.5f), end = Offset(w * 0.88f, h * 0.5f), strokeWidth = 2.dp.toPx())
                // Latitude lines curves
                drawArc(
                    color = color,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.15f, h * 0.25f),
                    size = Size(w * 0.7f, h * 0.5f),
                    style = Stroke(width = 1.5f.dp.toPx())
                )
                drawArc(
                    color = color,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.15f, h * 0.25f),
                    size = Size(w * 0.7f, h * 0.5f),
                    style = Stroke(width = 1.5f.dp.toPx())
                )
                // Longitudinal lines loops
                drawArc(
                    color = color,
                    startAngle = 90f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.35f, h * 0.12f),
                    size = Size(w * 0.3f, h * 0.76f),
                    style = Stroke(width = 1.5f.dp.toPx())
                )
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.35f, h * 0.12f),
                    size = Size(w * 0.3f, h * 0.76f),
                    style = Stroke(width = 1.5f.dp.toPx())
                )
            }
            "military" -> {
                // Military shield/star training cap or shield badge
                val starPath = Path().apply {
                    val cx = w * 0.5f
                    val cy = h * 0.45f
                    val rOuter = w * 0.32f
                    val rInner = w * 0.14f
                    var first = true
                    for (i in 0..10) {
                        val angle = (Math.PI * i / 5) - (Math.PI / 2)
                        val r = if (i % 2 == 0) rOuter else rInner
                        val x = (cx + r * Math.cos(angle)).toFloat()
                        val y = (cy + r * Math.sin(angle)).toFloat()
                        if (first) {
                            moveTo(x, y)
                            first = false
                        } else {
                            lineTo(x, y)
                        }
                    }
                    close()
                }
                drawPath(path = starPath, color = color)

                // Bottom shelf lines
                drawLine(color = color, start = Offset(w * 0.2f, h * 0.85f), end = Offset(w * 0.8f, h * 0.85f), strokeWidth = 3.dp.toPx())
            }
            "search" -> {
                // Magnifying glass
                drawCircle(color = color, radius = w * 0.25f, center = Offset(w * 0.35f, h * 0.35f), style = Stroke(width = 2.dp.toPx()))
                drawLine(color = color, start = Offset(w * 0.53f, h * 0.53f), end = Offset(w * 0.85f, h * 0.85f), strokeWidth = 3.dp.toPx())
            }
            "envelope" -> {
                // Mail envelope
                val path = Path().apply {
                    moveTo(w * 0.1f, h * 0.25f)
                    lineTo(w * 0.9f, h * 0.25f)
                    lineTo(w * 0.9f, h * 0.75f)
                    lineTo(w * 0.1f, h * 0.75f)
                    close()
                }
                drawPath(path = path, color = color, style = Stroke(width = 2.dp.toPx()))

                val flap = Path().apply {
                    moveTo(w * 0.1f, h * 0.25f)
                    lineTo(w * 0.5f, h * 0.53f)
                    lineTo(w * 0.9f, h * 0.25f)
                }
                drawPath(path = flap, color = color, style = Stroke(width = 2.dp.toPx()))
            }
            "edit_note" -> {
                // Pencil with paper
                val paper = Path().apply {
                    moveTo(w * 0.2f, h * 0.15f)
                    lineTo(w * 0.7f, h * 0.15f)
                    lineTo(w * 0.85f, h * 0.3f)
                    lineTo(w * 0.85f, h * 0.85f)
                    lineTo(w * 0.2f, h * 0.85f)
                    close()
                }
                drawPath(path = paper, color = color, style = Stroke(width = 2.dp.toPx()))
                drawLine(color = color, start = Offset(w * 0.3f, h * 0.4f), end = Offset(w * 0.7f, h * 0.4f), strokeWidth = 2.dp.toPx())
                drawLine(color = color, start = Offset(w * 0.3f, h * 0.55f), end = Offset(w * 0.7f, h * 0.55f), strokeWidth = 2.dp.toPx())
                drawLine(color = color, start = Offset(w * 0.3f, h * 0.7f), end = Offset(w * 0.5f, h * 0.7f), strokeWidth = 2.dp.toPx())
                // Pencil
                drawLine(color = color, start = Offset(w * 0.7f, h * 0.6f), end = Offset(w * 0.6f, h * 0.85f), strokeWidth = 2.dp.toPx())
            }
            "medical_services" -> {
                // Plus/cross medical symbol
                drawRect(color = color.copy(alpha = 0.2f),
                    topLeft = Offset(w * 0.15f, h * 0.15f),
                    size = Size(w * 0.7f, h * 0.7f),
                )
                drawRect(color = color, style = Stroke(width = 2.dp.toPx()),
                    topLeft = Offset(w * 0.15f, h * 0.15f),
                    size = Size(w * 0.7f, h * 0.7f),
                )
                drawRect(color = color,
                    topLeft = Offset(w * 0.35f, h * 0.25f),
                    size = Size(w * 0.3f, h * 0.5f),
                )
                drawRect(color = color,
                    topLeft = Offset(w * 0.25f, h * 0.35f),
                    size = Size(w * 0.5f, h * 0.3f),
                )
            }
            "book" -> {
                // Open book
                val path = Path().apply {
                    moveTo(w * 0.1f, h * 0.2f)
                    quadraticTo(w * 0.3f, h * 0.1f, w * 0.5f, h * 0.3f)
                    quadraticTo(w * 0.7f, h * 0.1f, w * 0.9f, h * 0.2f)
                    lineTo(w * 0.9f, h * 0.8f)
                    quadraticTo(w * 0.7f, h * 0.65f, w * 0.5f, h * 0.85f)
                    quadraticTo(w * 0.3f, h * 0.65f, w * 0.1f, h * 0.8f)
                    close()
                }
                drawPath(path = path, color = color, style = Stroke(width = 2.dp.toPx()))
                drawLine(color = color, start = Offset(w * 0.5f, h * 0.3f), end = Offset(w * 0.5f, h * 0.85f), strokeWidth = 2.dp.toPx())
            }
            "home" -> {
                // Simple house
                val housePath = Path().apply {
                    moveTo(w * 0.5f, h * 0.1f)
                    lineTo(w * 0.9f, h * 0.4f)
                    lineTo(w * 0.9f, h * 0.85f)
                    lineTo(w * 0.1f, h * 0.85f)
                    lineTo(w * 0.1f, h * 0.4f)
                    close()
                }
                drawPath(path = housePath, color = color, style = Stroke(width = 2.dp.toPx()))
            }
            "description" -> {
                // Document with lines
                val path = Path().apply {
                    moveTo(w * 0.2f, h * 0.1f)
                    lineTo(w * 0.65f, h * 0.1f)
                    lineTo(w * 0.8f, h * 0.25f)
                    lineTo(w * 0.8f, h * 0.9f)
                    lineTo(w * 0.2f, h * 0.9f)
                    close()
                }
                drawPath(path = path, color = color, style = Stroke(width = 2.dp.toPx()))
                drawLine(color = color, start = Offset(w * 0.3f, h * 0.45f), end = Offset(w * 0.7f, h * 0.45f), strokeWidth = 2.dp.toPx())
                drawLine(color = color, start = Offset(w * 0.3f, h * 0.6f), end = Offset(w * 0.7f, h * 0.6f), strokeWidth = 2.dp.toPx())
                drawLine(color = color, start = Offset(w * 0.3f, h * 0.75f), end = Offset(w * 0.5f, h * 0.75f), strokeWidth = 2.dp.toPx())
            }
            "badge" -> {
                // ID badge with clip
                val path = Path().apply {
                    moveTo(w * 0.3f, h * 0.2f)
                    lineTo(w * 0.7f, h * 0.2f)
                    lineTo(w * 0.7f, h * 0.85f)
                    lineTo(w * 0.3f, h * 0.85f)
                    close()
                }
                drawPath(path = path, color = color, style = Stroke(width = 2.dp.toPx()))
                drawLine(color = color, start = Offset(w * 0.4f, h * 0.4f), end = Offset(w * 0.6f, h * 0.4f), strokeWidth = 2.dp.toPx())
                drawLine(color = color, start = Offset(w * 0.4f, h * 0.55f), end = Offset(w * 0.6f, h * 0.55f), strokeWidth = 2.dp.toPx())
                drawCircle(color = color, radius = w * 0.1f, center = Offset(w * 0.5f, h * 0.72f), style = Stroke(width = 2.dp.toPx()))
            }
            "calendar_today" -> {
                // Calendar grid
                val path = Path().apply {
                    moveTo(w * 0.1f, h * 0.25f)
                    lineTo(w * 0.9f, h * 0.25f)
                    lineTo(w * 0.9f, h * 0.85f)
                    lineTo(w * 0.1f, h * 0.85f)
                    close()
                }
                drawPath(path = path, color = color, style = Stroke(width = 2.dp.toPx()))
                drawLine(color = color, start = Offset(w * 0.1f, h * 0.45f), end = Offset(w * 0.9f, h * 0.45f), strokeWidth = 2.dp.toPx())
                drawLine(color = color, start = Offset(w * 0.3f, h * 0.45f), end = Offset(w * 0.3f, h * 0.85f), strokeWidth = 2.dp.toPx())
                drawLine(color = color, start = Offset(w * 0.6f, h * 0.45f), end = Offset(w * 0.6f, h * 0.85f), strokeWidth = 2.dp.toPx())
                drawLine(color = color, start = Offset(w * 0.45f, h * 0.1f), end = Offset(w * 0.45f, h * 0.25f), strokeWidth = 2.dp.toPx())
                drawLine(color = color, start = Offset(w * 0.55f, h * 0.1f), end = Offset(w * 0.55f, h * 0.25f), strokeWidth = 2.dp.toPx())
            }
            "wallet" -> {
                // Financial purse/wallet
                val path = Path().apply {
                    moveTo(w * 0.15f, h * 0.25f)
                    lineTo(w * 0.75f, h * 0.25f)
                    quadraticTo(w * 0.85f, h * 0.25f, w * 0.85f, h * 0.35f)
                    lineTo(w * 0.85f, h * 0.75f)
                    quadraticTo(w * 0.85f, h * 0.85f, w * 0.75f, h * 0.85f)
                    lineTo(w * 0.15f, h * 0.85f)
                    quadraticTo(w * 0.1f, h * 0.85f, w * 0.1f, h * 0.75f)
                    lineTo(w * 0.1f, h * 0.35f)
                    quadraticTo(w * 0.1f, h * 0.25f, w * 0.15f, h * 0.25f)
                    close()
                }
                drawPath(path = path, color = color, style = Stroke(width = 2.dp.toPx()))

                // Closing tab card slot
                drawRect(
                    color = color,
                    topLeft = Offset(w * 0.55f, h * 0.45f),
                    size = Size(w * 0.35f, h * 0.2f)
                )
                // Lock circle
                drawCircle(color = Color.White, radius = 2.dp.toPx(), center = Offset(w * 0.68f, h * 0.55f))
            }
            else -> {
                // Fallback default dot circles
                drawCircle(color = color, radius = w * 0.3f, center = Offset(w * 0.5f, h * 0.5f))
            }
        }
    }
}

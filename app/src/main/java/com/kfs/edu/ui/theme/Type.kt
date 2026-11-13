package com.kfs.edu.ui.theme

import androidx.compose.material3.Typography
import com.kfs.edu.R
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Weights used by the redesign: 400, 500, 600, 700 (the design artifact used IBM Plex Sans Arabic).
val MiSansArabicFont = FontFamily(
  Font(R.font.misansarabic_regular, FontWeight.Normal),
  Font(R.font.misansarabic_medium, FontWeight.Medium),
  Font(R.font.misansarabic_semibold, FontWeight.SemiBold),
  Font(R.font.misansarabic_bold, FontWeight.Bold),
)

val Typography =
  Typography(
    bodyLarge =
      TextStyle(
        fontFamily = MiSansArabicFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
      ),
    titleLarge =
      TextStyle(
        fontFamily = MiSansArabicFont,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
      ),
    titleMedium =
      TextStyle(
        fontFamily = MiSansArabicFont,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
      ),
    bodyMedium =
      TextStyle(
        fontFamily = MiSansArabicFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
      ),
    bodySmall =
      TextStyle(
        fontFamily = MiSansArabicFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
      ),
    labelSmall =
      TextStyle(
        fontFamily = MiSansArabicFont,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
      ),
  )

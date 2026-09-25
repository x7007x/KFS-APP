package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.HarDataCache
import com.example.data.RealStudentProfile
import com.example.ui.components.ServiceIcon
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Immutable
data class ProfileServiceItem(
    val label: String,
    val icon: String,
    val url: String,
    val webTitle: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentProfileScreen(
    profile: RealStudentProfile,
    onClose: () -> Unit,
    onOpenWebView: (url: String, title: String) -> Unit,
    onLogout: () -> Unit
) {
    val faculty = remember(profile) { HarDataCache.findFacultyByInfoId(profile.asFacultyInfoId.toInt()) }
    val collegeName = faculty?.factitel ?: "غير معروف"
    val collegeUrl = faculty?.let { "https://kfs.edu.eg/${it.facUrl}" } ?: "https://kfs.edu.eg"
    val firstName = profile.fullName.split(" ").firstOrNull() ?: profile.fullName
    val photoUrl = faculty?.let { "https://kfs.edu.eg/${it.facUrl}/photo/${profile.photo}" } ?: ""
    var showLogoutConfirm by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current
    val copiedField = remember { mutableStateOf("") }

    val studentServices = remember {
        listOf(
            ProfileServiceItem("نتائج الطلاب", "clipboard_results", "https://mis.kfs.edu.eg/newresult/", "نتائج الطلاب"),
            ProfileServiceItem("تسجيل المقررات", "edit_note", "https://mis.kfs.edu.eg/CreditHours_Reg/", "تسجيل مقررات الساعات المعتمدة"),
            ProfileServiceItem("الكشف الطبي", "medical_services", "https://mis.kfs.edu.eg/misclinic/", "الكشف الطبي"),
            ProfileServiceItem("الدفع الإلكتروني", "wallet", "https://payments.kfs.edu.eg/", "منظومة الدفع الالكتروني"),
            ProfileServiceItem("البريد الإلكتروني", "envelope", "https://kfs.edu.eg/displaytopic.aspx?topic=95559", "البريد الإلكتروني"),
            ProfileServiceItem("المكتبة الرقمية", "book", "https://kfs.edu.eg/displaytopic.aspx?topic=95556", "المكتبة الرقمية"),
            ProfileServiceItem("منصة المقررات", "graduation_cap", "https://thinqiacademy.ekb.eg/", "منصة المقررات الالكترونيه"),
            ProfileServiceItem("التربية العسكرية", "military", "https://kfs.edu.eg/milatrysite/", "التربية العسكرية"),
            ProfileServiceItem("سكن الطلاب", "home", "https://www.kfs.edu.eg/student_affair/display_dep.aspx?topic=68220&dep=1321", "سكن الطلاب"),
            ProfileServiceItem("الاستمارة النهائية", "description", "https://mis.kfs.edu.eg/last_year", "الاستماره الخاصه بالفرقه النهائية"),
        )
    }

    val collegeServices = remember {
        listOf(
            ProfileServiceItem("موقع الكلية", "globe", collegeUrl, collegeName),
            ProfileServiceItem("جداول ولوائح", "calendar_today", "https://kfs.edu.eg/news_mob.aspx?topic=${faculty?.facId ?: "1"}", "جداول ولوائح الكلية"),
            ProfileServiceItem("أخبار الكلية", "newspaper", "https://kfs.edu.eg/${faculty?.facUrl ?: "university"}", "أخبار الكلية"),
        )
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = {
                Text("تسجيل الخروج", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth())
            },
            text = {
                Text("هل أنت متأكد من تسجيل الخروج من حسابك؟", textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth())
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("تأكيد الخروج", color = Color.White) }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutConfirm = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KfuBlueDark,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = { Text("الملف الشخصي", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                },
                actions = {
                    IconButton(onClick = { showLogoutConfirm = true }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "تسجيل خروج", tint = Color.White)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(KfuBackground)
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = "greeting") {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    colors = CardDefaults.cardColors(containerColor = KfuSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, KfuBorder, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "مرحباً $firstName",
                                fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KfuBlue,
                                textAlign = TextAlign.Start
                            )
                            Text(
                                text = collegeName,
                                fontSize = 12.sp, color = KfuGreen, fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Start
                            )
                            Text(
                                text = "شعبة: ${profile.programName}",
                                fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Start
                            )
                        }
                        if (profile.photo.isNotBlank()) {
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = "صورة الطالب",
                                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).border(2.dp, KfuBlue, RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.logo_university),
                                contentDescription = "صورة الطالب",
                                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).border(2.dp, KfuBlue, RoundedCornerShape(12.dp))
                            )
                        }
                    }
                }
            }

            item(key = "personal_info") {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    colors = CardDefaults.cardColors(containerColor = KfuSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, KfuBorder, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.End) {
                        Text("البيانات الشخصية", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KfuBlue,
                            textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(12.dp))

                        ProfileFieldWithCopy("الاسم بالكامل", profile.fullName, clipboardManager, copiedField, "name")
                        ProfileField("الرقم القومي", profile.nationalNumber)
                        ProfileFieldWithCopy("كود الطالب", profile.studentCode, clipboardManager, copiedField, "code")
                        ProfileField("الكلية", collegeName)
                        ProfileField("الشعبة", profile.programName)
                        ProfileField("الفرقة/المستوى", profile.phaseParent)
                        ProfileField("الحالة", profile.caseDescr)
                        ProfileField("طبيعة الدراسة", profile.natureDescr)
                        ProfileField("نظام الدراسة", profile.methodDescr)
                        ProfileField("النوع", profile.gender)
                    }
                }
            }

            item(key = "account_info") {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    colors = CardDefaults.cardColors(containerColor = KfuSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, KfuBorder, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.End) {
                        Text("بيانات الحساب", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KfuBlue,
                            textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(12.dp))

                        ProfileFieldWithCopy("البريد الإلكتروني", profile.email, clipboardManager, copiedField, "email")
                        ProfileFieldWithCopy("كلمة المرور الابتدائية", profile.notes, clipboardManager, copiedField, "password")

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val emailLocal = profile.email.substringBefore("@")
                                val domain = profile.email.substringAfter("@")
                                val mailUrl = when {
                                    domain.contains("outlook") || domain.contains("office") || domain.contains("microsoft") ->
                                        "https://outlook.office.com/mail/"
                                    else -> "https://outlook.live.com/owa/"
                                }
                                onOpenWebView(mailUrl, "البريد الإلكتروني - $emailLocal")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = KfuBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("فتح البريد الإلكتروني", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item(key = "student_services") {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    colors = CardDefaults.cardColors(containerColor = KfuSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, KfuBorder, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.End) {
                        Text("خدمات الطلاب", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KfuBlue,
                            textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            studentServices.take(5).forEach { service ->
                                ServiceGridItem(
                                    label = service.label,
                                    iconName = service.icon,
                                    modifier = Modifier.weight(1f),
                                    onClick = { onOpenWebView(service.url, service.webTitle) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            studentServices.drop(5).take(5).forEach { service ->
                                ServiceGridItem(
                                    label = service.label,
                                    iconName = service.icon,
                                    modifier = Modifier.weight(1f),
                                    onClick = { onOpenWebView(service.url, service.webTitle) }
                                )
                            }
                        }
                    }
                }
            }

            item(key = "college_services") {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    colors = CardDefaults.cardColors(containerColor = KfuSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, KfuBorder, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.End) {
                        Text("خدمات الكلية", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KfuBlue,
                            textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            collegeServices.forEach { service ->
                                ServiceGridItem(
                                    label = service.label,
                                    iconName = service.icon,
                                    modifier = Modifier.weight(1f),
                                    onClick = { onOpenWebView(service.url, service.webTitle) }
                                )
                            }
                        }
                    }
                }
            }

            item(key = "logout") {
                OutlinedButton(
                    onClick = { showLogoutConfirm = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color.Red)
                    )
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.Red, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تسجيل الخروج", color = Color.Red, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ServiceGridItem(
    label: String,
    iconName: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() }
            .border(1.dp, KfuBorder, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = KfuSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ServiceIcon(name = iconName, color = KfuBlue, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                color = KfuTextPrimary, textAlign = TextAlign.Center, lineHeight = 13.sp)
        }
    }
}

@Composable
fun ProfileField(label: String, value: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = KfuTextPrimary,
            textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth())
        Text(text = label, fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth())
    }
    HorizontalDivider(color = KfuBorder, thickness = 0.5.dp)
}

@Composable
fun ProfileFieldWithCopy(label: String, value: String, clipboardManager: androidx.compose.ui.platform.ClipboardManager, copiedField: MutableState<String>, fieldKey: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                clipboardManager.setText(AnnotatedString(value))
                copiedField.value = fieldKey
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = KfuTextPrimary,
                textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth())
            Text(text = label, fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth())
        }
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = if (copiedField.value == fieldKey) Icons.Default.Check else Icons.Default.ContentCopy,
            contentDescription = if (copiedField.value == fieldKey) "تم النسخ" else "نسخ",
            tint = if (copiedField.value == fieldKey) KfuGreen else Color.Gray,
            modifier = Modifier.size(18.dp)
        )
    }
    HorizontalDivider(color = KfuBorder, thickness = 0.5.dp)
}

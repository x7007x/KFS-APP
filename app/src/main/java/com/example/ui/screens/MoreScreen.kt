package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.KfuNetworkClient
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen() {
    var showMessageDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(KfuBackground),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "message_btn") {
            Button(
                onClick = { showMessageDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = KfuBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.Email, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("إرسال رسالة للجامعة", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        item(key = "links") {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(containerColor = KfuSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, KfuBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    MoreLinkItem(title = "الموقع الإلكتروني الرسمي للجامعة", desc = "www.kfs.edu.eg")
                    HorizontalDivider(color = KfuBorder)
                    MoreLinkItem(title = "مركز تطوير نظم المعلومات والخدمات", desc = "itc.kfs.edu.eg")
                }
            }
        }

        item(key = "contact_header") {
            Text(
                text = "تفاصيل الاتصال بالجامعة",
                fontSize = 15.sp, fontWeight = FontWeight.Bold, color = KfuBlue,
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right
            )
        }

        item(key = "contact_info") {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(containerColor = KfuSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, KfuBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ContactRow(icon = Icons.Default.Business, label = "الاسم", value = "جامعة كفر الشيخ")
                    HorizontalDivider(color = KfuBorder, modifier = Modifier.padding(vertical = 8.dp))
                    ContactRow(icon = Icons.Default.LocationOn, label = "العنوان", value = "مصر، كفر الشيخ")
                    HorizontalDivider(color = KfuBorder, modifier = Modifier.padding(vertical = 8.dp))
                    ContactRow(icon = Icons.Default.Phone, label = "الهاتف", value = "002-047-3109590")
                    HorizontalDivider(color = KfuBorder, modifier = Modifier.padding(vertical = 8.dp))
                    ContactRow(icon = Icons.Default.Email, label = "البريد", value = "President@kfs.edu.eg")
                    HorizontalDivider(color = KfuBorder, modifier = Modifier.padding(vertical = 8.dp))
                    ContactRow(icon = Icons.Default.Language, label = "الموقع", value = "kfs.edu.eg")
                }
            }
        }
    }

    if (showMessageDialog) {
        MessageDialog(onDismiss = { showMessageDialog = false })
    }
}



@Composable
private fun ContactRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = KfuBlue,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = KfuTextPrimary,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun MoreLinkItem(title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = KfuTextPrimary, textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth())
            Text(desc, fontSize = 11.sp, color = KfuBlue, textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth())
        }
        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
    }
}

@Composable
fun MessageDialog(onDismiss: () -> Unit) {
    var senderName by remember { mutableStateOf("") }
    var senderEmail by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var sentSuccess by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = { if (!isSending) onDismiss() }) {
        Card(
            modifier = Modifier.fillMaxWidth().wrapContentHeight().border(1.dp, KfuBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(containerColor = KfuSurface)
        ) {
            Box {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.End) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { if (!isSending) onDismiss() }) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.Gray)
                        }
                        Text("إرسال رسالة للجامعة", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = KfuBlue)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (sentSuccess) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = KfuGreen, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("تم إرسال رسالتك بنجاح", color = KfuGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = KfuBlue),
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("إغلاق", color = Color.White) }
                        return@Column
                    }

                    val fieldColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KfuBlue,
                        unfocusedBorderColor = Color.LightGray,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = KfuBlue,
                        disabledTextColor = Color.Black,
                        disabledLabelColor = Color.Gray,
                        disabledBorderColor = Color.LightGray,
                        disabledPlaceholderColor = Color.Gray
                    )
                    val fieldStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right, color = Color.Black)

                    OutlinedTextField(
                        value = senderName, onValueChange = { senderName = it; errorMsg = "" },
                        label = { Text("الاسم") },
                        readOnly = isSending, singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = fieldStyle, colors = fieldColors
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = senderEmail, onValueChange = { senderEmail = it; errorMsg = "" },
                        label = { Text("البريد الإلكتروني") },
                        readOnly = isSending, singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = fieldStyle, colors = fieldColors
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = subject, onValueChange = { subject = it; errorMsg = "" },
                        label = { Text("الموضوع") },
                        readOnly = isSending, singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = fieldStyle, colors = fieldColors
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = message, onValueChange = { message = it; errorMsg = "" },
                        label = { Text("الرسالة") },
                        readOnly = isSending,
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        textStyle = fieldStyle, colors = fieldColors
                    )

                    if (errorMsg.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(errorMsg, color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (isSending) return@Button
                            if (senderName.isBlank() || senderEmail.isBlank() || subject.isBlank() || message.isBlank()) {
                                errorMsg = "يرجى ملء جميع الحقول"
                            } else {
                                isSending = true
                                scope.launch {
                                    val ok = KfuNetworkClient.sendUniversityMessage(senderName, senderEmail, subject, message)
                                    isSending = false
                                    if (ok) sentSuccess = true
                                    else errorMsg = "فشل الإرسال. حاول مرة أخرى."
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KfuBlue),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                        } else {
                            Text("إرسال", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

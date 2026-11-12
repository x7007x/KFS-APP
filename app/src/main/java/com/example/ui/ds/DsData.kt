package com.example.ui.ds

// Generated from the published design artifact. Do not edit by hand.

data class Svc(val label: String, val icon: String, val url: String, val title: String)
data class QuickLink(val label: String, val icon: String, val url: String, val title: String)

val SvcStudents = listOf(
    Svc("نتائج الطلاب", "clip", "https://mis.kfs.edu.eg/newresult/", "نتائج الطلاب"),
    Svc("تسجيل المقررات", "note", "https://mis.kfs.edu.eg/CreditHours_Reg/", "تسجيل مقررات الساعات المعتمدة"),
    Svc("التنسيق الإلكتروني", "globe", "https://portal.kfs.edu.eg/arts", "التنسيق الالكتروني"),
    Svc("الكشف الطبي", "med", "https://mis.kfs.edu.eg/misclinic/", "الكشف الطبي"),
    Svc("الدفع الإلكتروني", "wallet", "https://payments.kfs.edu.eg/", "منظومة الدفع الالكتروني"),
    Svc("البريد الإلكتروني", "mail", "https://kfs.edu.eg/displaytopic.aspx?topic=95559", "البريد الإلكتروني"),
    Svc("ميثاق الطالب", "doc", "https://scu.eg//News/News_img/15359661961298821989.pdf", "ميثاق الطالب"),
    Svc("منصة المقررات", "cap", "https://thinqiacademy.ekb.eg/", "منصة المقررات الالكترونيه"),
    Svc("التربية العسكرية", "shield", "https://kfs.edu.eg/milatrysite/", "التربية العسكرية"),
    Svc("المكتبة الرقمية", "book", "https://kfs.edu.eg/displaytopic.aspx?topic=95556", "المكتبة الرقمية"),
    Svc("سكن الطلاب", "home", "https://www.kfs.edu.eg/student_affair/display_dep.aspx?topic=68220&dep=1321", "سكن الطلاب"),
    Svc("الاستمارة النهائية", "doc", "https://mis.kfs.edu.eg/last_year", "الاستماره الخاصه بالفرقه النهائية")
)

val SvcStaff = listOf(
    Svc("دليل أعضاء التدريس", "group", "https://mis.kfs.edu.eg/stff/", "دليل أعضاء هيئة التدريس"),
    Svc("البحث عن عضو تدريس", "search", "https://www.kfs.edu.eg/SEARCH_STAFF_UNI.ASPX", "البحث عن عضو هيئة تدريس"),
    Svc("البريد الإلكتروني", "mail", "https://kfs.edu.eg/university/displaytopic.aspx?topic=95533", "البريد الإلكتروني"),
    Svc("المكتبة الرقمية", "book", "https://kfs.edu.eg/displaytopic.aspx?topic=95556", "المكتبة الرقمية"),
    Svc("دورات تنمية القدرات", "cap", "https://forms.gle/qox2UhgYGyVYtL2KA", "دورات تنمية قدرات أعضاء هيئة التدريس"),
    Svc("منصة المقررات thinqi", "globe", "https://sso.eetest.online/login", "منصة المقررات الالكترونيه"),
    Svc("اختبارات القدرات", "clip", "https://moodle.kfs.edu.eg/", "اختبارات تنمية قدرات أعضاء هيئة التدريس"),
    Svc("دورات التحول الرقمي", "cal", "https://kfs-ictt.metatec.app/", "حجز دورات التحول الرقمى"),
    Svc("الهوية الرقمية RDI", "badge", "https://www.kfs.edu.eg/display_dep.aspx?topic=87805&dep=1504", "الهوية الرقمية للباحث"),
    Svc("تسجيل الأبحاث", "note", "https://mis.kfs.edu.eg/papers/Home/Index", "تسجيل الابحاث"),
    Svc("صمم موقعك بنفسك", "globe", "https://www.kfs.edu.eg/control_cv/", "صمم موقعك بنفسك"),
    Svc("المجلات العلمية", "book", "https://kfs.edu.eg/display_dep.aspx?topic=76465&dep=1", "المجلات العلمية المعتمدة"),
    Svc("المعامل البحثية", "med", "https://kfs.edu.eg/display_dep.aspx?topic=59665&dep=1504", "المعامل البحثية"),
    Svc("المراكز البحثية", "building", "https://kfs.edu.eg/display_dep.aspx?topic=75534&dep=1504", "المراكز البحثية")
)

val QuickLinks = listOf(
    QuickLink("مجلس جامعة\nكفر الشيخ", "group", "https://kfs.edu.eg/news_mob.aspx?topic=85455", "مجلس جامعة كفر الشيخ"),
    QuickLink("لماذا جامعة\nكفر الشيخ", "diamond", "https://kfs.edu.eg/display.aspx?topic=70705", "لماذا جامعة كفر الشيخ؟"),
    QuickLink("عن الجامعة", "building", "https://kfs.edu.eg/display.aspx?topic=74339", "عن جامعة كفر الشيخ"),
    QuickLink("رؤية ورسالة\nالجامعة", "eye", "https://kfs.edu.eg/display.aspx?topic=1054", "رؤية ورسالة الجامعة")
)


val CardGradients: List<Pair<Long, Long>> = listOf(
    0xFF2F86D9 to 0xFF0D3D75,
    0xFF12A766 to 0xFF0B6B45,
    0xFF3B6FD1 to 0xFF1A8F9B,
    0xFF1A5CAA to 0xFF0A8A52,
    0xFF5A7DE0 to 0xFF1A5CAA,
    0xFF16939B to 0xFF0D3D75
)

val TileGradients: List<Pair<Long, Long>> = listOf(
    0xFF4AA3FF to 0xFF1A5CAA,
    0xFF2FD08A to 0xFF00874A,
    0xFFFFB340 to 0xFFE07B00,
    0xFFFF6F7A to 0xFFD93644,
    0xFF9A7BFF to 0xFF5B3FD6,
    0xFF37C8D6 to 0xFF0F7F95,
    0xFFFF8A5C to 0xFFD4521F,
    0xFF5CD05F to 0xFF2A8F2E
)

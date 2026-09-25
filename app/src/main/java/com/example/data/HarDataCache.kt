package com.example.data

object HarDataCache {

    data class HarNewsItem(
        val topicId: Int,
        val titleArabic: String,
        val picture: String,
        val date: String = "",
        val facultyId: Int = 0,
        val facultyFolder: String = ""
    )

    data class HarFacultyItem(
        val id: Int,
        val facId: Int,
        val facUrl: String,
        val facLogo: String,
        val factitel: String,
        val factype: Int,
        val asFacultyInfoId: Int
    )

    data class HarFacultyTopic(
        val facId: Int,
        val topicId: Int,
        val titleArabic: String,
        val topicPic: String,
        val idi: Int,
        val facName: String?,
        val id: Int
    )

    data class HarSocialInfo(
        val facId: Int,
        val youtube: String,
        val facebook: String,
        val phone: String,
        val location: String,
        val email: String
    )

    // All faculties parsed from HAR
    val allFaculties: List<HarFacultyItem> by lazy {
        listOf(
            HarFacultyItem(12, 7, "agre", "agre.png", "كلية الزراعة", 1, 1),
            HarFacultyItem(13, 12, "engineering", "engnew2.png", "كلية الهندسة", 1, 7),
            HarFacultyItem(14, 26, "science", "sci.png", "كلية العلوم", 1, 296),
            HarFacultyItem(15, 255, "fisheries", "fish.png", "كلية علوم الثروة السمكية", 1, 536),
            HarFacultyItem(17, 844, "computers", "comp.png", "كلية الحاسبات والمعلومات", 1, 556),
            HarFacultyItem(18, 1145, "nano_inst", "nano.png", "معهد علوم وتكنولوجيا النانو", 1, 592),
            HarFacultyItem(21, 1518, "ai", "ai.png", "كلية الذكاء الإصطناعي", 1, 602),
            HarFacultyItem(22, 9, "vet", "vet.png", "كلية الطب البيطري", 2, 2),
            HarFacultyItem(23, 228, "pharmacy", "ph.png", "كلية الصيدلة", 2, 488),
            HarFacultyItem(24, 233, "medcine", "med.png", "كلية الطب البشري", 2, 541),
            HarFacultyItem(25, 272, "nur", "nur.png", "كلية التمريض", 2, 540),
            HarFacultyItem(26, 273, "pt", "pt.png", "كلية العلاج الطبيعي", 2, 542),
            HarFacultyItem(28, 300, "dent", "dent.png", "كلية طب الفم والأسنان", 2, 550),
            HarFacultyItem(29, 1421, "nur_inst", "nurinst.png", "المعهد الفني للتمريض", 2, 597),
            HarFacultyItem(1, 8, "education", "edu.png", "كلية التربية", 3, 9),
            HarFacultyItem(4, 11, "arts", "arts.png", "كلية الآداب", 3, 8),
            HarFacultyItem(5, 13, "com", "com.png", "كلية التجارة", 3, 3),
            HarFacultyItem(6, 10, "specific", "spe.png", "كلية التربية النوعية", 3, 5),
            HarFacultyItem(7, 14, "sports", "sports.png", "كلية التربية الرياضية", 3, 6),
            HarFacultyItem(10, 1052, "languages", "lang.png", "كلية الآلسن", 3, 575),
            HarFacultyItem(11, 3066, "law", "lawlogo1.png", "كلية الحقوق", 3, 603),
            HarFacultyItem(31, 1800, "milatrysite", "tarbiaaskrya.png", "التربية العسكرية", 4, 1800)
        )
    }

    val scientificColleges: List<HarFacultyItem> get() = allFaculties.filter { it.factype == 1 }
    val medicalColleges: List<HarFacultyItem> get() = allFaculties.filter { it.factype == 2 }
    val humanitiesColleges: List<HarFacultyItem> get() = allFaculties.filter { it.factype == 3 }

    // Sample faculty topics from HAR (Engineering facid=12)
    val engineeringTopics: List<HarFacultyTopic> by lazy {
        listOf(
            HarFacultyTopic(12, 17251, "الجداول الدراسية", "timetable.png", 1, " هندسة    ", 6),
            HarFacultyTopic(12, 65716, "جداول الامتحانات", "image_picker2669708096768098870.png", 2, " هندسة    ", 67),
            HarFacultyTopic(12, 17259, "ارقام الجلوس", "image_picker1782213250894674325.png", 3, null, 86),
            HarFacultyTopic(12, 17258, "أماكن اللجان", "image_picker3288099348718728671.png", 3, " هندسة    ", 34)
        )
    }

    val nursingInstituteTopics: List<HarFacultyTopic> by lazy {
        listOf(
            HarFacultyTopic(1421, 94764, "جداول الامتحانات", "exam.png", 1, " مفتمريض  ", 50),
            HarFacultyTopic(1421, 96327, "الجداول الدراسيه", "image_picker6457603303406435387.png", 1, null, 58),
            HarFacultyTopic(1421, 94766, "أرقام الجلوس", "image_picker1036999350230665521.png", 2, " مفتمريض  ", 51),
            HarFacultyTopic(1421, 94765, "أماكن اللجان", "image_picker1319129059356690885.png", 3, " مفتمريض  ", 52)
        )
    }

    val militaryTopics: List<HarFacultyTopic> by lazy {
        listOf(
            HarFacultyTopic(1800, 84157, "محاضرات دورات التربية العسكرية", "image_picker6887134062048721235.png", 1, " عسكرية   ", 57),
            HarFacultyTopic(1800, 84163, "شروط إجتياز دورة التربية العسكرية", "image_picker7100503602309313867.png", 2, " عسكرية   ", 72),
            HarFacultyTopic(1800, 85485, "التسجيل في دورات التربية العسكرية", "image_picker1285118427629959656.png", 3, " عسكرية   ", 77),
            HarFacultyTopic(1800, 77404, "صفحتنا الرسمية على الفيسبوك", "image_picker7429463032966043052.png", 5, null, 95)
        )
    }

    // Social media from HAR
    val socialInfo: Map<Int, HarSocialInfo> by lazy {
        mapOf(
            12 to HarSocialInfo(12, "https://www.youtube.com/channel/UCpASKLWDCVcbIsV5PPIT7ZQ", "fb://page/468726586547255", "+20473109533", "https://goo.gl/maps/rmpgZqKucmFcHirV8", "لا يوجد"),
            1421 to HarSocialInfo(1421, "لا يوجد", "fb://page/121668545903843", "+20473214998", "https://goo.gl/maps/d1kQKwQWMei6cdAm8", "لا يوجد"),
            1800 to HarSocialInfo(1800, "لا يوجد", "لا يوجد", "لا يوجد", "لا يوجد", "لا يوجد")
        )
    }

    // University-wide news (facid=1) sampled from HAR
    val universityNews: List<HarNewsItem> by lazy {
        listOf(
            HarNewsItem(116528, "من بين 172 خبيراً في مصر.. طالب بهندسة كفر الشيخ يحصد رعاية وتكريم من رئيس الجمهورية", "125202621261430.jpg", "2026-05-21"),
            HarNewsItem(116527, "جامعة كفر الشيخ تشارك في النسخة الرابعة من المؤتمر السنوي الدولي للتحول الرقمي", "12520261454157.jpg", "2026-05-19"),
            HarNewsItem(116526, "ختام فعاليات الأسبوع البيئي السادس بجامعة كفر الشيخ", "125202614252335.jpg", "2026-05-18"),
            HarNewsItem(116524, "جامعة كفر الشيخ تعلن عن فتح باب التقدم لجائزة الجامعة التقديرية", "125202614010826.jpg", "2026-05-17"),
            HarNewsItem(116523, "رئيس جامعة كفر الشيخ يشارك في ملتقى التعليم العالي بالعاصمة الإدارية", "125202613591117.jpg", "2026-05-16"),
            HarNewsItem(116521, "جامعة كفر الشيخ تحصل على المركز الأول في مسابقة الابتكار الزراعي", "125202613473330.jpg", "2026-05-15"),
            HarNewsItem(116520, "وفد من جامعة كفر الشيخ يزور جامعة طوكيو لتعزيز التعاون العلمي", "125202613253667.jpg", "2026-05-14"),
            HarNewsItem(116359, "جدول امتحانات الدراسات العليا للعام الجامعي 2025/2026", "105202614531518.jpg", "2026-05-10"),
            HarNewsItem(116358, "إعلان نتائج الترم الأول للعام الجامعي 2025/2026", "10520261445566.jpg", "2026-05-09"),
            HarNewsItem(116356, "زيارة وفد من هيئة ضمان الجودة والاعتماد لكلية الهندسة", "10520261430271.jpg", "2026-05-08"),
            HarNewsItem(116355, "ندوة توعوية حول المخدرات بكلية التربية", "10520261422624.jpg", "2026-05-07"),
            HarNewsItem(116354, "جامعة كفر الشيخ تطلق مبادرة (كفر الشيخ خضراء)", "10520261411211.jpg", "2026-05-06"),
            HarNewsItem(116409, "تهنئة بمناسبة الاحتفال باليوم العالمي للتمريض", "12520261422597.jpg", "2026-05-12"),
            HarNewsItem(116353, "المؤتمر العلمي السادس والدولي الرابع لكلية التمريض جامعة كفر الشيخ", "10520261147110.jpg", "2026-05-05"),
            HarNewsItem(115774, "اللقاء الفكري الموسع الذي نظمته أسرة طلاب (من أجل مصر) بالجامعة", "5420261214117.jpg", "2026-04-28"),
            HarNewsItem(115771, "اللقاء الفكري لأسرة طلاب من أجل مصر بكلية الطب البشري", "5420261212218.jpg", "2026-04-27"),
            HarNewsItem(115768, "زيارة طلابية ميدانية لمحطة البحوث الزراعية", "5420261204640.jpg", "2026-04-26")
        )
    }

    // Engineering-specific news sampled from HAR
    val engineeringNews: List<HarNewsItem> by lazy {
        listOf(
            HarNewsItem(116528, "من بين 172 خبيراً في مصر.. طالب بهندسة كفر الشيخ يحصد رعاية وتكريم من رئيس الجمهورية", "125202621261430.jpg", "2026-05-21", 12),
            HarNewsItem(116359, "جدول امتحانات الدراسات العليا للعام الجامعي 2025/2026", "105202614531518.jpg", "2026-05-10", 12),
            HarNewsItem(116355, "ندوة توعوية حول المخدرات بكلية التربية", "10520261422624.jpg", "2026-05-07", 12),
            HarNewsItem(116506, "قافلة طبية لكلية الهندسة لقرية في كفر الشيخ", "11520261555442.jpg", "2026-05-20", 12)
        )
    }

    val nursingNews: List<HarNewsItem> by lazy {
        listOf(
            HarNewsItem(116409, "تهنئة بمناسبة الاحتفال باليوم العالمي للتمريض", "12520261422597.jpg", "2026-05-12", 1421, "nur_inst"),
            HarNewsItem(116353, "المؤتمر العلمي السادس والدولي الرابع لكلية التمريض جامعة كفر الشيخ", "10520261147110.jpg", "2026-05-05", 1421, "nur_inst"),
            HarNewsItem(115774, "اللقاء الفكري الموسع الذي نظمته أسرة طلاب (من أجل مصر) بالجامعة", "5420261214117.jpg", "2026-04-28", 1421, "nur_inst")
        )
    }

    // Student auth sample (example data, not real)
    val sampleStudentAuth: String by lazy {
        """{"NATIONAL_NUMBER":"12345678901234","FULL_NAME_AR":"طالب تجريبي","METHOD_DESCR":"student@example.kfs.edu.eg","METHOD_NOTES":"Pass1234","STUD_CODE":"2024000000000","PASSWORD_ITEC":"000000","phase_parent":"مستوى رابع","GENDER_DESCR_AR":"ذكر","CASE_DESCR_AR":"مستجد","RELIGION_DESCR_AR":"مسلم","METHOD_DESCR_AR":"ساعات معتمدة","NATURE_DESCR_AR":"انتظام","ED_CODE_STUD_ENROLL_AR":"مقيد","Program_Name":"عام","AS_FACULTY_INFO_ID":1,"STUD_PHOTO":""}"""
    }

    fun getTopicsForFaculty(facId: Int): List<HarFacultyTopic> = when (facId) {
        12 -> engineeringTopics
        1421 -> nursingInstituteTopics
        1800 -> militaryTopics
        else -> emptyList()
    }

    fun getNewsForFaculty(facId: Int): List<HarNewsItem> = when (facId) {
        1 -> universityNews
        12 -> engineeringNews
        1421 -> nursingNews
        else -> universityNews
    }

    fun findFacultyByFacId(facId: Int): HarFacultyItem? = allFaculties.find { it.facId == facId }
    fun findFacultyByInfoId(infoId: Int): HarFacultyItem? = allFaculties.find { it.asFacultyInfoId == infoId }

    // Static data for college groups
    val scientificCollegeNames: List<Pair<String, String>> by lazy {
        scientificColleges.map { it.factitel to it.facId.toString() }
    }
    val medicalCollegeNames: List<Pair<String, String>> by lazy {
        medicalColleges.map { it.factitel to it.facId.toString() }
    }
    val humanitiesCollegeNames: List<Pair<String, String>> by lazy {
        humanitiesColleges.map { it.factitel to it.facId.toString() }
    }
}

object DataFetchCache {
    @Volatile var medicalColleges: List<RealCollegeItem> = emptyList()
    @Volatile var scientificColleges: List<RealCollegeItem> = emptyList()
    @Volatile var humanitiesColleges: List<RealCollegeItem> = emptyList()
    @Volatile var collegesLoaded = false
    @Volatile var latestNews: List<RealNewsItem> = emptyList()
    @Volatile var newsLoaded = false
}

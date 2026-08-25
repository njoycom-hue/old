package com.dunoetoktok.app.util

data class WordCategory(val name: String, val words: List<String>)

val WORD_CATEGORIES: List<WordCategory> = listOf(
    WordCategory("과일", listOf("사과", "바나나", "포도", "딸기", "수박", "오렌지")),
    WordCategory("동물", listOf("호랑이", "사자", "코끼리", "토끼", "강아지", "고양이")),
    WordCategory("채소", listOf("당근", "오이", "감자", "양파", "배추")),
    WordCategory("색깔", listOf("빨강", "파랑", "노랑", "초록", "보라")),
    WordCategory("가구", listOf("침대", "소파", "책상", "의자", "옷장")),
    WordCategory("교통수단", listOf("자동차", "버스", "기차", "비행기", "자전거")),
    WordCategory("악기", listOf("피아노", "기타", "바이올린", "드럼", "플루트")),
)

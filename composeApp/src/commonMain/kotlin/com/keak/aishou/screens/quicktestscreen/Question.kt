package com.keak.aishou.screens.quicktestscreen

data class Question(
val testId: String,
val testTitle: String,
val version: Int,
val index: Int,
val quizType: QuizType,
val questionText: String,
val choices: List<Choice>
)
data class Choice(
    val key: String,                 // "A","B","C"
    val text: String                 // görünen metin
)
enum class QuizType{
    Single, Compat
}
package com.keak.aishou.screens.paywall

object MBTITypes {
    const val INTJ = "INTJ"
    const val INTP = "INTP"
    const val ENTJ = "ENTJ"
    const val ENTP = "ENTP"

    const val INFJ = "INFJ"
    const val INFP = "INFP"
    const val ENFJ = "ENFJ"
    const val ENFP = "ENFP"

    const val ISTJ = "ISTJ"
    const val ISFJ = "ISFJ"
    const val ESTJ = "ESTJ"
    const val ESFJ = "ESFJ"

    const val ISTP = "ISTP"
    const val ISFP = "ISFP"
    const val ESTP = "ESTP"
    const val ESFP = "ESFP"
    val ALL = listOf(INTJ, INTP, ENTJ, ENTP, INFJ, INFP, ENFJ, ENFP, ISTJ, ISFJ, ESTJ, ESFJ, ISTP, ISFP, ESTP, ESFP)

}
object MBTITypesEmoji {
    val MAP = mapOf(
        MBTITypes.INTJ to "🧠", MBTITypes.INTP to "🧪", MBTITypes.ENTJ to "👔", MBTITypes.ENTP to "💡",
        MBTITypes.INFJ to "🕯️", MBTITypes.INFP to "🌈", MBTITypes.ENFJ to "🤝", MBTITypes.ENFP to "✨",
        MBTITypes.ISTJ to "📘", MBTITypes.ISFJ to "🛡️", MBTITypes.ESTJ to "📋", MBTITypes.ESFJ to "🤗",
        MBTITypes.ISTP to "🛠️", MBTITypes.ISFP to "🎨", MBTITypes.ESTP to "⚡", MBTITypes.ESFP to "🎉"
    )

    fun label(type: String): String = "$type ${MAP[type] ?: ""}"
    val LIST_WITH_EMOJI: List<String> = MBTITypes.ALL.map(::label)
}
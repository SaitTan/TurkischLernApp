package com.saittan.turkischlernen.data.models

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes

enum class SituationSection(val titleDe: String) {
    Needs("Bedürfnisse"),
    Feelings("Gefühle"),
    Greetings("Begrüßung"),
    Questions("Fragen & Antworten"),
}

data class Situation(
    val germanLabel: String,
    val turkishSentence: String,
    val pronunciation: String,
    @DrawableRes val imageResId: Int,
    val section: SituationSection,
    @RawRes val audioResId: Int? = null,
)

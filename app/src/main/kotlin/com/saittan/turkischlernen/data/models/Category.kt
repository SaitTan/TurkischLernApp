package com.saittan.turkischlernen.data.models

import androidx.annotation.DrawableRes

data class Category(
    val id: String,
    val nameDe: String,
    @DrawableRes val iconResId: Int,
    val words: List<Word>
)

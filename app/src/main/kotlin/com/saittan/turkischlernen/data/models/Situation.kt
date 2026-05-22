package com.saittan.turkischlernen.data.models

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes

data class Situation(
    val germanLabel: String,
    val turkishSentence: String,
    val pronunciation: String,
    @DrawableRes val imageResId: Int,
    @RawRes val audioResId: Int? = null
)

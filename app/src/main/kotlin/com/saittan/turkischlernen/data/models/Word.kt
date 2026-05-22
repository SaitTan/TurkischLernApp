package com.saittan.turkischlernen.data.models

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes

data class Word(
    val turkish: String,
    val german: String,
    @DrawableRes val imageResId: Int,
    @RawRes val audioResId: Int? = null,
    val known: Boolean = false
)

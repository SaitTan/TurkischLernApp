package com.saittan.turkischlernen.data.repository

import com.saittan.turkischlernen.R
import com.saittan.turkischlernen.data.models.Category
import com.saittan.turkischlernen.data.models.Word

object WordRepository {

    val categories: List<Category> = listOf(
        Category(
            id = "food",
            nameDe = "Essen & Trinken",
            iconResId = R.drawable.openmoji_food_cat,
            words = listOf(
                Word("Elma", "Apfel", R.drawable.openmoji_apple),
                Word("Su", "Wasser", R.drawable.openmoji_droplet, known = true),
                Word("Ekmek", "Brot", R.drawable.openmoji_bread),
                Word("Muz", "Banane", R.drawable.openmoji_banana),
                Word("Süt", "Milch", R.drawable.openmoji_milk),
            )
        ),
        Category(
            id = "animals",
            nameDe = "Tiere",
            iconResId = R.drawable.openmoji_animal_cat,
            words = listOf(
                Word("Köpek", "Hund", R.drawable.openmoji_dog),
                Word("Kedi", "Katze", R.drawable.openmoji_cat),
                Word("Kuş", "Vogel", R.drawable.openmoji_bird),
                Word("Balık", "Fisch", R.drawable.openmoji_fish),
                Word("At", "Pferd", R.drawable.openmoji_horse),
            )
        ),
        Category(
            id = "nature",
            nameDe = "Natur",
            iconResId = R.drawable.openmoji_nature_cat,
            words = listOf(
                Word("Ağaç", "Baum", R.drawable.openmoji_tree),
                Word("Çiçek", "Blume", R.drawable.openmoji_flower),
                Word("Güneş", "Sonne", R.drawable.openmoji_sun),
                Word("Bahçe", "Garten", R.drawable.openmoji_garden, known = true),
                Word("Yağmur", "Regen", R.drawable.openmoji_rain),
            )
        ),
        Category(
            id = "home",
            nameDe = "Zuhause",
            iconResId = R.drawable.openmoji_home_cat,
            words = listOf(
                Word("Ev", "Haus", R.drawable.openmoji_house),
                Word("Kapı", "Tür", R.drawable.openmoji_door),
                Word("Masa", "Tisch", R.drawable.openmoji_table),
                Word("Sandalye", "Stuhl", R.drawable.openmoji_chair),
                Word("Yatak", "Bett", R.drawable.openmoji_bed),
            )
        ),
        Category(
            id = "colors",
            nameDe = "Farben",
            iconResId = R.drawable.openmoji_color_cat,
            words = listOf(
                Word("Kırmızı", "Rot", R.drawable.openmoji_red),
                Word("Mavi", "Blau", R.drawable.openmoji_blue),
                Word("Sarı", "Gelb", R.drawable.openmoji_yellow),
                Word("Yeşil", "Grün", R.drawable.openmoji_green),
                Word("Beyaz", "Weiß", R.drawable.openmoji_white),
            )
        ),
    )

    fun categoryById(id: String): Category? = categories.firstOrNull { it.id == id }

    val totalWordCount: Int = categories.sumOf { it.words.size }
}

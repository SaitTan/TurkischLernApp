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
                Word("Çay", "Tee", R.drawable.openmoji_tea),
                Word("Peynir", "Käse", R.drawable.openmoji_cheese),
                Word("Pasta", "Kuchen", R.drawable.openmoji_cake),
                Word("Yumurta", "Ei", R.drawable.openmoji_egg),
                Word("Çilek", "Erdbeere", R.drawable.openmoji_strawberry),
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
                Word("Tavşan", "Hase", R.drawable.openmoji_rabbit),
                Word("İnek", "Kuh", R.drawable.openmoji_cow),
                Word("Koyun", "Schaf", R.drawable.openmoji_sheep),
                Word("Aslan", "Löwe", R.drawable.openmoji_lion),
                Word("Fil", "Elefant", R.drawable.openmoji_elephant),
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
                Word("Ay", "Mond", R.drawable.openmoji_moon),
                Word("Yıldız", "Stern", R.drawable.openmoji_star),
                Word("Deniz", "Meer", R.drawable.openmoji_sea),
                Word("Bulut", "Wolke", R.drawable.openmoji_cloud),
                Word("Kar", "Schnee", R.drawable.openmoji_snow),
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
                Word("Pencere", "Fenster", R.drawable.openmoji_window),
                Word("Lamba", "Lampe", R.drawable.openmoji_lamp),
                Word("Mutfak", "Küche", R.drawable.openmoji_kitchen),
                Word("Banyo", "Bad", R.drawable.openmoji_bath),
                Word("Televizyon", "Fernseher", R.drawable.openmoji_tv),
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
                Word("Siyah", "Schwarz", R.drawable.openmoji_black),
                Word("Turuncu", "Orange", R.drawable.openmoji_orange),
                Word("Mor", "Lila", R.drawable.openmoji_purple),
                Word("Pembe", "Rosa", R.drawable.openmoji_pink),
                Word("Kahverengi", "Braun", R.drawable.openmoji_brown),
            )
        ),
        Category(
            id = "numbers",
            nameDe = "Zahlen",
            iconResId = R.drawable.openmoji_numbers_cat,
            words = listOf(
                Word("Bir", "Eins", R.drawable.openmoji_one),
                Word("İki", "Zwei", R.drawable.openmoji_two),
                Word("Üç", "Drei", R.drawable.openmoji_three),
                Word("Dört", "Vier", R.drawable.openmoji_four),
                Word("Beş", "Fünf", R.drawable.openmoji_five),
                Word("Altı", "Sechs", R.drawable.openmoji_six),
                Word("Yedi", "Sieben", R.drawable.openmoji_seven),
                Word("Sekiz", "Acht", R.drawable.openmoji_eight),
                Word("Dokuz", "Neun", R.drawable.openmoji_nine),
                Word("On", "Zehn", R.drawable.openmoji_on),
            )
        ),
        Category(
            id = "body",
            nameDe = "Körper",
            iconResId = R.drawable.openmoji_body_cat,
            words = listOf(
                Word("Baş", "Kopf", R.drawable.openmoji_face),
                Word("Saç", "Haare", R.drawable.openmoji_haircut),
                Word("Göz", "Auge", R.drawable.openmoji_eye),
                Word("Burun", "Nase", R.drawable.openmoji_nose),
                Word("Ağız", "Mund", R.drawable.openmoji_mouth),
                Word("Kulak", "Ohr", R.drawable.openmoji_ear),
                Word("Diş", "Zahn", R.drawable.openmoji_tooth),
                Word("El", "Hand", R.drawable.openmoji_hand),
                Word("Parmak", "Finger", R.drawable.openmoji_finger),
                Word("Ayak", "Fuß", R.drawable.openmoji_foot),
            )
        ),
        Category(
            id = "family",
            nameDe = "Familie",
            iconResId = R.drawable.openmoji_family_cat,
            words = listOf(
                Word("Anne", "Mama", R.drawable.openmoji_mother),
                Word("Baba", "Papa", R.drawable.openmoji_father),
                Word("Bebek", "Baby", R.drawable.openmoji_baby),
                Word("Abla", "Schwester", R.drawable.openmoji_girl),
                Word("Ağabey", "Bruder", R.drawable.openmoji_boy),
                Word("Dede", "Opa", R.drawable.openmoji_grandpa),
                Word("Nine", "Oma", R.drawable.openmoji_grandma),
                Word("Çocuk", "Kind", R.drawable.openmoji_child),
                Word("Arkadaş", "Freund", R.drawable.openmoji_friends),
                Word("Aile", "Familie", R.drawable.openmoji_family),
            )
        ),
        Category(
            id = "clothing",
            nameDe = "Kleidung",
            iconResId = R.drawable.openmoji_clothing_cat,
            words = listOf(
                Word("Tişört", "T-Shirt", R.drawable.openmoji_tshirt),
                Word("Pantolon", "Hose", R.drawable.openmoji_pants),
                Word("Elbise", "Kleid", R.drawable.openmoji_dress),
                Word("Ceket", "Jacke", R.drawable.openmoji_jacket),
                Word("Şapka", "Mütze", R.drawable.openmoji_cap),
                Word("Ayakkabı", "Schuhe", R.drawable.openmoji_shoes),
                Word("Bot", "Stiefel", R.drawable.openmoji_boot),
                Word("Çorap", "Socken", R.drawable.openmoji_socks),
                Word("Eldiven", "Handschuhe", R.drawable.openmoji_gloves),
                Word("Atkı", "Schal", R.drawable.openmoji_scarf),
            )
        ),
    )

    fun categoryById(id: String): Category? = categories.firstOrNull { it.id == id }

    val totalWordCount: Int = categories.sumOf { it.words.size }
}

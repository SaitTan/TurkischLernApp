package com.saittan.turkischlernen.data.repository

import com.saittan.turkischlernen.R
import com.saittan.turkischlernen.data.models.Situation

object SituationRepository {

    val situations: List<Situation> = listOf(
        Situation(
            germanLabel = "Ich muss auf die Toilette",
            turkishSentence = "Tuvalete gitmem lazım",
            pronunciation = "Too-va-le-te git-mem la-zım",
            imageResId = R.drawable.openmoji_toilet,
        ),
        Situation(
            germanLabel = "Ich habe Hunger",
            turkishSentence = "Acıktım",
            pronunciation = "A-dschik-tım",
            imageResId = R.drawable.openmoji_hungry,
        ),
        Situation(
            germanLabel = "Ich habe Durst",
            turkishSentence = "Susadım",
            pronunciation = "Su-sa-dım",
            imageResId = R.drawable.openmoji_drink,
        ),
        Situation(
            germanLabel = "Ich möchte Wasser",
            turkishSentence = "Su istiyorum",
            pronunciation = "Su is-ti-yo-rum",
            imageResId = R.drawable.openmoji_droplet,
        ),
        Situation(
            germanLabel = "Ich bin müde",
            turkishSentence = "Yoruldum",
            pronunciation = "Yo-rul-dum",
            imageResId = R.drawable.openmoji_tired,
        ),
        Situation(
            germanLabel = "Ich möchte spielen",
            turkishSentence = "Oynamak istiyorum",
            pronunciation = "Oy-na-mak is-ti-yo-rum",
            imageResId = R.drawable.openmoji_play,
        ),
        Situation(
            germanLabel = "Ich bin fertig",
            turkishSentence = "Bittim",
            pronunciation = "Bit-tim",
            imageResId = R.drawable.openmoji_done,
        ),
        Situation(
            germanLabel = "Hilf mir bitte",
            turkishSentence = "Lütfen yardım et",
            pronunciation = "Lüt-fen jar-dım et",
            imageResId = R.drawable.openmoji_help,
        ),
        Situation(
            germanLabel = "Ich verstehe nicht",
            turkishSentence = "Anlamıyorum",
            pronunciation = "An-la-mı-yo-rum",
            imageResId = R.drawable.openmoji_confused,
        ),
        Situation(
            germanLabel = "Danke",
            turkishSentence = "Teşekkür ederim",
            pronunciation = "Te-schek-kür e-de-rim",
            imageResId = R.drawable.openmoji_thanks,
        ),
    )
}

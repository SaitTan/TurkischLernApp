package com.saittan.turkischlernen.data.repository

import com.saittan.turkischlernen.R
import com.saittan.turkischlernen.data.models.Situation
import com.saittan.turkischlernen.data.models.SituationSection

object SituationRepository {

    val situations: List<Situation> = listOf(
        // ─── Bedürfnisse ───────────────────────────────────────────
        Situation(
            germanLabel = "Ich muss auf die Toilette",
            turkishSentence = "Tuvalete gitmem lazım",
            pronunciation = "Too-va-le-te git-mem la-zım",
            imageResId = R.drawable.openmoji_toilet,
            section = SituationSection.Needs,
        ),
        Situation(
            germanLabel = "Ich habe Hunger",
            turkishSentence = "Acıktım",
            pronunciation = "A-dschik-tım",
            imageResId = R.drawable.openmoji_hungry,
            section = SituationSection.Needs,
        ),
        Situation(
            germanLabel = "Ich habe Durst",
            turkishSentence = "Susadım",
            pronunciation = "Su-sa-dım",
            imageResId = R.drawable.openmoji_drink,
            section = SituationSection.Needs,
        ),
        Situation(
            germanLabel = "Ich möchte Wasser",
            turkishSentence = "Su istiyorum",
            pronunciation = "Su is-ti-yo-rum",
            imageResId = R.drawable.openmoji_droplet,
            section = SituationSection.Needs,
        ),
        Situation(
            germanLabel = "Ich bin müde",
            turkishSentence = "Yoruldum",
            pronunciation = "Yo-rul-dum",
            imageResId = R.drawable.openmoji_tired,
            section = SituationSection.Needs,
        ),
        Situation(
            germanLabel = "Ich möchte spielen",
            turkishSentence = "Oynamak istiyorum",
            pronunciation = "Oy-na-mak is-ti-yo-rum",
            imageResId = R.drawable.openmoji_play,
            section = SituationSection.Needs,
        ),
        Situation(
            germanLabel = "Ich bin fertig",
            turkishSentence = "Bittim",
            pronunciation = "Bit-tim",
            imageResId = R.drawable.openmoji_done,
            section = SituationSection.Needs,
        ),
        Situation(
            germanLabel = "Mir ist kalt",
            turkishSentence = "Üşüyorum",
            pronunciation = "Ü-schü-yo-rum",
            imageResId = R.drawable.openmoji_cold,
            section = SituationSection.Needs,
        ),
        Situation(
            germanLabel = "Mir ist heiß",
            turkishSentence = "Sıcakladım",
            pronunciation = "Sı-dschak-la-dım",
            imageResId = R.drawable.openmoji_hot,
            section = SituationSection.Needs,
        ),
        Situation(
            germanLabel = "Ich bin krank",
            turkishSentence = "Hastayım",
            pronunciation = "Has-ta-yım",
            imageResId = R.drawable.openmoji_sick,
            section = SituationSection.Needs,
        ),
        Situation(
            germanLabel = "Ich vermisse Mama",
            turkishSentence = "Annemi özledim",
            pronunciation = "An-ne-mi öz-le-dim",
            imageResId = R.drawable.openmoji_pleading,
            section = SituationSection.Needs,
        ),
        Situation(
            germanLabel = "Ich möchte nach Hause",
            turkishSentence = "Eve gitmek istiyorum",
            pronunciation = "E-ve git-mek is-ti-yo-rum",
            imageResId = R.drawable.openmoji_house,
            section = SituationSection.Needs,
        ),

        // ─── Gefühle ───────────────────────────────────────────────
        Situation(
            germanLabel = "Ich bin glücklich",
            turkishSentence = "Mutluyum",
            pronunciation = "Mut-lu-yum",
            imageResId = R.drawable.openmoji_happy,
            section = SituationSection.Feelings,
        ),
        Situation(
            germanLabel = "Ich bin traurig",
            turkishSentence = "Üzgünüm",
            pronunciation = "Üz-gü-nüm",
            imageResId = R.drawable.openmoji_sad,
            section = SituationSection.Feelings,
        ),
        Situation(
            germanLabel = "Ich habe Angst",
            turkishSentence = "Korkuyorum",
            pronunciation = "Kor-ku-yo-rum",
            imageResId = R.drawable.openmoji_afraid,
            section = SituationSection.Feelings,
        ),
        Situation(
            germanLabel = "Ich bin wütend",
            turkishSentence = "Kızgınım",
            pronunciation = "Kız-gı-nım",
            imageResId = R.drawable.openmoji_angry,
            section = SituationSection.Feelings,
        ),
        Situation(
            germanLabel = "Ich freue mich",
            turkishSentence = "Heyecanlıyım",
            pronunciation = "He-ye-dschan-lı-yım",
            imageResId = R.drawable.openmoji_excited,
            section = SituationSection.Feelings,
        ),

        // ─── Begrüßung ─────────────────────────────────────────────
        Situation(
            germanLabel = "Hallo",
            turkishSentence = "Merhaba",
            pronunciation = "Mer-ha-ba",
            imageResId = R.drawable.openmoji_wave,
            section = SituationSection.Greetings,
        ),
        Situation(
            germanLabel = "Guten Morgen",
            turkishSentence = "Günaydın",
            pronunciation = "Gü-nay-dın",
            imageResId = R.drawable.openmoji_sunrise,
            section = SituationSection.Greetings,
        ),
        Situation(
            germanLabel = "Gute Nacht",
            turkishSentence = "İyi geceler",
            pronunciation = "I-yi ge-dsche-ler",
            imageResId = R.drawable.openmoji_moon,
            section = SituationSection.Greetings,
        ),
        Situation(
            germanLabel = "Bis bald",
            turkishSentence = "Görüşmek üzere",
            pronunciation = "Gö-rüsch-mek ü-ze-re",
            imageResId = R.drawable.openmoji_wave,
            section = SituationSection.Greetings,
        ),
        Situation(
            germanLabel = "Bitte",
            turkishSentence = "Lütfen",
            pronunciation = "Lüt-fen",
            imageResId = R.drawable.openmoji_please,
            section = SituationSection.Greetings,
        ),
        Situation(
            germanLabel = "Danke",
            turkishSentence = "Teşekkür ederim",
            pronunciation = "Te-schek-kür e-de-rim",
            imageResId = R.drawable.openmoji_thanks,
            section = SituationSection.Greetings,
        ),
        Situation(
            germanLabel = "Hilf mir bitte",
            turkishSentence = "Lütfen yardım et",
            pronunciation = "Lüt-fen jar-dım et",
            imageResId = R.drawable.openmoji_help,
            section = SituationSection.Greetings,
        ),

        // ─── Fragen & Antworten ────────────────────────────────────
        Situation(
            germanLabel = "Was ist das?",
            turkishSentence = "Bu ne?",
            pronunciation = "Bu ne",
            imageResId = R.drawable.openmoji_confused,
            section = SituationSection.Questions,
        ),
        Situation(
            germanLabel = "Wo ist es?",
            turkishSentence = "Nerede?",
            pronunciation = "Ne-re-de",
            imageResId = R.drawable.openmoji_location,
            section = SituationSection.Questions,
        ),
        Situation(
            germanLabel = "Wie heißt du?",
            turkishSentence = "İsmin ne?",
            pronunciation = "Is-min ne",
            imageResId = R.drawable.openmoji_speech,
            section = SituationSection.Questions,
        ),
        Situation(
            germanLabel = "Ich verstehe nicht",
            turkishSentence = "Anlamıyorum",
            pronunciation = "An-la-mı-yo-rum",
            imageResId = R.drawable.openmoji_confused,
            section = SituationSection.Questions,
        ),
        Situation(
            germanLabel = "Okay / In Ordnung",
            turkishSentence = "Olur",
            pronunciation = "O-lur",
            imageResId = R.drawable.openmoji_ok,
            section = SituationSection.Questions,
        ),
        Situation(
            germanLabel = "Ja, gerne!",
            turkishSentence = "Evet, tabii ki!",
            pronunciation = "E-vet, ta-bii ki",
            imageResId = R.drawable.openmoji_thumbsup,
            section = SituationSection.Questions,
        ),
    )

    val grouped: List<Pair<SituationSection, List<Situation>>> =
        SituationSection.entries.map { section ->
            section to situations.filter { it.section == section }
        }
}

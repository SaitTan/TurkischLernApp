package de.turkischlernen.app.data.content

import de.turkischlernen.app.data.model.Situation

/**
 * Bereich "Ich brauche": Kommunikationskarten für den Alltag.
 * Ein Tipp auf die Karte liest den türkischen Satz laut vor.
 */
object Situations {

    val all: List<Situation> = listOf(
        Situation(
            "s_tuvalet", "Ich muss auf die Toilette", "Tuvalete gitmem lazım", "🚻",
            "Tu-wa-le-te git-mem la-sım"
        ),
        Situation("s_ac", "Ich habe Hunger", "Acıktım", "😋", "A-dschık-tım"),
        Situation("s_susuz", "Ich habe Durst", "Susadım", "🥤", "Ssu-ssa-dım"),
        Situation("s_su", "Ich möchte Wasser", "Su istiyorum", "💧", "Ssu is-ti-jo-rum"),
        Situation("s_yorgun", "Ich bin müde", "Yoruldum", "😴", "Jo-rul-dum"),
        Situation(
            "s_oyna", "Ich möchte spielen", "Oynamak istiyorum", "🎲",
            "Oi-na-mak is-ti-jo-rum"
        ),
        Situation("s_bittim", "Ich bin fertig", "Bittim", "🙌", "Bit-tim"),
        Situation("s_yardim", "Hilf mir bitte", "Lütfen yardım et", "🆘", "Lüt-fen jar-dım et"),
        Situation(
            "s_anlamiyorum", "Ich verstehe nicht", "Anlamıyorum", "🤔",
            "An-la-mı-jo-rum"
        ),
        Situation(
            "s_tesekkur", "Danke", "Teşekkür ederim", "🙏",
            "Te-schek-kür e-de-rim"
        ),
        Situation("s_usudum", "Mir ist kalt", "Üşüdüm", "🥶", "Ü-schü-düm"),
        Situation("s_agri", "Mir tut weh", "Canım acıyor", "🤕", "Dscha-nım a-dschı-jor"),
        Situation(
            "s_eve", "Ich möchte nach Hause", "Eve gitmek istiyorum", "🏠",
            "E-we git-mek is-ti-jo-rum"
        ),
        Situation("s_tuvalet_nerede", "Wo ist die Toilette?", "Tuvalet nerede?", "❓", "Tu-wa-let ne-re-de"),
        Situation("s_uyumak", "Ich möchte schlafen", "Uyumak istiyorum", "🛏️", "U-ju-mak is-ti-jo-rum"),
        Situation("s_disari", "Ich möchte raus", "Dışarı çıkmak istiyorum", "🌳", "Dı-scha-rı tschık-mak is-ti-jo-rum")
    )
}

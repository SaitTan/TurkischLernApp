package de.turkischlernen.app.data.content

import de.turkischlernen.app.data.content.ExpandedCurriculum.UnitContent
import de.turkischlernen.app.data.model.Phrase
import de.turkischlernen.app.data.model.Word

/**
 * Weiterführende Alltagsthemen: je neun Wörter und vier Sätze.
 * Neue Einheiten werden hinter den bisherigen Lernpfad angehängt.
 */
internal object EverydayCurriculum {

    val units: List<UnitContent> = listOf(
        UnitContent(
            id = "u_fruehstueck",
            title = "Frühstück",
            subtitle = "Ein leckerer Start in den Tag",
            emoji = "🥣",
            colorHex = 0xFFFF9600,
            words = listOf(
                Word("kahvalti", "kahvaltı", "Frühstück", "🥣", "Kah-wal-tı"),
                Word("bal", "bal", "Honig", "🍯", "Bal"),
                Word("recel", "reçel", "Marmelade", "🫙", "Re-tschel"),
                Word("tereyagi", "tereyağı", "Butter", "🧈", "Te-re-ja-ı"),
                Word("zeytin", "zeytin", "Olive", "🫒", "Sej-tin"),
                Word("yogurt", "yoğurt", "Joghurt", "🥣", "Jo-urt"),
                Word("simit", "simit", "Sesamring", "🥯", "Ssi-mit"),
                Word("meyve_suyu", "meyve suyu", "Fruchtsaft", "🧃", "Mej-we ssu-ju"),
                Word("tost", "tost", "Toast", "🥪", "Tost")
            ),
            phrases = listOf(
                Phrase("p_kahvalti_yapiyorum", "kahvaltı yapıyorum", "Ich frühstücke", "🥣", "Kah-wal-tı ja-pı-jo-rum"),
                Phrase("p_bal_istiyorum", "bal istiyorum", "Ich möchte Honig", "🍯", "Bal is-ti-jo-rum"),
                Phrase("p_simit_yiyorum", "simit yiyorum", "Ich esse einen Sesamring", "🥯", "Ssi-mit ji-jo-rum"),
                Phrase("p_meyve_suyu_iciyorum", "meyve suyu içiyorum", "Ich trinke Fruchtsaft", "🧃", "Mej-we ssu-ju i-tschi-jo-rum")
            )
        ),
        UnitContent(
            id = "u_kueche",
            title = "In der Küche",
            subtitle = "Wir decken den Tisch",
            emoji = "🍴",
            colorHex = 0xFF2FB98B,
            words = listOf(
                Word("tabak", "tabak", "Teller", "🍽️", "Ta-bak"),
                Word("bardak", "bardak", "Trinkglas", "🥛", "Bar-dak"),
                Word("fincan", "fincan", "Tasse", "☕", "Fin-dschan"),
                Word("kasik", "kaşık", "Löffel", "🥄", "Ka-schık"),
                Word("catal", "çatal", "Gabel", "🍴", "Tscha-tal"),
                Word("bicak", "bıçak", "Messer", "🔪", "Bı-tschak"),
                Word("tencere", "tencere", "Kochtopf", "🍲", "Ten-dsche-re"),
                Word("tava", "tava", "Pfanne", "🍳", "Ta-wa"),
                Word("pecete", "peçete", "Serviette", "🧻", "Pe-tsche-te")
            ),
            phrases = listOf(
                Phrase("p_tabak_masada", "tabak masada", "Der Teller steht auf dem Tisch", "🍽️", "Ta-bak ma-ssa-da"),
                Phrase("p_kasik_nerede", "kaşık nerede", "Wo ist der Löffel?", "🥄", "Ka-schık ne-re-de"),
                Phrase("p_bardak_bos", "bardak boş", "Das Glas ist leer", "🥛", "Bar-dak bosch"),
                Phrase("p_pecete_istiyorum", "peçete istiyorum", "Ich möchte eine Serviette", "🧻", "Pe-tsche-te is-ti-jo-rum")
            )
        ),
        UnitContent(
            id = "u_wochentage",
            title = "Die Wochentage",
            subtitle = "Von Montag bis Sonntag",
            emoji = "📅",
            colorHex = 0xFF1CB0F6,
            words = listOf(
                Word("pazartesi", "pazartesi", "Montag", "📅", "Pa-sar-te-ssi"),
                Word("sali", "salı", "Dienstag", "📅", "Ssa-lı"),
                Word("carsamba", "çarşamba", "Mittwoch", "📅", "Tschar-scham-ba"),
                Word("persembe", "perşembe", "Donnerstag", "📅", "Per-schem-be"),
                Word("cuma", "cuma", "Freitag", "📅", "Dschu-ma"),
                Word("cumartesi", "cumartesi", "Samstag", "📅", "Dschu-mar-te-ssi"),
                Word("pazar", "pazar", "Sonntag", "📅", "Pa-sar"),
                Word("hafta_sonu", "hafta sonu", "Wochenende", "🎉", "Haf-ta sso-nu"),
                Word("gun", "gün", "Tag", "🌞", "Gün")
            ),
            phrases = listOf(
                Phrase("p_bugun_pazartesi", "bugün pazartesi", "Heute ist Montag", "📅", "Bu-gün pa-sar-te-ssi"),
                Phrase("p_yarin_sali", "yarın salı", "Morgen ist Dienstag", "📅", "Ja-rın ssa-lı"),
                Phrase("p_dun_pazardı", "dün pazardı", "Gestern war Sonntag", "📅", "Dün pa-sar-dı"),
                Phrase("p_hafta_sonu_oynuyoruz", "hafta sonu oynuyoruz", "Am Wochenende spielen wir", "🎲", "Haf-ta sso-nu oi-nu-jo-rus")
            )
        ),
        UnitContent(
            id = "u_einkaufen",
            title = "Einkaufen",
            subtitle = "Was kostet das?",
            emoji = "🛒",
            colorHex = 0xFF58CC02,
            words = listOf(
                Word("para", "para", "Geld", "💰", "Pa-ra"),
                Word("fiyat", "fiyat", "Preis", "🏷️", "Fi-jat"),
                Word("lira", "lira", "Lira", "💵", "Li-ra"),
                Word("ucuz", "ucuz", "günstig", "🏷️", "U-dschus"),
                Word("pahali", "pahalı", "teuer", "💸", "Pa-ha-lı"),
                Word("kasa", "kasa", "Kasse", "🏪", "Ka-ssa"),
                Word("fis", "fiş", "Kassenbon", "🧾", "Fisch"),
                Word("sepet", "sepet", "Korb", "🧺", "Sse-pet"),
                Word("indirim", "indirim", "Rabatt", "📉", "In-di-rim")
            ),
            phrases = listOf(
                Phrase("p_bu_kac_lira", "bu kaç lira", "Wie viele Lira kostet das?", "🏷️", "Bu katsch li-ra"),
                Phrase("p_bir_ekmek_istiyorum", "bir ekmek istiyorum", "Ich möchte ein Brot", "🍞", "Bir ek-mek is-ti-jo-rum"),
                Phrase("p_sepet_dolu", "sepet dolu", "Der Korb ist voll", "🧺", "Sse-pet do-lu"),
                Phrase("p_fis_alabilir_miyim", "fiş alabilir miyim", "Kann ich einen Kassenbon bekommen?", "🧾", "Fisch a-la-bi-lir mi-jim")
            )
        ),
        UnitContent(
            id = "u_spielplatz",
            title = "Auf dem Spielplatz",
            subtitle = "Zusammen spielen",
            emoji = "🛝",
            colorHex = 0xFFFFC800,
            words = listOf(
                Word("top", "top", "Ball", "⚽", "Top"),
                Word("salincak", "salıncak", "Schaukel", "🛝", "Ssa-lın-dschak"),
                Word("kaydirak", "kaydırak", "Rutsche", "🛝", "Kai-dı-rak"),
                Word("kum", "kum", "Sand", "🏖️", "Kum"),
                Word("kova", "kova", "Eimer", "🪣", "Ko-wa"),
                Word("kurek", "kürek", "Schaufel", "🪏", "Kü-rek"),
                Word("ip", "ip", "Seil", "🪢", "Ip"),
                Word("tahterevalli", "tahterevalli", "Wippe", "🛝", "Tah-te-re-wal-li"),
                Word("oyun", "oyun", "Spiel", "🎲", "O-jun")
            ),
            phrases = listOf(
                Phrase("p_birlikte_oynayalim", "birlikte oynayalım", "Lass uns zusammen spielen", "🤝", "Bir-lik-te oi-na-ja-lım"),
                Phrase("p_topu_bana_at", "topu bana at", "Wirf mir den Ball zu", "⚽", "To-pu ba-na at"),
                Phrase("p_sira_bende", "sıra bende", "Ich bin dran", "🙋", "Ssı-ra ben-de"),
                Phrase("p_kumdan_kale_yapiyorum", "kumdan kale yapıyorum", "Ich baue eine Sandburg", "🏰", "Kum-dan ka-le ja-pı-jo-rum")
            )
        ),
        UnitContent(
            id = "u_meerestiere",
            title = "Tiere im Meer",
            subtitle = "Eine Reise unter Wasser",
            emoji = "🐬",
            colorHex = 0xFF1CB0F6,
            words = listOf(
                Word("yunus", "yunus", "Delfin", "🐬", "Ju-nus"),
                Word("kopekbaligi", "köpekbalığı", "Hai", "🦈", "Kö-pek-ba-lı-ı"),
                Word("balina", "balina", "Wal", "🐋", "Ba-li-na"),
                Word("ahtapot", "ahtapot", "Oktopus", "🐙", "Ah-ta-pot"),
                Word("yengec", "yengeç", "Krabbe", "🦀", "Jen-getsch"),
                Word("denizanasi", "denizanası", "Qualle", "🪼", "De-ni-sa-na-ssı"),
                Word("denizyildizi", "denizyıldızı", "Seestern", "⭐", "De-nis-jıl-dı-sı"),
                Word("midye", "midye", "Miesmuschel", "🐚", "Mid-je"),
                Word("fok", "fok", "Robbe", "🦭", "Fok")
            ),
            phrases = listOf(
                Phrase("p_yunus_yuzuyor", "yunus yüzüyor", "Der Delfin schwimmt", "🐬", "Ju-nus jü-sü-jor"),
                Phrase("p_balina_cok_buyuk", "balina çok büyük", "Der Wal ist sehr groß", "🐋", "Ba-li-na tschok bü-jük"),
                Phrase("p_yengec_kumda", "yengeç kumda", "Die Krabbe ist im Sand", "🦀", "Jen-getsch kum-da"),
                Phrase("p_denizde_fok_var", "denizde fok var", "Im Meer ist eine Robbe", "🦭", "De-nis-de fok war")
            )
        ),
        UnitContent(
            id = "u_berufe",
            title = "Berufe",
            subtitle = "Wer macht was?",
            emoji = "🧑‍⚕️",
            colorHex = 0xFFFF4B4B,
            words = listOf(
                Word("doktor", "doktor", "Arzt", "🧑‍⚕️", "Dok-tor"),
                Word("hemsire", "hemşire", "Pflegekraft", "🧑‍⚕️", "Hem-schi-re"),
                Word("polis", "polis", "Polizist", "👮", "Po-lis"),
                Word("itfaiyeci", "itfaiyeci", "Feuerwehrkraft", "🧑‍🚒", "It-fa-i-je-dschi"),
                Word("asci", "aşçı", "Koch", "🧑‍🍳", "Asch-tschı"),
                Word("ciftci", "çiftçi", "Landwirt", "🧑‍🌾", "Tschift-tschi"),
                Word("pilot", "pilot", "Pilot", "🧑‍✈️", "Pi-lot"),
                Word("dis_hekimi", "diş hekimi", "Zahnarzt", "🦷", "Disch he-ki-mi"),
                Word("veteriner", "veteriner", "Tierarzt", "🐾", "We-te-ri-ner")
            ),
            phrases = listOf(
                Phrase("p_doktor_hastanede", "doktor hastanede", "Der Arzt ist im Krankenhaus", "🏥", "Dok-tor has-ta-ne-de"),
                Phrase("p_asci_yemek_yapiyor", "aşçı yemek yapıyor", "Der Koch bereitet Essen zu", "🧑‍🍳", "Asch-tschı je-mek ja-pı-jor"),
                Phrase("p_pilot_ucakta", "pilot uçakta", "Der Pilot ist im Flugzeug", "✈️", "Pi-lot u-tschak-ta"),
                Phrase("p_veteriner_kediye_bakiyor", "veteriner kediye bakıyor", "Der Tierarzt kümmert sich um die Katze", "🐱", "We-te-ri-ner ke-di-je ba-kı-jor")
            )
        ),
        UnitContent(
            id = "u_richtungen",
            title = "Wo geht es lang?",
            subtitle = "Links, rechts und geradeaus",
            emoji = "🧭",
            colorHex = 0xFF2FB98B,
            words = listOf(
                Word("sag", "sağ", "rechts", "➡️", "Ssa-a"),
                Word("sol", "sol", "links", "⬅️", "Ssol"),
                Word("duz", "düz", "geradeaus", "⬆️", "Düs"),
                Word("ileri", "ileri", "vorwärts", "⏩", "I-le-ri"),
                Word("geri", "geri", "zurück", "⏪", "Ge-ri"),
                Word("yakin", "yakın", "nah", "📍", "Ja-kın"),
                Word("uzak", "uzak", "weit weg", "🔭", "U-sak"),
                Word("burada", "burada", "hier", "👇", "Bu-ra-da"),
                Word("orada", "orada", "dort", "👉", "O-ra-da")
            ),
            phrases = listOf(
                Phrase("p_saga_don", "sağa dön", "Biege rechts ab", "➡️", "Ssa-a dön"),
                Phrase("p_sola_don", "sola dön", "Biege links ab", "⬅️", "Sso-la dön"),
                Phrase("p_duz_git", "düz git", "Geh geradeaus", "⬆️", "Düs git"),
                Phrase("p_okul_cok_yakin", "okul çok yakın", "Die Schule ist ganz nah", "🏫", "O-kul tschok ja-kın")
            )
        ),
        UnitContent(
            id = "u_geburtstag",
            title = "Geburtstag",
            subtitle = "Wir feiern zusammen",
            emoji = "🎂",
            colorHex = 0xFFCE82FF,
            words = listOf(
                Word("dogum_gunu", "doğum günü", "Geburtstag", "🎂", "Do-um gü-nü"),
                Word("pasta", "pasta", "Kuchen", "🍰", "Pas-ta"),
                Word("mum", "mum", "Kerze", "🕯️", "Mum"),
                Word("hediye", "hediye", "Geschenk", "🎁", "He-di-je"),
                Word("balon", "balon", "Luftballon", "🎈", "Ba-lon"),
                Word("davetiye", "davetiye", "Einladung", "💌", "Da-we-ti-je"),
                Word("parti", "parti", "Party", "🎉", "Par-ti"),
                Word("kurdele", "kurdele", "Geschenkband", "🎀", "Kur-de-le"),
                Word("dilek", "dilek", "Wunsch", "🌠", "Di-lek")
            ),
            phrases = listOf(
                Phrase("p_iyi_ki_dogdun", "iyi ki doğdun", "Alles Gute zum Geburtstag", "🎂", "I-ji ki do-dun"),
                Phrase("p_bu_senin_hediyen", "bu senin hediyen", "Das ist dein Geschenk", "🎁", "Bu sse-nin he-di-jen"),
                Phrase("p_mumlari_ufluyorum", "mumları üflüyorum", "Ich puste die Kerzen aus", "🕯️", "Mum-la-rı üf-lü-jo-rum"),
                Phrase("p_bir_dilek_tut", "bir dilek tut", "Wünsch dir etwas", "🌠", "Bir di-lek tut")
            )
        ),
        UnitContent(
            id = "u_weltraum",
            title = "Im Weltraum",
            subtitle = "Planeten und Raketen",
            emoji = "🚀",
            colorHex = 0xFFCE82FF,
            words = listOf(
                Word("uzay", "uzay", "Weltraum", "🌌", "U-sai"),
                Word("gezegen", "gezegen", "Planet", "🪐", "Ge-se-gen"),
                Word("dunya", "dünya", "Erde", "🌍", "Dün-ja"),
                Word("mars", "mars", "Mars", "🔴", "Mars"),
                Word("roket", "roket", "Rakete", "🚀", "Ro-ket"),
                Word("astronot", "astronot", "Astronaut", "🧑‍🚀", "As-tro-not"),
                Word("uydu", "uydu", "Satellit", "🛰️", "Ui-du"),
                Word("teleskop", "teleskop", "Teleskop", "🔭", "Te-les-kop"),
                Word("kuyruklu_yildiz", "kuyruklu yıldız", "Komet", "☄️", "Kui-ruk-lu jıl-dıs")
            ),
            phrases = listOf(
                Phrase("p_roket_ucuyor", "roket uçuyor", "Die Rakete fliegt", "🚀", "Ro-ket u-tschu-jor"),
                Phrase("p_dunya_bir_gezegen", "dünya bir gezegen", "Die Erde ist ein Planet", "🌍", "Dün-ja bir ge-se-gen"),
                Phrase("p_astronot_uzayda", "astronot uzayda", "Der Astronaut ist im Weltraum", "🧑‍🚀", "As-tro-not u-sai-da"),
                Phrase("p_yildizlara_bakiyorum", "yıldızlara bakıyorum", "Ich schaue die Sterne an", "🔭", "Jıl-dıs-la-ra ba-kı-jo-rum")
            )
        )
    )

    val words: List<Word> = units.flatMap { it.words }
    val phrases: List<Phrase> = units.flatMap { it.phrases }
}
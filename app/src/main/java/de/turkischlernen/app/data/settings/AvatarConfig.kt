package de.turkischlernen.app.data.settings

/**
 * Eine Fellvariante des Maskottchens. Die drei Farbtöne gehören zusammen:
 * [furHex] für Kopf und Körper, [furDarkHex] für Ohren, Schwanzkontur und Hüften,
 * [creamHex] für Brustfleck und Pfoten.
 */
data class FurOption(
    val id: String,
    val label: String,
    val furHex: Long,
    val furDarkHex: Long,
    val creamHex: Long
)

/** Ein Accessoire, das der Hund tragen kann. [emoji] steht auf der Auswahlkachel. */
data class AccessoryOption(
    val id: String,
    val label: String,
    val emoji: String
)

/** Eine einfarbige Auswahl (Hautton, Haarfarbe, T-Shirt). */
data class ColorOption(
    val id: String,
    val label: String,
    val hex: Long
)

/** Eine Frisur. [emoji] steht auf der Auswahlkachel. */
data class HairStyleOption(
    val id: String,
    val label: String,
    val emoji: String
)

/**
 * Alles, was das Kind an seiner eigenen Figur und am Maskottchen einstellen kann.
 * Die Figur steht im Profil, der Hund begleitet die Lektionen.
 */
data class AvatarConfig(
    // Maskottchen
    val furId: String = AvatarOptions.DEFAULT_FUR,
    val accessoryId: String = AvatarOptions.DEFAULT_ACCESSORY,
    val name: String = AvatarOptions.DEFAULT_NAME,
    // Eigene Figur
    val skinId: String = AvatarOptions.DEFAULT_SKIN,
    val hairId: String = AvatarOptions.DEFAULT_HAIR,
    val hairColorId: String = AvatarOptions.DEFAULT_HAIR_COLOR,
    val shirtId: String = AvatarOptions.DEFAULT_SHIRT,
    val glasses: Boolean = false
) {
    val fur: FurOption get() = AvatarOptions.fur(furId)
    val accessory: AccessoryOption get() = AvatarOptions.accessory(accessoryId)
    val skin: ColorOption get() = AvatarOptions.skin(skinId)
    val hair: HairStyleOption get() = AvatarOptions.hair(hairId)
    val hairColor: ColorOption get() = AvatarOptions.hairColor(hairColorId)
    val shirt: ColorOption get() = AvatarOptions.shirt(shirtId)
}

/** Alle wählbaren Varianten. Unbekannte Ids fallen immer auf den Standard zurück. */
object AvatarOptions {

    const val DEFAULT_FUR = "sand"
    const val DEFAULT_ACCESSORY = "keins"
    const val DEFAULT_NAME = "Kangal"
    const val DEFAULT_SKIN = "mittel"
    const val DEFAULT_HAIR = "kurz"
    const val DEFAULT_HAIR_COLOR = "schwarz"
    const val DEFAULT_SHIRT = "anthrazit"

    /** Längere Namen passen nicht auf den Bildschirm. */
    const val MAX_NAME_LENGTH = 12

    val furs = listOf(
        FurOption("sand", "Sandfarben", 0xFFE3B87C, 0xFFC9975A, 0xFFF7E6C6),
        FurOption("hellbraun", "Hellbraun", 0xFFC98B5A, 0xFFA86C40, 0xFFF0DCC0),
        FurOption("grau", "Grau", 0xFFB7BDC4, 0xFF8E959D, 0xFFEDEFF1),
        FurOption("weiss", "Weiß", 0xFFF2EDE4, 0xFFD2C9BA, 0xFFFFFFFF),
        FurOption("schwarz", "Schwarz", 0xFF6B625C, 0xFF463F3B, 0xFFD9D2CB)
    )

    val accessories = listOf(
        AccessoryOption("keins", "Ohne", "🚫"),
        AccessoryOption("muetze", "Mütze", "🧢"),
        AccessoryOption("brille", "Sonnenbrille", "🕶️"),
        AccessoryOption("schleife", "Schleife", "🎀"),
        AccessoryOption("schal", "Fußballschal", "🧣")
    )

    val skins = listOf(
        ColorOption("hell", "Hell", 0xFFF6D9C2),
        ColorOption("mittel", "Mittel", 0xFFE7B892),
        ColorOption("oliv", "Oliv", 0xFFD2A074),
        ColorOption("braun", "Braun", 0xFFA9714B),
        ColorOption("dunkel", "Dunkel", 0xFF6F4A33)
    )

    val hairStyles = listOf(
        HairStyleOption("kurz", "Kurz", "👦"),
        HairStyleOption("locken", "Locken", "🧒"),
        HairStyleOption("pony", "Pony", "💇"),
        HairStyleOption("undercut", "Undercut", "✂️")
    )

    val hairColors = listOf(
        ColorOption("schwarz", "Schwarz", 0xFF2B2320),
        ColorOption("dunkelbraun", "Dunkelbraun", 0xFF4A3122),
        ColorOption("braun", "Braun", 0xFF6F4E2F),
        ColorOption("blond", "Blond", 0xFFD8B26A),
        ColorOption("rot", "Rot", 0xFFA8502A)
    )

    val shirts = listOf(
        ColorOption("anthrazit", "Anthrazit", 0xFF3A3A3C),
        ColorOption("blau", "Blau", 0xFF1CB0F6),
        ColorOption("gruen", "Grün", 0xFF58CC02),
        ColorOption("rot", "Rot", 0xFFFF4B4B),
        ColorOption("gelb", "Gelb", 0xFFFFC800),
        ColorOption("lila", "Lila", 0xFFCE82FF)
    )

    fun fur(id: String): FurOption = furs.firstOrNull { it.id == id } ?: furs.first()

    fun accessory(id: String): AccessoryOption =
        accessories.firstOrNull { it.id == id } ?: accessories.first()

    fun skin(id: String): ColorOption =
        skins.firstOrNull { it.id == id } ?: skins.first { it.id == DEFAULT_SKIN }

    fun hair(id: String): HairStyleOption = hairStyles.firstOrNull { it.id == id } ?: hairStyles.first()

    fun hairColor(id: String): ColorOption = hairColors.firstOrNull { it.id == id } ?: hairColors.first()

    fun shirt(id: String): ColorOption = shirts.firstOrNull { it.id == id } ?: shirts.first()

    /** Kürzt zu lange Namen, damit sie überall hinpassen. */
    fun cleanName(raw: String): String = raw.take(MAX_NAME_LENGTH)
}

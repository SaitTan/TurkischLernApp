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

/** Die Einstellungen, die das Kind an seinem Hund vornehmen kann. */
data class AvatarConfig(
    val furId: String = AvatarOptions.DEFAULT_FUR,
    val accessoryId: String = AvatarOptions.DEFAULT_ACCESSORY,
    val name: String = AvatarOptions.DEFAULT_NAME
) {
    val fur: FurOption get() = AvatarOptions.fur(furId)
    val accessory: AccessoryOption get() = AvatarOptions.accessory(accessoryId)
}

/** Alle wählbaren Fellfarben und Accessoires. */
object AvatarOptions {

    const val DEFAULT_FUR = "sand"
    const val DEFAULT_ACCESSORY = "keins"
    const val DEFAULT_NAME = "Kangal"

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

    /** Fellfarbe zur Id – unbekannte Ids fallen auf den Standard zurück. */
    fun fur(id: String): FurOption = furs.firstOrNull { it.id == id } ?: furs.first()

    /** Accessoire zur Id – unbekannte Ids fallen auf den Standard zurück. */
    fun accessory(id: String): AccessoryOption =
        accessories.firstOrNull { it.id == id } ?: accessories.first()

    /** Kürzt zu lange Namen, damit sie überall hinpassen. */
    fun cleanName(raw: String): String = raw.take(MAX_NAME_LENGTH)
}

# 🇹🇷 Türkisch lernen – Android-App für Kinder

Eine kindgerechte Android-App im Stil von Duolingo, mit der ein 8-jähriges,
deutschsprachiges Kind **Türkisch** lernen kann: Lernpfad mit Einheiten und
Lektionen, XP, Tagesziel, Serie (Streak), Herzen, Abzeichen, Konfetti – und
zusätzlich eine Kommunikationstafel „Ich brauche …“ für den Alltag.

Die App ist **komplett offline**, **ohne Login**, **ohne Werbung** und
speichert alles nur lokal auf dem Gerät.

---

## ✨ Was die App kann

### 1. Lernpfad (Tab „Lernen“)
- **9 Einheiten**, **47 Lektionen** und **102 Wörter & Sätze** – Zickzack-Pfad mit runden Knoten
- Lektionen schalten sich nacheinander frei (🔒 → ⭐ → ✅), jede Einheit endet mit einer **Prüfung** 👑
- **5 Aufgabentypen**, automatisch aus den Vokabeln erzeugt:
  | Aufgabe | Ablauf |
  |---|---|
  | 🖼️ Bild → Wort | Bild antippen = Wort wird vorgelesen, dann türkisches Wort auswählen |
  | 🇩🇪 Bedeutung | Türkisches Wort hören/lesen, deutsche Bedeutung auswählen |
  | 👂 Hörverstehen | Nur Ton (auch 🐢 langsam), richtiges Wort antippen |
  | 🧩 Satz bauen | Satz aus Wortkacheln zusammensetzen |
  | 🔗 Paare finden | Türkisch ↔ Deutsch verbinden (in den Prüfungen) |
- **Falsch beantwortete Aufgaben kommen am Ende der Lektion noch einmal** (wie bei Duolingo)
- Rückmeldung: fröhlicher Klang + „Super gemacht!“ bzw. sanftes Signal ohne negativen Ton,
  die richtige Lösung wird immer angezeigt **und vorgesprochen**
- Abschlussbildschirm mit **Konfetti**, XP, Serie und Fehlerzahl

### 2. Ich brauche … (Tab „Ich brauche“)
16 große Bildkarten für Alltagssituationen (Toilette, Hunger, Durst, Hilfe, …).
Ein Tipp liest den türkischen Satz vor, **langes Drücken** liest ihn langsam vor.
Deutsches Label, türkischer Satz und Aussprachehilfe stehen auf jeder Karte.

### 3. Üben (Tab „Üben“)
- **Schwierige Wörter üben** – alle Wörter, bei denen Fehler passiert sind
- **Alle Wörter üben** – gemischte Wiederholung
- Wörterliste zum Nachhören (⭐ markiert Wörter, die das Kind schon kannte)

### 4. Profil (Tab „Profil“)
- XP, Serie, gelernte Wörter, abgeschlossene Lektionen
- **10 Abzeichen** zum Freischalten
- **Eltern-Bereich**:
  - Unbegrenzte Herzen ein/aus (aus = klassischer Duolingo-Modus mit Herzverlust)
  - Tagesziel 15 / 30 / 50 XP
  - Türkische Aussprache testen
  - Türkische Sprachdaten installieren (falls sie fehlen)
  - Fortschritt zurücksetzen

---

## 📚 Lernstoff

| Einheit | Inhalt (Beispiele) |
|---|---|
| 🍎 Essen & Trinken | elma, su⭐, ekmek, muz, süt, peynir, çay, yumurta, çikolata, dondurma, yemek⭐, portakal |
| 🐶 Tiere | köpek, kedi, kuş, balık, at, inek, tavşan, aslan, kelebek, fil, arı, kaplumbağa |
| 🎨 Farben | kırmızı, mavi, sarı, yeşil, beyaz, siyah, turuncu, mor, pembe, kahverengi |
| 🔢 Zahlen 1–10 | bir … on |
| 🌳 Natur | ağaç, çiçek, güneş, bahçe⭐, yağmur, ay, deniz, kar, yıldız, taş, orman, bulut |
| 🏠 Zuhause | ev, kapı, masa, sandalye, yatak, pencere, mutfak, banyo, lamba, anahtar, kitap, oyuncak |
| 👨‍👩‍👧‍👦 Familie | anne, baba, kardeş, abla, abi, dede, nine, bebek, arkadaş, aile |
| 👋 Begrüßen & Danke | merhaba, günaydın, lütfen, evet, hayır + Sätze („iyi geceler“, „teşekkür ederim“ …) |
| 🙋 Ich brauche … | acıktım⭐, susadım⭐, yoruldum, bittim, anlamıyorum, üşüdüm + Sätze („su istiyorum“ …) |

⭐ = Wörter, die das Kind schon kennt – sie sind in der Wörterliste markiert.

**Bilder:** Als Illustrationen werden große Emojis verwendet (Farben als Farbkreis).
Dadurch bleibt die App winzig, funktioniert offline und braucht keine
Lizenz-Grafiken. Wer echte Illustrationen möchte, kann `ItemIllustration`
(`ui/components/Illustration.kt`) auf eigene PNGs umstellen.

**Ton:** Türkische Sprachausgabe über die Android-**Text-to-Speech**-Engine
(`tr-TR`). Fehlt Türkisch auf dem Gerät, zeigt die App oben im Lernpfad einen
Hinweis, der direkt zur Installation führt. Die Feedback-Töne werden zur
Laufzeit berechnet – es sind keine Audiodateien nötig.

---

## 🛠️ Bauen & installieren

### Voraussetzungen
- Android Studio (Ladybug oder neuer) **oder** JDK 17 + Android SDK
- Min SDK 33 (Android 13), Target SDK 35 – passt für das **Pixel 10 Pro**

### Variante A: Android Studio
1. Repository klonen und in Android Studio öffnen (`File → Open`)
2. Gradle-Sync abwarten
3. Gerät per USB anschließen (USB-Debugging aktiv) und auf ▶ **Run** klicken

### Variante B: Kommandozeile
```bash
./gradlew assembleDebug          # APK bauen
./gradlew installDebug           # direkt auf das angeschlossene Gerät installieren
./gradlew test                   # Unit-Tests
```
Die fertige APK liegt danach unter:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Variante C: APK ohne lokale Installation (GitHub Actions)
Bei jedem Push baut der Workflow **Android Build** automatisch Debug- und
Release-APK:
1. Auf GitHub zum Reiter **Actions** gehen
2. Den letzten Lauf öffnen
3. Unter **Artifacts** `tuerkisch-lernen-apk` herunterladen, entpacken
4. Die APK auf dem Pixel öffnen und installieren („Installation aus unbekannten Quellen“ erlauben)

> Hinweis: Die Release-APK wird mit dem Debug-Keystore signiert, damit sie
> direkt installierbar ist. Für eine Veröffentlichung im Play Store muss ein
> eigener Keystore in `app/build.gradle.kts` hinterlegt werden.

### Türkische Sprachausgabe einrichten (einmalig auf dem Gerät)
`Einstellungen → System → Sprachen & Eingabe → Text-in-Sprache-Ausgabe →
Sprachdaten installieren → Türkisch`
(oder in der App: **Profil → Eltern-Bereich → Türkische Sprachdaten installieren**)

---

## 🧱 Projektstruktur

```
app/src/main/java/de/turkischlernen/app/
├── MainActivity.kt            Einstieg (Compose)
├── TurkischApp.kt             Application + AppContainer
├── AppContainer.kt            Repository, Sprachausgabe, Töne
├── audio/
│   ├── SpeechManager.kt       Text-to-Speech tr-TR
│   └── SoundPlayer.kt         berechnete Feedback-Töne (ohne Audiodateien)
├── data/
│   ├── model/                 Word, Phrase, Lesson, LearnUnit, Exercise …
│   ├── content/
│   │   ├── Curriculum.kt      ← hier stehen ALLE Vokabeln & Einheiten
│   │   ├── Situations.kt      ← hier stehen die „Ich brauche“-Karten
│   │   └── ExerciseGenerator.kt  erzeugt die Aufgaben einer Lektion
│   └── progress/              Fortschritt (DataStore), Streak/Herzen-Logik, Abzeichen
└── ui/
    ├── theme/                 Farben, Typografie, Dark Mode
    ├── components/            Buttons, Konfetti, Illustrationen, Statusleiste
    ├── path/                  Lernpfad
    ├── lesson/                Lektion, Aufgabenansichten, Abschlussbildschirm
    ├── situations/            „Ich brauche …“
    ├── practice/              Wiederholen
    ├── profile/               Profil & Eltern-Bereich
    └── navigation/            Tabs & Navigation
```

**Technik:** Kotlin, Jetpack Compose (Material 3), Navigation Compose,
DataStore, Android TextToSpeech. Keine Netzwerk-Berechtigung, keine externen
Dienste.

---

## ➕ Neue Wörter hinzufügen

Alles steht in `data/content/Curriculum.kt`:

```kotlin
private val tiere = listOf(
    Word("kopek", "köpek", "Hund", "🐶", "Kö-pek"),
    // neue Vokabel einfach anhängen:
    Word("horoz", "horoz", "Hahn", "🐓", "Ho-ros"),
)
```

Neue Lektionen entstehen automatisch: je 3 Wörter eine Lektion, dazu eine
Prüfung pro Einheit. Eine ganz neue Einheit wird mit `buildUnit(...)` in der
Liste `units` ergänzt. Neue Alltagskarten kommen nach
`data/content/Situations.kt`.

Die Tests in `app/src/test/` prüfen automatisch, dass alle IDs eindeutig sind
und jede Lektion gültige Vokabeln enthält.

---

## ✅ Tests

```bash
./gradlew test
```
Geprüft werden u. a.: Vollständigkeit des Lernstoffs, Aufgabengenerierung
(die Lösung ist immer unter den Antworten), Streak-Berechnung, Herz-Regeneration
und XP-Bonus.

---

## 🔒 Datenschutz

Keine Konten, keine Werbung, keine Analyse, keine Internet-Berechtigung.
Der Fortschritt liegt ausschließlich im App-Speicher des Geräts und kann im
Eltern-Bereich gelöscht werden.

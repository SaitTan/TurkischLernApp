# Türkisch Lernen

Eine kindgerechte Android-App, die einem 8-jährigen deutschsprachigen Kind dabei
hilft, Türkisch zu lernen – interaktiv, spielerisch und ohne Lesekenntnisse
vorauszusetzen.

Phase 1 enthält zwei Bereiche:

- **Wörter lernen** – Multiple-Choice-Quiz mit Bildkarten in fünf Kategorien
  (Essen, Tiere, Natur, Zuhause, Farben), je 5 Wörter.
- **Ich brauche…** – 10 Alltagskarten, die beim Antippen den türkischen Satz
  vorlesen.

Audio wird über Android **Text-to-Speech** (`tr-TR`) erzeugt. Vorgefertigte
MP3/OGG-Dateien können später in `app/src/main/res/raw/` abgelegt werden – der
`AudioManager` spielt sie automatisch ab, falls am Datenmodell eine
`audioResId` gesetzt ist (sonst Fallback auf TTS).

## Voraussetzungen

- Android Studio Koala (2024.1) oder neuer
- JDK 17
- Android SDK 35 (compileSdk), Build Tools, Geräte mit Android 13 (API 33) oder höher

## Build & Installation

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug   # an einem angeschlossenen Gerät
```

Die fertige APK liegt unter `app/build/outputs/apk/debug/app-debug.apk`.

## Türkische TTS-Stimme installieren

Damit die App spricht, muss eine türkische Stimme installiert sein:

1. Android-Einstellungen öffnen
2. *System → Sprachen & Eingabe → Text-zu-Sprache-Ausgabe*
3. Beim Standard-Engine (z. B. „Speech Services by Google") auf *Sprachdaten
   installieren* tippen
4. **Türkisch (Türkei)** auswählen und herunterladen

## Projektstruktur

```
app/src/main/
├── kotlin/com/saittan/turkischlernen/
│   ├── MainActivity.kt
│   ├── audio/AudioManager.kt
│   ├── data/
│   │   ├── models/{Word,Category,Situation}.kt
│   │   └── repository/{WordRepository,SituationRepository}.kt
│   └── ui/
│       ├── components/BackBar.kt
│       ├── screens/{HomeScreen,WordCategoriesScreen,WordLearningScreen,SituationsScreen}.kt
│       └── theme/Theme.kt
└── res/
    ├── drawable/openmoji_*.png      ← OpenMoji-Illustrationen (CC BY-SA 4.0)
    ├── raw/                          ← (optional) MP3/OGG mit Muttersprachler-Audio
    └── values/{strings,colors,themes}.xml
```

## Eigene Aufnahmen einfügen

1. MP3/OGG nach `app/src/main/res/raw/` legen, z. B. `elma.mp3`.
2. In `WordRepository.kt` bei dem entsprechenden `Word` die `audioResId`
   ergänzen: `audioResId = R.raw.elma`.
3. Neu bauen – die App nutzt die Datei automatisch statt TTS.

## Hinweis zur Phase-1-Umsetzung

Diese Version implementiert das komplette Phase-1-MVP aus den Anforderungen:
beide Bereiche, alle 5 Startkategorien mit je 5 Wörtern, alle 10
Situationskarten, TTS-Fallback und kindgerechte Animationen (Konfetti bei
richtiger Antwort, Wackeln bei falscher, Bounce beim Bild-Antippen).
Lokale Fortschrittsspeicherung und Muttersprachler-Aufnahmen sind als
Erweiterungspunkte vorbereitet.

## Lizenzen

- App-Code: siehe Repository
- Icons: [OpenMoji](https://openmoji.org/) – Lizenz **CC BY-SA 4.0**

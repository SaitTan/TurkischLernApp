# 📱 Türkisch Lernen – Android App für Kinder
> **Projektbeschreibung für Claude Code**

---

## 1. Projektziel

Eine kindgerechte Android-App, die einem **8-jährigen deutschsprachigen Kind** dabei hilft, **Türkisch** zu lernen – interaktiv, spielerisch und ohne Lesekenntnisse vorauszusetzen. Der Fokus liegt auf Bilderkennung, Hörverständnis und alltäglichen Situationen.

---

## 2. Zielgerät & Plattform

| Eigenschaft | Wert |
|---|---|
| Plattform | Android |
| Zielgerät | Google Pixel 10 Pro |
| Min. Android Version | Android 13 (API Level 33) |
| Sprache der UI | Deutsch |
| Lernsprache | Türkisch |

---

## 3. Zielgruppe

- **Alter:** 8 Jahre
- **Muttersprache:** Deutsch
- **Vorkenntnisse Türkisch:** Einige Einzelwörter bekannt (z. B. *Acıktım, Susadım, Yemek, Su, Bahçe*), aber noch keine aktive Sprechfähigkeit
- **Lesefähigkeit:** Grundlegende Deutschkenntnisse vorhanden; türkische Schrift sollte daher **immer mit Audio** begleitet werden

---

## 4. Design-Prinzipien

- **Kindgerechtes UI:** Große, bunte Schaltflächen; klare, runde Formen; keine Textlastigkeit
- **Keine Ablenkungen:** Kein Werbebanner, kein Social-Feed, kein Login erforderlich
- **Positive Verstärkung:** Animationen und Töne bei richtigen Antworten (Sterne, Konfetti, fröhliche Sounds)
- **Barrierefrei:** Alle Inhalte müssen auch ohne Lesen nutzbar sein (Icons + Audio)
- **Offline-fähig:** Alle Inhalte und Audiodateien lokal gespeichert (kein Internet erforderlich)

---

## 5. App-Struktur & Navigation

Die App besteht aus einem **Hauptmenü** mit zwei Hauptbereichen:

```
Hauptmenü
├── 🖼️  Bereich 1: Wörter lernen (Bilderkarten)
└── 🙋  Bereich 2: Situationen (Ich-brauche-Karten)
```

---

## 6. Bereich 1 – Wörter lernen (Bilderkarten)

### 6.1 Konzept
Das Kind sieht ein **Bild** und versucht herauszufinden, wie es auf Türkisch heißt.

### 6.2 Ablauf (User Flow)

1. Kind wählt eine **Kategorie** (z. B. Essen, Tiere, Natur, Farben, Zuhause)
2. Ein großes **Bild** wird angezeigt
3. Darunter sind **3–4 Antwort-Schaltflächen** mit türkischen Wörtern (Multiple Choice mit Bildsymbolen)
4. **Tippt das Kind auf das Bild** (ohne zu antworten): Die App **spricht das Wort laut aus** (Text-to-Speech oder vorgefertigte Audiodatei auf Türkisch) und zeigt das Wort als Text ein
5. **Wählt das Kind eine Antwort:**
   - ✅ Richtig → Glückliche Animation + Lob-Sound + Wort wird nochmals gesprochen
   - ❌ Falsch → Sanfte Fehler-Animation + das richtige Wort wird gesprochen, kein negativer Ton

### 6.3 Startkategorien (Phase 1)

| Kategorie | Beispielwörter (Türkisch) |
|---|---|
| 🍎 Essen & Trinken | Elma, Su, Ekmek, Muz, Süt |
| 🐶 Tiere | Köpek, Kedi, Kuş, Balık, At |
| 🌳 Natur | Ağaç, Çiçek, Güneş, Bahçe, Yağmur |
| 🏠 Zuhause | Ev, Kapı, Masa, Sandalye, Yatak |
| 🎨 Farben | Kırmızı, Mavi, Sarı, Yeşil, Beyaz |

> **Hinweis:** Wörter, die das Kind schon kennt (*Acıktım, Susadım, Yemek, Su, Bahçe*), sollen hervorgehoben oder mit einem Stern markiert sein.

### 6.4 Audioanforderungen
- Jedes Wort muss als **klare, kindgerechte Sprachaufnahme auf Türkisch** hinterlegt sein
- Fallback: Android **Text-to-Speech (TTS)** mit Sprache `tr-TR`
- Audio wird bei **jedem Antippen des Bildes** abgespielt

---

## 7. Bereich 2 – Situationen (Ich-brauche-Karten)

### 7.1 Konzept
Das Kind kann in Alltagssituationen **schnell kommunizieren**, indem es auf ein Bild tippt und die App den türkischen Satz laut vorliest.

### 7.2 Ablauf (User Flow)

1. Kind öffnet den Situationsbereich
2. Raster aus **großen Bildkarten** mit deutschem Label (damit Kind sie findet)
3. Kind **tippt auf eine Karte** → App spricht den türkischen Satz laut aus
4. Optional: Türkischer Satz wird auch als Text angezeigt

### 7.3 Startkarten (Phase 1)

| Deutsches Label | Türkischer Satz | Aussprache-Hinweis |
|---|---|---|
| Ich muss auf die Toilette | Tuvalete gitmem lazım | Too-va-le-te git-mem la-zım |
| Ich habe Hunger | Acıktım | A-dschik-tım |
| Ich habe Durst | Susadım | Su-sa-dım |
| Ich möchte Wasser | Su istiyorum | Su is-ti-yo-rum |
| Ich bin müde | Yoruldum | Yo-rul-dum |
| Ich möchte spielen | Oynamak istiyorum | Oy-na-mak is-ti-yo-rum |
| Ich bin fertig | Bittim | Bit-tim |
| Hilf mir bitte | Lütfen yardım et | Lüt-fen jar-dım et |
| Ich verstehe nicht | Anlamıyorum | An-la-mı-yo-rum |
| Danke | Teşekkür ederim | Te-schek-kür e-de-rim |

### 7.4 Audioanforderungen
- Sätze als **native türkische Sprachaufnahmen** hinterlegen
- Fallback: TTS `tr-TR`
- Große, gut erkennbare **Piktogramme/Illustrationen** für jede Karte

---

## 8. Technische Anforderungen

### 8.1 Entwicklungsumgebung
- **Sprache:** Kotlin (bevorzugt) oder Flutter/Dart
- **Build-System:** Gradle
- **Min SDK:** 33 (Android 13)
- **Target SDK:** 35 (Android 15)

### 8.2 Text-to-Speech
- Android `TextToSpeech` API mit Locale `tr_TR`
- Prüfung ob `tr-TR` TTS-Sprache installiert ist; falls nicht → Hinweis an Nutzer
- Vorgefertigte MP3/OGG-Dateien bevorzugt, TTS als Fallback

### 8.3 Datenspeicherung
- Fortschritt / bereits gelernte Wörter lokal in **SharedPreferences oder Room DB** speichern
- Keine Cloud-Anbindung in Phase 1

### 8.4 Berechtigungen
- `INTERNET` – nur falls TTS-Daten nachgeladen werden müssen (optional)
- Keine weiteren Berechtigungen erforderlich

### 8.5 Projektstruktur (Empfehlung)

```
app/
├── src/main/
│   ├── kotlin/
│   │   ├── ui/
│   │   │   ├── MainActivity.kt
│   │   │   ├── HomeFragment.kt
│   │   │   ├── WordsFragment.kt          ← Bereich 1
│   │   │   ├── SituationsFragment.kt     ← Bereich 2
│   │   │   └── CategoryFragment.kt
│   │   ├── data/
│   │   │   ├── WordRepository.kt
│   │   │   ├── models/Word.kt
│   │   │   └── models/Situation.kt
│   │   └── audio/
│   │       └── AudioManager.kt
│   ├── res/
│   │   ├── drawable/      ← Bilder & Icons
│   │   ├── raw/           ← MP3/OGG Audiodateien
│   │   └── layout/        ← XML Layouts
└── build.gradle
```

---

## 9. UX-Details & Animationen

- **Richtige Antwort:** Konfetti-Animation (z. B. via `Konfetti` Library), grünes Aufleuchten, Lob auf Deutsch ("Super gemacht! 🌟")
- **Falsche Antwort:** Sanftes Wackeln der Karte, kein negativer Sound, sofort korrekte Antwort zeigen
- **Bildtipp (Hilfe):** Bild kurz vergrößern (Bounce-Animation), dann Wort einsprechen
- **Ladeübergänge:** Slide-In Animationen zwischen Screens
- **Hauptmenü:** Große, runde Kacheln mit Emoji-Icons für jeden Bereich

---

## 10. Erweiterungen (Phase 2 – spätere Version)

Diese Features sind **nicht** für Phase 1, aber sollen architektonisch vorbereitet werden:

- [ ] **Fortschrittsanzeige:** Welche Wörter wurden bereits gelernt (Sterne-System)
- [ ] **Sprachwiederholung:** Kind spricht nach (Mikrofon-Aufnahme, einfache Auswertung)
- [ ] **Quiz-Modus:** Zeitbasiertes Wörter-Ratespiel
- [ ] **Elternbereich:** Neue Wörter und Kategorien hinzufügen
- [ ] **Weitere Kategorien:** Schule, Körperteile, Kleidung, Zahlen
- [ ] **Eltern-Dashboard:** Lernstatistiken anzeigen

---

## 11. Assets & Ressourcen

### Bilder
- Kindgerechte, klare Illustrationen (Flat-Design oder Cartoon-Stil)
- Empfehlung: [OpenMoji](https://openmoji.org/), [FlatIcon](https://www.flaticon.com/) oder eigene Illustrationen
- Mindestgröße: 512×512px, PNG mit transparentem Hintergrund

### Audio
- Türkische Muttersprachler-Aufnahmen bevorzugt
- Format: MP3 oder OGG Vorbis
- Sample Rate: 44.1 kHz, Stereo
- Fallback: Android TTS `tr-TR`

---

## 12. Lieferumfang (Definition of Done)

- [ ] Lauffähige APK für Google Pixel 10 Pro
- [ ] Beide Bereiche vollständig implementiert (Wörter + Situationen)
- [ ] Alle 5 Startkategorien mit je 5 Wörtern befüllt
- [ ] Alle 10 Situationskarten implementiert
- [ ] Audio (TTS-Fallback) für alle Wörter und Sätze funktionsfähig
- [ ] Kindgerechtes UI mit Animationen
- [ ] Offline-Betrieb vollständig möglich
- [ ] README mit Build-Anleitung

---

*Erstellt für Claude Code – Version 1.0 | Sprache: Deutsch/Türkisch | Plattform: Android*

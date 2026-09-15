# Etappe 1: Lektions-Gefühl (Duolingo-Stil)

Stand: 2026-09-15 · Status: freigegeben

## Ziel

Jede Übung soll sich lebendig anfühlen wie in Duolingo: Sounds, Vibration,
Antwort-Animationen, Combo-Serie und ein belohnender Lektions-Abschluss.
Zielgruppe: Kinder 8–12 Jahre (spielerisch, Fehler klar aber freundlich).

Eigene Umsetzung – keine Grafiken, Sounds oder Figuren von Duolingo.

## Nicht Teil dieser Etappe

Maskottchen, Schatztruhen, Tagesaufgaben, Serien-Kalender, Lernpfad-Animationen,
Lottie. Diese folgen als eigene Etappen.

## Verhalten

### Antwort-Feedback
- Antippen einer Antwortkarte/Kachel: Karte skaliert beim Drücken leicht (≈0,95) und federt zurück; `tap`-Sound.
- Richtig: `correct`-Sound, kurze Vibration, Feedback-Leiste fährt von unten ein, gewählte bzw. richtige Karte hüpft einmal, zufälliger Lobspruch.
- Falsch: `wrong`-Sound, doppelte Vibration, gewählte Karte wackelt horizontal (≈400 ms), Feedback-Leiste zeigt die richtige Lösung.
- Lobsprüche (zufällig, nie zweimal direkt hintereinander derselbe): „Super!“, „Stark!“, „Klasse!“, „Richtig!“, „Harika!“, „Aferin!“, „Çok iyi!“.
- Bei „Paare finden“ bleibt es wie bisher: falsches Paar → `wrong`-Sound + Wackeln der beiden Kacheln, kein Herzverlust.

### Combo-Serie
- Zähler richtiger Antworten in Folge; eine falsche Antwort setzt ihn auf 0.
- Ab 3 in Folge: Banner „🔥 N in Folge!“ springt oben über der Aufgabe ein, `combo`-Sound mit steigender Tonhöhe je Stufe (Stufe = min(combo − 2, 5)) – ersetzt dann den `correct`-Sound, damit sich nichts überlagert –, Fortschrittsbalken glüht kurz golden.

### Kopfzeile der Lektion
- Fortschrittsbalken animiert weich auf den neuen Wert.
- Herzverlust (nur wenn Herzen begrenzt): Herz schrumpft/pulsiert, Zahl zählt herunter.

### Lektions-Abschluss (gestaffelter Ablauf)
1. Pokal (fehlerfrei) bzw. 🎉 springt mit Federeffekt ein, `celebrate`-Sound, Konfetti.
2. „Perfekt!“ / „Geschafft!“ blendet ein.
3. Drei Karten erscheinen nacheinander (≈150 ms Versatz):
   - XP zählen von 0 bis Ergebnis hoch, mit `xpTick`-Sounds (höchstens ~10 Ticks),
   - Genauigkeit in % = richtige Antworten / alle Antworten (gerundet),
   - Serie in Tagen.
4. Neu freigeschaltete Abzeichen: je Abzeichen eine Einblendung (Emoji springt ein, Titel, `badge`-Sound), nacheinander per Tippen weiter.
5. Wenn die Serie durch diese Lektion gewachsen ist: Einblendung „🔥 Serie verlängert!“ mit pulsierender Flamme und `streak`-Sound.
6. „WEITER“ führt zurück.

### Einstellungen
Eltern-Bereich im Profil erhält zwei Schalter: **Sounds** und **Vibration** (Standard: beide an).
Sie gelten sofort. Die Sprachausgabe (TTS) ist davon nicht betroffen.

## Umsetzung

| Baustein | Aufgabe |
|---|---|
| `audio/SoundPlayer` (umgebaut) | `SoundPool` mit `.ogg` aus `res/raw`; `tap`, `correct`, `wrong`, `combo(level)` (per Playback-Rate), `xpTick`, `celebrate`, `badge`, `streak`; prüft Sound-Einstellung |
| `audio/Haptics` (neu) | `success()`, `error()` über `Vibrator`/`VibrationEffect`; prüft Vibrations-Einstellung |
| `data/settings/SettingsRepository` (neu) | DataStore `settings`: `soundEnabled`, `hapticsEnabled`; unabhängig vom Fortschritts-Reset |
| `data/progress/LessonLogic` (neu, rein) | `nextCombo`, `comboLevel`, `accuracyPercent`, `newlyUnlocked(before, after)`, `pickPraise(previous, random)` |
| `LessonViewModel` | Zustände `combo`, `praise`, `correctAnswers`, `totalAnswers`, `newAchievements`, `streakIncreased`; löst Sound + Haptik aus |
| `ui/components/Animations.kt` (neu) | `Modifier.shake(trigger)`, `Modifier.bounce(trigger)`, `Modifier.pressScale(interactionSource)`, `AnimatedCounter` |
| `OptionCard`, `Tile`, `ThickProgressBar`, Herzanzeige | nutzen die Animationen |
| `LessonScreen` | Feedback-Leiste per `AnimatedVisibility`, Combo-Banner |
| `LessonCompleteScreen` | gestaffelter Ablauf, Abzeichen- und Serien-Einblendung |
| `ProfileScreen` | zwei Schalter |

### Datenfluss
Antwort → `LessonViewModel.submitAnswer` → Zustand (richtig/falsch, Combo, Lobspruch) + Sound + Haptik → UI beobachtet Zustand und startet Animationen.
Beim Abschluss: Abzeichen und Serie **vor** dem Speichern merken → `completeLesson` → Fortschritt neu lesen → `newlyUnlocked` und `streakIncreased` berechnen.

### Fehlerbehandlung
- Sound nicht geladen/abspielbar → still, kein Absturz.
- Kein Vibrator bzw. keine Unterstützung → Haptik entfällt.
- Systemeinstellung „Animationen entfernen“ → Compose verkürzt Animationen automatisch; Ablauf funktioniert trotzdem.

### Sounds & Lizenz
Alle Sounds von Kenney (CC0), abgelegt in `app/src/main/res/raw/`, Quelle in `LICENSES.md`:
- Kurze Effekte (`tap`, `correct`, `wrong`, `combo`, `xpTick`) aus „Interface Sounds“ – Kandidaten: `select_001`, `confirmation_001`, `error_004`, `glass_002`, `tick_002`.
- Längere Belohnungen (`celebrate`, `badge`, `streak`) aus „Music Jingles“. Ohne dieses Paket bleiben diese drei als berechnete Töne (bisheriger `SoundPlayer`-Stil).
- Auswahl nach Dateiname/Länge; die endgültige Zuordnung wird beim Test auf dem Handy bestätigt.

## Tests
- Unit-Tests für `LessonLogic` (Combo, Level, Genauigkeit, neue Abzeichen, Lobspruch ohne direkte Wiederholung).
- Bestehende Unit-Tests bleiben grün, `assembleRelease` baut.
- Manuelle Prüfung auf dem Handy: Sounds, Vibration, Schalter, Animationen, Abschluss-Ablauf.

# Etappe 5a: Sprech-Aufgaben

Stand: 2026-10-04 · Status: freigegeben

## Ziel

Ein sechster Aufgabentyp, bei dem das Kind das türkische Wort oder den Satz **laut
sagt**. Die App hört zu und prüft; wenn das nicht möglich ist, wird daraus automatisch
eine Nachsprech-Übung.

## Verhalten

- Die Aufgabe zeigt das türkische Wort groß, daneben den Lautsprecher zum Vorhören,
  darunter einen großen **Mikrofon-Knopf**.
- Antippen: Der Knopf pulsiert, die App hört bis zu 5 Sekunden zu.
- **Verstanden und richtig** → normale Erfolgsmeldung; darunter steht klein, was
  verstanden wurde.
- **Nicht richtig** → „Fast! Ich habe ‚…‘ gehört." mit Knopf **Nochmal versuchen**.
  Nach zwei Versuchen zählt die Aufgabe als erledigt (ohne Herzverlust), damit niemand
  hängen bleibt.
- **Keine Erkennung möglich** (Berechtigung abgelehnt, keine Erkennung auf dem Gerät,
  kein Netz) → Nachsprech-Modus: vorhören, nachsprechen, Knopf **GESAGT!**; gilt als
  richtig.
- Die Mikrofon-Berechtigung wird erst beim ersten Antippen gefragt. Wird sie abgelehnt,
  bleibt es beim Nachsprechen; im Eltern-Bereich lässt sich die Erkennung ganz
  abschalten.
- Pro Runde kommen ein bis zwei Sprech-Aufgaben vor.

## Vergleich gesprochen ↔ Ziel

- Vergleich ohne Groß-/Kleinschreibung, ohne Satzzeichen, türkische Sonderzeichen
  werden vereinfacht (ç→c, ğ→g, ı→i, ö→o, ş→s, ü→u).
- Ähnlichkeit über die Levenshtein-Distanz; ab **70 %** gilt die Antwort als richtig,
  damit Kinderaussprache nicht an einem Buchstaben scheitert.

## Nicht Teil dieser Etappe

Aussprache-Bewertung einzelner Laute, Telefonat (5b), Aufnahme speichern.

## Umsetzung

| Baustein | Aufgabe |
|---|---|
| `data/content/SpeechMatch.kt` (neu, rein) | `normalize(text)`, `similarity(a, b)`, `matches(spoken, target)`, `THRESHOLD = 0.7` |
| `audio/SpeechListener.kt` (neu) | Kapselt Androids `SpeechRecognizer` mit `tr-TR`; `available`, `start(onResult)`, `stop()`; meldet `null` bei Fehler |
| `data/model/Exercise.kt` | Neuer Typ `Speak(target)` mit Prompt „Sprich das Wort" |
| `data/content/ExerciseGenerator.kt` | Streut je Lektion ein bis zwei Sprech-Aufgaben ein |
| `ui/lesson/ExerciseInteraction.kt` | Zustand: gesprochener Text, Versuche, Ergebnis; Auswertung für `Speak` |
| `ui/lesson/ExerciseViews.kt` | `SpeakView` mit Mikrofon-Knopf, Pulsring, Rückfall auf „GESAGT!" |
| `ui/lesson/LessonScreen.kt` | Bindet `SpeakView` ein |
| `AndroidManifest.xml` | `RECORD_AUDIO`-Berechtigung |
| `data/settings/SettingsRepository.kt` | Schalter `speechRecognition` (Standard an) |
| `ui/profile/ProfileScreen.kt` | Schalter „Spracherkennung" im Eltern-Bereich |
| `AppContainer` | Stellt den `SpeechListener` bereit und beendet ihn beim Aufräumen |

### Fehlerbehandlung

Jeder Fehler der Erkennung (kein Netz, keine Sprachdaten, Zeitüberschreitung, kein
Mikrofon) führt zum Nachsprech-Modus statt zu einer Fehlermeldung. Die App bleibt
damit auch ohne Internet vollständig benutzbar.

## Tests

- `SpeechMatchTest`: Normalisierung (Groß-/Kleinschreibung, Satzzeichen, türkische
  Zeichen), identische Wörter ergeben 100 %, ein Buchstabe Unterschied bleibt über der
  Schwelle, ein völlig anderes Wort fällt durch, leere Eingabe ist nie richtig.
- `ExerciseGeneratorTest`: jede Lektion enthält mindestens eine Sprech-Aufgabe und
  bleibt bei höchstens 14 Aufgaben.
- Mikrofon und Erkennung prüft der Nutzer auf dem Pixel 10 Pro.

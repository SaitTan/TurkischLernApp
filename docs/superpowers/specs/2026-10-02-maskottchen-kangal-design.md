# Etappe 2: Maskottchen (Kangal-Hund)

Stand: 2026-10-02 · Status: freigegeben

## Ziel

Ein Maskottchen begleitet das Kind durch die App und reagiert auf das, was es tut –
wie die Figur in Duolingo, aber als eigener Charakter: ein **Kangal**, der türkische
Hirtenhund. Sandfarbener Körper, schwarze Schnauzen-Maske, Schlappohren.

Die App ist privat (nur für den Sohn des Projektinhabers), Ziel ist größtmögliche
Nähe zum Duolingo-Gefühl.

## Nicht Teil dieser Etappe

Auswahl zwischen mehreren Tieren, Kleidung/Hüte, Lottie oder Bilddateien,
Schatztruhen und Tagesaufgaben (Etappe 3), Lernpfad-Animationen (Etappe 4).

## Verhalten

### Stimmungen

| Stimmung | Aussehen | Auslöser |
|---|---|---|
| `IDLE` | sitzt, atmet, blinzelt gelegentlich, Schwanz wippt langsam | Grundzustand |
| `HAPPY` | hüpft einmal, Schwanz wedelt schnell, Zunge heraus | richtige Antwort |
| `SAD` | Ohren hängen, Kopf gesenkt, Schwanz still | falsche Antwort |
| `CHEER` | Sprung mit erhobenen Pfoten, Augen als Freudenbögen | Lektion geschafft |
| `WAVE` | hebt eine Pfote und winkt, alle paar Sekunden | Lernpfad |

### Auftritte

- **Lektion:** etwa 64 dp, dauerhaft sichtbar links in der unteren Leiste – also auch
  während der Frage (dann `IDLE` neben dem Knopf „PRÜFEN“). `HAPPY` bei richtiger,
  `SAD` bei falscher Antwort. Die Sprechblase erscheint nur nach einer Antwort.
- **Lektions-Abschluss:** groß, `CHEER`, neben Pokal/Konfetti (ersetzt das Emoji nicht).
- **Lernpfad:** `WAVE`, neben dem aktuellen Knoten ("LOS!"). Bei gesperrten Lektionen nicht sichtbar.
- **Profil:** oben statt der Eule, `IDLE`.

### Sprechblase und Stimme

- Kurze türkische Rufe, darunter klein die deutsche Übersetzung.
- Erfolg: „Harika!“ (Toll!), „Aferin!“ (Gut gemacht!), „Çok iyi!“ (Sehr gut!), „Süper!“ (Super!), „Devam!“ (Weiter so!)
- Trost: „Olsun!“ (Macht nichts!), „Tekrar dene!“ (Versuch es nochmal!), „Önemli değil!“ (Nicht schlimm!)
- Jubel: „Bravo!“ (Bravo!), „Tebrikler!“ (Glückwunsch!), „Çok güzel!“ (Sehr schön!)
- Nie zweimal direkt hintereinander derselbe Ruf (gleiche Regel wie bei den Lobsprüchen).
- Gesprochen wird **angehängt** (`QUEUE_ADD`), damit der Ruf das vorgelesene Vokabel-Wort
  nicht abschneidet: erst „elma“, dann „Harika!“.
- Ist der Sound-Schalter im Eltern-Bereich aus, bleibt der Ruf stumm; die Sprechblase
  erscheint trotzdem. Fehlt die türkische Sprachausgabe, ebenso.

## Umsetzung

| Baustein | Aufgabe |
|---|---|
| `ui/mascot/MascotMood.kt` (neu) | Aufzählung `IDLE`, `HAPPY`, `SAD`, `CHEER`, `WAVE` |
| `ui/mascot/Kangal.kt` (neu) | `@Composable Kangal(mood, modifier, size)`; zeichnet den Hund mit `Canvas` aus Kreisen, Ovalen und Bögen. Dauer-Animationen (Atmen, Blinzeln, Schwanz) über `rememberInfiniteTransition`, Sprünge über `Animatable` je Stimmungswechsel |
| `ui/mascot/MascotBubble.kt` (neu) | Sprechblase mit Zipfel, federt über `Modifier.popIn` ein |
| `data/content/MascotPhrases.kt` (neu, reine Logik) | `data class MascotPhrase(tr, de)`, Listen `success`, `comfort`, `cheer`, `fun pick(list, previous, random)` |
| `audio/SpeechManager.kt` | `speak(text, slow, queue: Boolean = false)` – `QUEUE_ADD` statt `QUEUE_FLUSH`, wenn `queue` true ist |
| `ui/lesson/LessonViewModel.kt` | Feld `mascotPhrase`, gesetzt in `submitAnswer` (Erfolg/Trost) und beim Abschluss (Jubel) |
| `ui/lesson/LessonScreen.kt` | Hund + Sprechblase in der Rückmeldungs-Leiste; spricht den Ruf an, wenn Sound an ist |
| `ui/lesson/LessonCompleteScreen.kt` | Jubelnder Hund neben dem Pokal |
| `ui/path/PathScreen.kt` | Winkender Hund am aktuellen Knoten |
| `ui/profile/ProfileScreen.kt` | Hund statt Eulen-Emoji |

### Datenfluss

Es kommt kein neuer Zustand dazu: Die Stimmung wird aus `AnswerState` bzw. dem
Bildschirm abgeleitet. Nur der aktuelle Ruf wird im ViewModel gehalten, damit er
sich nicht wiederholt und beim erneuten Zeichnen stabil bleibt.

### Fehlerbehandlung

- Keine türkische Sprachausgabe oder Sound aus → Sprechblase ohne Stimme.
- Systemeinstellung „Animationen entfernen“ → Compose kürzt die Animationen automatisch.

## Tests

- Unit-Tests für `MascotPhrases`: jede Kategorie gefüllt, Übersetzungen nicht leer,
  `pick` liefert nie den vorherigen Ruf.
- Bestehende Tests bleiben grün, `assembleRelease` baut in CI.
- Aussehen und Bewegung prüft der Nutzer auf dem Pixel 10 Pro.

# Etappe 6: Schlaues Wiederholen

Stand: 2026-10-04 · Status: freigegeben

## Ziel

Wörter werden wiederholt, kurz bevor sie vergessen werden. Jedes Wort hat einen
Lernstand; die App legt es in wachsenden Abständen wieder vor.

## Verhalten

### Lernstand je Wort

- Stufen 0 bis 5. Start: Stufe 0, fällig sofort.
- **Richtig** beantwortet → eine Stufe höher, neue Fälligkeit nach
  **1, 3, 7, 14, 30 Tagen** (Stufe 1 bis 5).
- **Falsch** beantwortet → eine Stufe zurück (mindestens 0), wieder **morgen** fällig.
- Gezählt wird jede Antwort in Lektionen und im Üben-Tab.

### Auffrischen

- Im Tab **Üben** oben ein Knopf **„AUFFRISCHEN (N Wörter)"** mit der Zahl der heute
  fälligen Wörter; er startet eine Runde mit genau diesen Wörtern.
- Reihenfolge: am längsten überfällig zuerst, dann niedrigste Stufe.
- Ist nichts fällig: „Heute ist nichts fällig – gut gemacht!", Knopf ausgegraut.
- Die Runde zählt wie freies Wiederholen (XP begrenzt, keine Spielzeit).

### Anzeige in der Wörterliste

- Je Wort fünf Punkte: gefüllte Punkte = Lernstand (●●●○○).
- Fällige Wörter bekommen einen farbigen Rand und stehen oben in der Liste.

### Tagesaufgabe

- Neue Aufgabe im Vorrat: **„Frische 10 Wörter auf"**; gezählt werden heute
  wiederholte fällige Wörter.

## Nicht Teil dieser Etappe

Eigene Lernkurven je Aufgabentyp, Statistiken über Wochen, Export.

## Umsetzung

| Baustein | Aufgabe |
|---|---|
| `data/progress/ReviewLogic.kt` (neu, rein) | `INTERVALS = [1, 3, 7, 14, 30]`, `MAX_LEVEL = 5`, `nextLevel(level, correct)`, `dueDate(level, today)`, `isDue(review, today)`, `sortByUrgency(reviews, today)`, `encode/decode` |
| `data/progress/UserProgress` | `reviews: Map<String, ItemReview>`, `reviewedToday: Int` |
| `data/progress/ProgressRepository` | `recordAnswers(itemIds, correct, today)` schreibt Stufen und Fälligkeiten; Tageszähler für wiederholte Wörter |
| `ui/lesson/LessonViewModel` | meldet jede Antwort an das Repository |
| `ui/practice/PracticeScreen` | Auffrischen-Knopf mit Anzahl, Sortierung, Punkte je Wort |
| `data/content/DailyQuests` | neue Aufgabe `REVIEW` |

### Datenfluss

Ein Wort hat genau einen Eintrag `wortId:stufe:datum`. Die Fälligkeit wird beim
Antworten neu berechnet; nichts läuft im Hintergrund.

### Fehlerbehandlung

Unlesbare Einträge werden übersprungen. Wörter ohne Eintrag gelten als fällig, sobald
sie einmal gelernt wurden.

## Tests

- `ReviewLogicTest`: Stufenauf- und -abstieg, Grenzen 0 und 5, Fälligkeitsdaten je
  Stufe, „fällig" bei heute und früher, Sortierung nach Dringlichkeit, Speicherformat
  hin und zurück, kaputte Einträge.
- `DailyQuestsTest`: die neue Aufgabe zählt den richtigen Zähler.

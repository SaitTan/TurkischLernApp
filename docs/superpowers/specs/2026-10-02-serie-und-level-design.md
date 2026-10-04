# Etappe 3b: Serien-Kalender und Level-Aufstieg

Stand: 2026-10-02 · Status: freigegeben

## Ziel

Zwei weitere Duolingo-Bausteine: ein Serien-Kalender hinter der Flamme und ein Level,
das aus den gesammelten XP wächst, mit gefeiertem Aufstieg.

## Verhalten

### Serien-Kalender

- Tippen auf die 🔥 in der Kopfzeile öffnet eine Einblendung.
- Darin die aktuelle Woche von Montag bis Sonntag als sieben Kreise:
  gelernte Tage orange mit Flamme, heute mit Rand hervorgehoben, kommende Tage grau.
- Darunter die aktuelle Serie und die längste je erreichte Serie.
- Dafür merkt sich die App die Tage, an denen gelernt wurde – höchstens die letzten
  90 Tage, damit nichts unbegrenzt wächst.

### Level

- **100 XP ergeben ein Level.** Level 1 ab 0 XP, Level 2 ab 100 XP, Level 3 ab 200 XP.
- Im Profil steht „Level N" mit einem Balken, wie weit es bis zum nächsten Level ist.

### Level-Aufstieg

- Steigt das Level durch eine abgeschlossene Lektion, erscheint im Abschluss-Ablauf
  nach Abzeichen und Serie eine Einblendung: große Levelzahl, Konfetti, jubelnder Kangal.
- Dazu: **eine Schatztruhe** (öffnet sich direkt im Anschluss) und
  **5 Minuten PlayStation** zusätzlich.
- Die Truhe eines Levels gibt es nur einmal (eigene Truhen-Id je Level).

## Nicht Teil dieser Etappe

Ligen oder Ranglisten, Freunde, Monatsansicht des Kalenders, Serien-Reparatur
("streak freeze").

## Umsetzung

| Baustein | Aufgabe |
|---|---|
| `data/progress/LevelLogic.kt` (neu, rein) | `XP_PER_LEVEL = 100`, `LEVEL_UP_PLAY_MINUTES = 5`, `levelOf(xp)`, `xpIntoLevel(xp)`, `xpToNext(xp)`, `leveledUp(vorher, nachher)`, `chestId(level)` |
| `data/progress/StreakCalendar.kt` (neu, rein) | `weekOf(date)` (Montag bis Sonntag), `isActive(date, activeDays)`, `longestStreak(activeDays)`, `MAX_HISTORY_DAYS = 90` |
| `data/progress/UserProgress` | Neu: `activeDays: Set<String>`, `longestStreak: Int` |
| `data/progress/ProgressRepository` | `completeLesson` trägt den Tag ein, kürzt die Liste auf 90 Tage und schreibt die längste Serie fort; neu `addPlayMinutes(minutes)` |
| `ui/stats/StreakOverlay.kt` (neu) | Wochen-Einblendung mit Kreisen, aktueller und längster Serie |
| `ui/components/StatsBar.kt` | Flamme bekommt einen Klick |
| `ui/navigation/AppRoot.kt` | Zustand für die Einblendung |
| `ui/lesson/LessonViewModel` | Level vor und nach dem Speichern vergleichen, `newLevel` merken, 5 Minuten gutschreiben |
| `ui/lesson/LessonCompleteScreen` | Level-Einblendung im Ablauf, danach die Truhe des Levels |
| `ui/profile/ProfileScreen` | Level mit Fortschrittsbalken |

### Datenfluss

Das Level wird aus `totalXp` berechnet und nirgends gespeichert. Der Aufstieg ergibt
sich aus dem Vergleich der XP vor und nach dem Speichern – dieselbe Stelle, an der
schon neue Abzeichen und die Serie geprüft werden.

### Fehlerbehandlung

Fehlt die Liste der aktiven Tage (alte Installation), zeigt der Kalender nur den
heutigen Tag. Eine bereits geöffnete Level-Truhe bleibt zu.

## Tests

- `LevelLogicTest`: 0/99 XP → Level 1, 100 → Level 2, 250 → Level 3; `xpIntoLevel`
  und `xpToNext` passen zusammen; Aufstieg wird erkannt, Gleichstand nicht;
  Truhen-Id je Level verschieden.
- `StreakCalendarTest`: `weekOf` liefert Montag bis Sonntag und enthält das Datum;
  `longestStreak` zählt zusammenhängende Tage, Lücken brechen ab, leere Liste ergibt 0.
- Aussehen prüft der Nutzer auf dem Pixel 10 Pro.

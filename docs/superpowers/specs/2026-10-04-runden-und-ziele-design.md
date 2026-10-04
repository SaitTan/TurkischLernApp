# Etappe 4: Runden je Lektion, Ziele-Tab, feinere Einstellungen

Stand: 2026-10-04 · Status: freigegeben

## Ziel

Näher an Duolingo: Eine Lektion besteht aus mehreren Runden, der Lernpfad arbeitet nur
noch mit Farben statt Häkchen, Tagesziel und Belohnungen bekommen einen eigenen Tab,
und im Eltern-Bereich lassen sich Spielzeit und einzelne Töne regeln.

## Verhalten

### 1. Drei Runden je Lektion

- Jede Lektion wird **dreimal** gespielt (`ROUNDS_PER_LESSON = 3`), jede Runde mit
  höchstens 14 Aufgaben wie bisher.
- Antippen eines Knotens öffnet eine kleine Karte (wie bei Duolingo): Titel der Lektion,
  „Runde 2 von 3", Knopf **LOS!** bzw. **WIEDERHOLEN**, Abbrechen durch Tippen daneben.
- Eine Lektion gilt als abgeschlossen, wenn drei Runden geschafft sind. Erst dann wird
  die nächste Lektion frei.

### 2. Knoten ohne Häkchen – nur Farben und ein Ring

| Zustand | Farbe | Ring |
|---|---|---|
| Gesperrt | grau | leer, Schloss-Symbol |
| Offen / angefangen | Farbe der Einheit | zeigt geschaffte Runden (0–2 von 3) |
| Fertig (3 Runden) | grün | voll |
| Komplett (eine Extra-Runde danach) | gold | voll |

- Die Emojis ✅ 🏆 👑 verschwinden; alle freien Knoten zeigen denselben weißen Stern,
  gesperrte ein Schloss.
- Nach dem Abschluss bietet die Karte „WIEDERHOLEN" an; diese Extra-Runde macht den
  Knoten **gold**. Danach bleibt er gold, weitere Runden sind jederzeit möglich.

### 3. Spielzeit je Runde

- **1 Minute PlayStation je gespielter Runde** (`PLAY_MINUTES_PER_ROUND = 1`), also
  3 Minuten für eine komplette Lektion, 1 weitere für die Gold-Runde.
- Freies Wiederholen im Übungs-Tab gibt weiterhin keine Minuten.

### 4. Neuer Tab „Ziele" statt „Ich brauche"

- Die untere Leiste zeigt: **Lernen · Ziele · Üben · Profil**.
- „Ich brauche" entfällt ersatzlos, auch die Alltagskarten (`Situations`).
- Der Tab **Ziele** (🎯) enthält, was bisher über dem Lernpfad stand:
  Tagesziel mit Balken, die drei Tagesaufgaben, die Tages-Truhe (wenn alle drei
  erledigt sind) und eine Übersicht „Truhen auf dem Lernpfad: X offen".
- Der Lernpfad beginnt dadurch direkt mit der ersten Einheit.

### 5. Spielzeit bearbeiten

Im Eltern-Bereich statt der drei festen Knöpfe:
- aktueller Stand groß,
- Knöpfe **−30 / −15 / −5 / −1** und **+1 / +5 / +15**,
- ein Eingabefeld zum direkten Setzen (nur Zahlen, 0–999).

### 6. Töne einzeln schalten

Im Eltern-Bereich unter dem Haupt-Schalter „Sounds" sechs einzelne Schalter:
Tipp-Ton, Antwort-Töne, Kangal-Stimme, Combo-Ton, XP-Ticks, Belohnungs-Töne
(Fanfare, Abzeichen, Serie, Truhe). Ist „Sounds" aus, schweigt alles.

## Nicht Teil dieser Etappe

Ligen, Freunde, Monatsmissionen, Herz-Nachschub per Werbung.

## Umsetzung

| Baustein | Aufgabe |
|---|---|
| `data/progress/LessonRounds.kt` (neu, rein) | `ROUNDS_PER_LESSON`, `PLAY_MINUTES_PER_ROUND`, `NodeStage` (LOCKED, OPEN, IN_PROGRESS, DONE, GOLD), `stageOf(rounds, unlocked)`, `isDone`, `isGold`, `nextRoundLabel(rounds)` |
| `data/progress/UserProgress` | `lessonRounds: Map<String, Int>`; `completedLessons` und `goldLessons` daraus abgeleitet |
| `data/progress/ProgressRepository` | Runden je Lektion speichern (StringSet „id:n"), Spielzeit je Runde, Truhen unverändert |
| `ui/path/PathScreen` | Ring-Knoten, Farbzustände, Start-Karte beim Antippen; Tagesziel/Tagesaufgaben entfernt |
| `ui/goals/GoalsScreen.kt` (neu) | Tagesziel, Tagesaufgaben, Tages-Truhe, Truhen-Übersicht |
| `ui/navigation/AppRoot` | Tab „Ich brauche" raus, „Ziele" rein |
| `data/settings/SettingsRepository` | Sechs neue Schalter; `AppSettings.sounds` als eigenes Objekt |
| `audio/SoundPlayer` | Prüft je Ton den passenden Schalter |
| `ui/profile/ProfileScreen` | Ton-Schalter, Spielzeit-Editor |
| `data/content/Situations.kt`, `ui/situations/` | gelöscht |

### Datenfluss

Die Runden stehen im Fortschritt und bestimmen alles Weitere: Farbe des Knotens,
Freischaltung der nächsten Lektion, Spielzeit, Abzeichen. Es kommt kein zweiter
Zustand dazu.

### Fehlerbehandlung

Alte Installationen haben nur `completedLessons`: Diese Lektionen zählen beim ersten
Start als „3 Runden geschafft", damit niemand von vorne anfangen muss. Kaputte
Einträge im Rundenspeicher werden übersprungen.

## Tests

- `LessonRoundsTest`: Zustand je Rundenzahl, fertig ab 3, gold ab 4, Beschriftung
  „Runde 2 von 3", Spielzeit je Runde.
- `ProgressMigrationTest`: alte `completedLessons` ergeben 3 Runden.
- Bestehende Tests bleiben grün.
- Aussehen prüft der Nutzer auf dem Pixel 10 Pro.

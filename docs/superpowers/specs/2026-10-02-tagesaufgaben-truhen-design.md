# Etappe 3a: Tagesaufgaben und Schatztruhen

Stand: 2026-10-02 · Status: freigegeben

## Ziel

Gründe zum Weitermachen, wie bei Duolingo: drei Tagesaufgaben und Schatztruhen, aus
denen Bonus-XP oder neue Sachen für Figur und Hund kommen.

Etappe 3b (Serien-Kalender, Level-Aufstieg) folgt getrennt.

## Verhalten

### Tagesaufgaben

- Drei Aufgaben pro Tag, oben auf dem Lernpfad unter dem Tagesziel, jede mit
  Fortschrittsbalken und Haken.
- Aus einem Vorrat gewürfelt, Startwert ist das Datum: derselbe Tag ergibt immer
  dieselben drei Aufgaben, auch nach einem Neustart. Die drei sind immer verschieden.
- Beispiele: „Schaffe 2 Lektionen", „Sammle 20 XP", „Beende eine Runde ohne Fehler",
  „Übe 10 Wörter", „Antworte 15-mal richtig".
- Erledigte Aufgabe: grüner Haken, kurzer Hüpfer, Ton.
- Alle drei erledigt → eine Schatztruhe (einmal pro Tag).

### Schatztruhen

- Auf dem Lernpfad nach **jeder dritten Lektion** ein Truhen-Knoten; dazu die
  Tages-Truhe bei drei erledigten Aufgaben.
- Antippen öffnet die Truhe: Deckel springt auf, Lichtstrahlen, Konfetti, der Kangal
  jubelt daneben.
- Inhalt: 5–20 Bonus-XP, oder ein noch gesperrtes Accessoire für den Hund bzw. ein
  gesperrtes T-Shirt für die Figur. Sind alle Teile frei, gibt es immer XP.
- Jede Truhe öffnet nur einmal; geöffnete Truhen bleiben sichtbar, aber offen.
- Eine Truhe auf dem Lernpfad ist erst erreichbar, wenn die drei Lektionen davor
  abgeschlossen sind.

### Freischaltbare Teile

Die heutigen Accessoires und T-Shirts bleiben frei verfügbar. Neu dazu kommen
gesperrte Teile: Krone, Halstuch und Winter-Mütze für den Hund, Regenbogen- und
Gold-Shirt für die Figur. Im Profil stehen sie mit 🔒 und sind nicht antippbar,
bis sie aus einer Truhe kommen.

## Nicht Teil dieser Etappe

Serien-Kalender, Level-Aufstieg, Edelsteine oder eine Währung, Shop, Zeitlimits.

## Umsetzung

| Baustein | Aufgabe |
|---|---|
| `data/content/DailyQuests.kt` (neu, rein) | `QuestKind` (LESSONS, XP, PERFECT, WORDS, CORRECT), `Quest(id, kind, goal, label, emoji)`, `questsForDay(date): List<Quest>` (drei verschiedene, aus dem Datum gewürfelt), `progressOf(quest, progress): Int`, `isDone(quest, progress)` |
| `data/progress/UserProgress` | Neue Tageszähler `lessonsToday`, `perfectToday`, `wordsToday`, `correctToday`; Reset beim Datumswechsel wie bei `xpToday`. Neu außerdem `openedChests: Set<String>` und `unlockedItems: Set<String>` |
| `data/progress/ChestLogic.kt` (neu, rein) | `chestIdForLesson(index)`, `chestAfterEveryNth = 3`, `dailyChestId(date)`, `roll(random, unlocked): ChestReward` mit `ChestReward.Xp(amount)` oder `ChestReward.Item(id)` |
| `data/settings/AvatarConfig.kt` | `AccessoryOption`/`ColorOption` bekommen `locked: Boolean = false`; neue gesperrte Teile |
| `data/progress/ProgressRepository` | Zähler mitschreiben in `completeLesson`; `openChest(chestId, reward)` schreibt XP und Freischaltung in einem Zug |
| `ui/path/DailyQuestsCard.kt` (neu) | Karte mit den drei Aufgaben |
| `ui/rewards/ChestOverlay.kt` (neu) | Gezeichnete Truhe mit Deckel-Animation, Lichtstrahlen, Konfetti und Gewinn-Anzeige |
| `ui/path/PathScreen` | Truhen-Knoten zwischen den Lektionen, Tages-Truhe über der Liste |
| `ui/profile/ProfileScreen` | Gesperrte Teile mit 🔒, nicht antippbar |

### Datenfluss

Die Aufgaben rechnen nur mit Werten, die schon im Fortschritt stehen – kein doppelter
Zustand. Beim Öffnen einer Truhe entscheidet `ChestLogic.roll` anhand der bereits
freigeschalteten Teile; das Ergebnis schreibt `openChest` in einem Vorgang.

### Fehlerbehandlung

Geöffnete Truhen bleiben zu. Sind alle Teile frei, gibt es XP. Unbekannte gespeicherte
Ids werden ignoriert. Truhen vor nicht abgeschlossenen Lektionen sind gesperrt.

## Tests

- `DailyQuestsTest`: gleicher Tag → gleiche drei Aufgaben; anderer Tag → andere Auswahl
  möglich; immer drei verschiedene; `progressOf` und `isDone` rechnen richtig; jede
  Vorlage hat Text und Emoji.
- `ChestLogicTest`: Truhe nach jeder dritten Lektion; `roll` liefert nie ein schon
  freigeschaltetes Teil; XP immer zwischen 5 und 20; ohne freie Teile immer XP.
- Aussehen und Animation prüft der Nutzer auf dem Pixel 10 Pro.

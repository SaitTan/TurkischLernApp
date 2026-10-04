# Avatar zum Selbstgestalten

Stand: 2026-10-02 · Status: freigegeben

## Ziel

Das Kind gestaltet seinen eigenen Kangal – wie man bei Duolingo seinen Avatar einstellt.
Der geänderte Hund erscheint überall in der App, nicht nur im Profil.

## Verhalten

Im Profil gibt es den Bereich **„Mein Hund"** mit:

- **Vorschau:** der Hund groß, ändert sich sofort mit.
- **Fellfarbe:** fünf Kreise zum Antippen – `sand` (Standard), `hellbraun`, `grau`, `weiss`, `schwarz`.
  Jede Farbe bringt Fell, dunkleren Ton (Ohren, Schwanzkontur, Hüften) und hellen Ton
  (Brustfleck, Pfoten) mit, damit die Zeichnung stimmig bleibt.
- **Accessoire:** fünf Kacheln – `keins` (Standard), `muetze`, `brille`, `schleife`, `schal`.
- **Name:** Eingabefeld, höchstens 12 Zeichen, Standard „Kangal". Leerer Name ist erlaubt,
  dann steht im Profil wieder „Mein Türkisch".

Jede Änderung wird sofort gespeichert, es gibt keinen Speichern-Knopf.
Der Name steht im Profil über den Statistiken.

**Auftritte:** Lektion, Lektions-Abschluss, Lernpfad und Profil zeigen denselben,
gestalteten Hund.

## Nicht Teil dieser Etappe

Mehrere Tiere zur Auswahl, Hintergründe, freischaltbare oder gekaufte Gegenstände,
mehrere Profile.

## Umsetzung

| Baustein | Aufgabe |
|---|---|
| `data/settings/AvatarConfig.kt` (neu) | `data class AvatarConfig(furId, accessoryId, name)`; `AvatarOptions.furs` (Id, Anzeigename, Fell-, Dunkel- und Hell-Farbe als Long) und `AvatarOptions.accessories` (Id, Anzeigename, Emoji für die Kachel); `fur(id)`/`accessory(id)` fallen bei unbekannter Id auf den Standard zurück |
| `data/settings/SettingsRepository.kt` | Drei neue Schlüssel (`avatar_fur`, `avatar_accessory`, `avatar_name`); `AppSettings` bekommt `avatar: AvatarConfig`; Setter `setAvatarFur`, `setAvatarAccessory`, `setAvatarName` |
| `ui/mascot/Kangal.kt` | Neuer Parameter `avatar: AvatarConfig = AvatarConfig()`; Fellfarben kommen daraus; `drawAccessory` zeichnet Mütze, Brille, Schleife oder Schal auf den Kopf – mit dem Kopf gedreht, damit es bei traurig mitkippt |
| `ui/profile/ProfileScreen.kt` | Bereich „Mein Hund" mit Vorschau, Farbkreisen, Accessoire-Kacheln, Namensfeld; Name als Überschrift |
| `ui/lesson/LessonScreen.kt`, `LessonCompleteScreen.kt`, `ui/path/PathScreen.kt` | Übergeben den gespeicherten Avatar |

### Datenfluss

`SettingsRepository.current` (StateFlow) liefert `AppSettings` samt Avatar. Die Bildschirme
lesen ihn per `collectAsState`; der Profil-Bereich schreibt per Setter zurück. Kein
zusätzlicher Zustand, keine neue Schicht.

### Fehlerbehandlung

Unbekannte oder alte Ids (z. B. nach einem Update) fallen auf den Standard zurück, statt
die Zeichnung zu zerstören. Ein zu langer Name wird auf 12 Zeichen gekürzt.

## Tests

- `AvatarConfigTest`: Standard ist sandfarben/ohne Accessoire/„Kangal"; `fur("grau")` und
  `accessory("muetze")` finden den richtigen Eintrag; unbekannte Id liefert den Standard;
  alle Ids sind eindeutig; jede Fellfarbe hat drei verschiedene Farbtöne.
- Aussehen prüft der Nutzer auf dem Pixel 10 Pro.

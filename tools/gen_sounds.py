"""Erzeugt eigene, Duolingo-ähnliche Feedback-Sounds als WAV (16 Bit, mono).

Keine fremden Aufnahmen: alles additive Synthese (Marimba-/Glocken-Klang).
Aufruf (aus dem Repo-Stamm): python tools/gen_sounds.py app/src/main/res/raw
"""
import math
import os
import struct
import sys
import wave

RATE = 44100


def note(freq, dur, start, partials, decay, vol=1.0, pitch_drop=0.0):
    """Eine Note als (start, samples)-Paar."""
    n = int(dur * RATE)
    out = []
    for i in range(n):
        t = i / RATE
        f = freq * (1.0 - pitch_drop * min(t / dur, 1.0))
        attack = min(i / (RATE * 0.004), 1.0)
        s = 0.0
        for mult, amp, dec in partials:
            s += amp * math.sin(2 * math.pi * f * mult * t) * math.exp(-t * decay * dec)
        out.append(s * attack * vol)
    return int(start * RATE), out


def mix(notes, total):
    buf = [0.0] * int(total * RATE)
    for start, samples in notes:
        for i, v in enumerate(samples):
            if start + i < len(buf):
                buf[start + i] += v
    peak = max(abs(v) for v in buf) or 1.0
    # Auf -1 dBFS normalisieren, Ende sanft ausblenden (kein Knacken).
    gain = 0.89 / peak
    fade = int(0.01 * RATE)
    for i in range(fade):
        buf[-1 - i] *= i / fade
    return [v * gain for v in buf]


def write(path, buf):
    with wave.open(path, "wb") as w:
        w.setnchannels(1)
        w.setsampwidth(2)
        w.setframerate(RATE)
        w.writeframes(b"".join(struct.pack("<h", int(max(-1, min(1, v)) * 32767)) for v in buf))


# Marimba: Grundton + 4. und 10. Teilton, obere klingen schneller ab.
MARIMBA = [(1, 1.0, 1.0), (4, 0.25, 3.0), (10, 0.06, 6.0)]
# Glocke/Glitzer: unharmonische Teiltöne, lang ausklingend.
BELL = [(1, 1.0, 1.0), (2.76, 0.45, 1.6), (5.4, 0.2, 2.5), (8.93, 0.08, 3.5)]
# Dumpf: Grundton + leise ungerade Obertöne.
DULL = [(1, 1.0, 1.0), (3, 0.18, 2.0), (5, 0.05, 3.0)]

C5, E5, G5 = 523.25, 659.25, 783.99
C6, E6, G6, C7 = 1046.5, 1318.5, 1568.0, 2093.0

SOUNDS = {
    # Helles, aufsteigendes "Ding-Ding".
    "sfx_correct": (0.5, [
        note(C6, 0.35, 0.00, MARIMBA, 9),
        note(G6, 0.42, 0.09, MARIMBA, 8),
    ]),
    # Dumpfes, absteigendes "Bonk-bonk" – freundlich, nicht schrill.
    "sfx_wrong": (0.42, [
        note(233.1, 0.20, 0.00, DULL, 14, pitch_drop=0.06),
        note(174.6, 0.30, 0.12, DULL, 11, pitch_drop=0.08),
    ]),
    # Weiches "Plopp" beim Antippen.
    "sfx_tap": (0.06, [
        note(900, 0.06, 0.0, [(1, 1.0, 1.0), (2, 0.2, 2.0)], 60, pitch_drop=0.35),
    ]),
    # Glitzer-Ton für die Combo (Tonhöhe variiert der SoundPlayer).
    "sfx_combo": (0.45, [
        note(E6, 0.30, 0.00, BELL, 9, vol=0.8),
        note(C7, 0.40, 0.06, BELL, 8, vol=0.7),
    ]),
    # Sehr kurzes Klicken beim Hochzählen.
    "sfx_xp_tick": (0.035, [
        note(2400, 0.035, 0.0, [(1, 1.0, 1.0)], 120),
    ]),
    # Fröhliche Fanfare: Arpeggio nach oben + Schlussakkord.
    "sfx_celebrate": (1.25, [
        note(C5, 0.30, 0.00, MARIMBA, 6),
        note(E5, 0.30, 0.10, MARIMBA, 6),
        note(G5, 0.30, 0.20, MARIMBA, 6),
        note(C6, 0.90, 0.32, MARIMBA, 3.5),
        note(E6, 0.90, 0.32, MARIMBA, 3.5, vol=0.6),
        note(G6, 0.90, 0.32, BELL, 3.5, vol=0.35),
    ]),
    # Abzeichen: schnelles Glitzer-Arpeggio.
    "sfx_badge": (1.0, [
        note(G5, 0.5, 0.00, BELL, 5, vol=0.7),
        note(C6, 0.5, 0.07, BELL, 5, vol=0.7),
        note(E6, 0.5, 0.14, BELL, 5, vol=0.7),
        note(G6, 0.8, 0.21, BELL, 3.5, vol=0.8),
        note(C7, 0.8, 0.28, BELL, 3.5, vol=0.5),
    ]),
    # Serie: warmer, aufsteigender Dreiklang.
    "sfx_streak": (0.95, [
        note(E5, 0.4, 0.00, MARIMBA, 5),
        note(659.25 * 1.5, 0.4, 0.12, MARIMBA, 5),
        note(E6, 0.75, 0.24, MARIMBA, 3.5),
        note(E6 * 1.5, 0.75, 0.24, BELL, 3.5, vol=0.35),
    ]),
}

if __name__ == "__main__":
    target = sys.argv[1]
    os.makedirs(target, exist_ok=True)
    for name, (total, notes) in SOUNDS.items():
        path = os.path.join(target, name + ".wav")
        write(path, mix(notes, total))
        print(f"{name}.wav  {os.path.getsize(path) // 1024} KB")

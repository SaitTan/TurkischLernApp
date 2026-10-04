package de.turkischlernen.app

import de.turkischlernen.app.data.content.CallScripts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CallScriptsTest {

    @Test
    fun `es gibt mehrere Telefonate mit Titel und Symbol`() {
        assertTrue(CallScripts.all.size >= 3)
        CallScripts.all.forEach { script ->
            assertTrue(script.title.isNotBlank())
            assertTrue(script.subtitle.isNotBlank())
            assertTrue(script.emoji.isNotBlank())
        }
    }

    @Test
    fun `jedes Telefonat hat einen gueltigen Anfang`() {
        CallScripts.all.forEach { script ->
            assertNotNull("Anfang fehlt in ${script.id}", script.line(script.startId))
        }
    }

    @Test
    fun `jede Antwort fuehrt zu einer vorhandenen Zeile oder zum Ende`() {
        CallScripts.all.forEach { script ->
            script.lines.forEach { line ->
                line.options.forEach { option ->
                    val ziel = option.nextId
                    if (ziel != null) {
                        assertNotNull(
                            "Unbekanntes Ziel $ziel in ${script.id}",
                            script.line(ziel)
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `die Zeilen-Ids sind je Telefonat eindeutig`() {
        CallScripts.all.forEach { script ->
            val ids = script.lines.map { it.id }
            assertEquals(ids.size, ids.toSet().size)
        }
    }

    @Test
    fun `jedes Telefonat endet irgendwo`() {
        CallScripts.all.forEach { script ->
            assertTrue(
                "Kein Ende in ${script.id}",
                script.lines.any { it.options.isEmpty() }
            )
        }
    }

    @Test
    fun `jede Zeile und Antwort hat Tuerkisch und Deutsch`() {
        CallScripts.all.forEach { script ->
            script.lines.forEach { line ->
                assertTrue(line.partnerTr.isNotBlank())
                assertTrue(line.partnerDe.isNotBlank())
                line.options.forEach { option ->
                    assertTrue(option.tr.isNotBlank())
                    assertTrue(option.de.isNotBlank())
                }
            }
        }
    }
}

package de.turkischlernen.app

import android.app.Application

class TurkischApp : Application() {

    private var currentContainer: AppContainer? = null

    /**
     * Wird bei Bedarf neu aufgebaut – z. B. wenn die App nach dem Beenden der
     * letzten Activity aufgeräumt hat und danach wieder gestartet wird.
     */
    val container: AppContainer
        get() = currentContainer ?: AppContainer(this).also { currentContainer = it }

    override fun onCreate() {
        super.onCreate()
        currentContainer = AppContainer(this)
    }

    /** Gibt Sprachausgabe und Sound-Pool frei. */
    fun releaseContainer() {
        currentContainer?.release()
        currentContainer = null
    }
}

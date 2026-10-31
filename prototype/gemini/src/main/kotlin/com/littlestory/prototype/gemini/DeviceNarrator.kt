package com.littlestory.prototype.gemini

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.littlestory.core.common.config.AppConfig
import com.littlestory.domain.player.Narrator
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale
import javax.inject.Inject

private const val PAUSE_SUFFIX = "_pause"

/**
 * Debug prototype narration with the phone's own text-to-speech: free and offline, but the voice
 * and its Kannada support depend on the phone. Each page is one utterance, followed by a short pause.
 */
class DeviceNarrator
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : Narrator {
        override fun narrate(pages: List<String>, fromPage: Int, languageTag: String, speed: Float): Flow<Int> =
            callbackFlow {
                var engine: TextToSpeech? = null
                val lastPage = pages.lastIndex
                engine =
                    TextToSpeech(context) { status ->
                        val tts = engine
                        if (status != TextToSpeech.SUCCESS || tts == null || fromPage > lastPage) {
                            close()
                            return@TextToSpeech
                        }
                        tts.language = Locale.forLanguageTag(languageTag)
                        tts.setSpeechRate(speed * AppConfig.Player.NARRATION_RATE)
                        tts.setOnUtteranceProgressListener(
                            object : UtteranceProgressListener() {
                                override fun onStart(utteranceId: String) {
                                    utteranceId.toIntOrNull()?.let { trySend(it) }
                                }

                                override fun onDone(utteranceId: String) {
                                    if (utteranceId == lastPage.toString()) close()
                                }

                                @Deprecated("Required override on older APIs")
                                override fun onError(utteranceId: String) {
                                    close()
                                }
                            },
                        )
                        for (page in fromPage..lastPage) {
                            tts.speak(pages[page], TextToSpeech.QUEUE_ADD, null, page.toString())
                            if (page < lastPage) {
                                tts.playSilentUtterance(
                                    AppConfig.Player.NARRATION_PAGE_PAUSE_MS,
                                    TextToSpeech.QUEUE_ADD,
                                    "$page$PAUSE_SUFFIX",
                                )
                            }
                        }
                    }
                awaitClose {
                    engine?.stop()
                    engine?.shutdown()
                }
            }
    }

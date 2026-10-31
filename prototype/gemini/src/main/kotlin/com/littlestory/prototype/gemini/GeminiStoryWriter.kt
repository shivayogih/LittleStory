package com.littlestory.prototype.gemini

import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.ai.FirebaseAI
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.AspectRatio
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.ai.type.imageConfig
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.littlestory.core.common.config.AppConfig
import com.littlestory.core.common.language.SupportedLanguages
import com.littlestory.core.common.story.StoryImageFiles
import com.littlestory.domain.generation.StoryWriter
import com.littlestory.domain.generation.StoryWriterResult
import com.littlestory.domain.model.StoryCreationRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "GeminiPrototype"
private const val APP_NAME = "littlestory-gemini-prototype"
private const val SYSTEM_PROMPT_ASSET = "prototype/story_system_prompt.txt"
private const val JSON_MIME = "application/json"
private const val IMAGE_HERO = "the hero child"
private const val IMAGE_STYLE =
    "Soft, warm children's picture-book illustration, flat shapes with gentle gradients, rounded friendly " +
        "characters, no text or letters anywhere, nothing scary. Keep the main subject in the centre."

/** Age bands used by the system prompt, keyed by their upper age. */
private val ageBands = listOf(5 to "3-5", 8 to "6-8", 12 to "9-12")

/**
 * Debug-only prototype writer on the Gemini free tier through Firebase AI Logic.
 *
 * - The child's name never leaves the device: the idea is sent with [HERO_TOKEN] and the name is
 *   filled in afterwards. Use made-up names anyway, because free-tier prompts may be reviewed by Google.
 * - The story text and the cover are made while the child waits; page pictures follow in the
 *   background and the Reader shows them once saved. A picture that fails leaves the drawn art.
 */
@Singleton
class GeminiStoryWriter
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : StoryWriter {
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        private val palettes = setOf("friendship", "adventure", "bedtime", "indian_tales", "space", "festivals")

        private val app: FirebaseApp? by lazy {
            if (BuildConfig.FIREBASE_APP_ID.isEmpty()) {
                null
            } else {
                val options =
                    FirebaseOptions
                        .Builder()
                        .setApplicationId(BuildConfig.FIREBASE_APP_ID)
                        .setApiKey(BuildConfig.FIREBASE_API_KEY)
                        .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
                        .build()
                runCatching { FirebaseApp.initializeApp(context, options, APP_NAME) }
                    .recoverCatching { FirebaseApp.getInstance(APP_NAME) }
                    .onSuccess {
                        FirebaseAppCheck
                            .getInstance(
                                it,
                            ).installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
                    }.getOrNull()
            }
        }

        override suspend fun write(request: StoryCreationRequest, storyId: String): StoryWriterResult {
            val firebase = app ?: return StoryWriterResult.Unavailable
            val ai = Firebase.ai(firebase, GenerativeBackend.googleAI())
            val story = requestStory(ai, request)
            return when {
                story == null -> StoryWriterResult.Failed
                !story.isUsable -> StoryWriterResult.Rejected
                else -> {
                    drawPictures(ai, story, storyId)
                    StoryWriterResult.Written(story.toDraft(request.heroName.trim(), languageOf(request), palettes))
                }
            }
        }

        /** The model's story, or null when the request failed, timed out or the reply was not JSON. */
        private suspend fun requestStory(ai: FirebaseAI, request: StoryCreationRequest): GeminiStory? {
            val systemPrompt =
                context.assets
                    .open(SYSTEM_PROMPT_ASSET)
                    .bufferedReader()
                    .use { it.readText() }
            val textModel =
                ai.generativeModel(
                    modelName = BuildConfig.TEXT_MODEL,
                    generationConfig = generationConfig { responseMimeType = JSON_MIME },
                    systemInstruction = content { text(systemPrompt) },
                )
            val reply =
                withTimeoutOrNull(AppConfig.Generation.WRITER_TIMEOUT_MS) {
                    runCatching { textModel.generateContent(userPrompt(request)).text }
                        .onFailure { Log.w(TAG, "Story request failed", it) }
                        .getOrNull()
                }
            return reply?.let(::parseGeminiStory)
        }

        /** The cover while the child waits; page pictures in the background. */
        private suspend fun drawPictures(ai: FirebaseAI, story: GeminiStory, storyId: String) {
            val imageModel = imageModel(ai)
            val filesDir = context.filesDir
            drawPicture(
                imageModel,
                story,
                story.coverImagePrompt,
                AspectRatio.SQUARE_1x1,
                StoryImageFiles.cover(filesDir, storyId),
            )
            scope.launch {
                story.pages.forEachIndexed { index, page ->
                    drawPicture(
                        imageModel,
                        story,
                        page.imagePrompt,
                        AspectRatio.LANDSCAPE_4x3,
                        StoryImageFiles.page(filesDir, storyId, index),
                    )
                }
            }
        }

        private fun userPrompt(request: StoryCreationRequest): String {
            val language = SupportedLanguages.fromTag(request.languageTag).nativeName
            val band = ageBands.firstOrNull { request.heroAge <= it.first }?.second ?: ageBands.last().second
            val hero =
                if (request.heroName.isBlank()) {
                    "a child aged ${request.heroAge}; give the hero a simple friendly first name instead of $HERO_TOKEN"
                } else {
                    "$HERO_TOKEN, aged ${request.heroAge}"
                }
            return """
                Write a story.
                Language: $language
                Age band: $band
                Hero: $hero
                Theme: ${request.themeId}
                Mood: ${request.moodId}
                Duration: ${request.durationMinutes} minutes
                Story idea from the parent: ${anonymise(request.prompt, request.heroName)}
                """.trimIndent()
        }

        private fun languageOf(request: StoryCreationRequest) = SupportedLanguages.fromTag(request.languageTag).tag

        private fun imageModel(ai: FirebaseAI): (AspectRatio) -> GenerativeModel =
            { ratio ->
                ai.generativeModel(
                    modelName = BuildConfig.IMAGE_MODEL,
                    generationConfig =
                        generationConfig {
                            responseModalities = listOf(ResponseModality.TEXT, ResponseModality.IMAGE)
                            imageConfig = imageConfig { aspectRatio = ratio }
                        },
                )
            }

        private suspend fun drawPicture(
            model: (AspectRatio) -> GenerativeModel,
            story: GeminiStory,
            scene: String,
            ratio: AspectRatio,
            target: File,
        ) {
            if (scene.isBlank()) return
            val prompt = "$IMAGE_STYLE\nCharacters: ${story.characters}\nScene: $scene".replace(HERO_TOKEN, IMAGE_HERO)
            val bytes =
                withTimeoutOrNull(AppConfig.Generation.WRITER_TIMEOUT_MS) {
                    runCatching {
                        model(ratio)
                            .generateContent(prompt)
                            .inlineDataParts
                            .firstOrNull()
                            ?.inlineData
                    }.onFailure { Log.w(TAG, "Picture request failed", it) }
                        .getOrNull()
                } ?: return
            withContext(Dispatchers.IO) {
                target.parentFile?.mkdirs()
                target.writeBytes(bytes)
            }
        }
    }

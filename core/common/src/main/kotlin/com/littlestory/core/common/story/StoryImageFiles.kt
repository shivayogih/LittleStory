package com.littlestory.core.common.story

import java.io.File

/**
 * Where a generated story's pictures live in app-private storage: `story_images/{storyId}/`.
 * The writer saves them here and the UI shows them when present; the drawn cover art (ADR-010)
 * stays the fallback. "Delete all data" removes [root].
 */
object StoryImageFiles {
    private const val DIR = "story_images"
    private const val COVER = "cover.png"

    fun root(filesDir: File): File = File(filesDir, DIR)

    fun storyDir(filesDir: File, storyId: String): File = File(root(filesDir), storyId)

    fun cover(filesDir: File, storyId: String): File = File(storyDir(filesDir, storyId), COVER)

    fun page(filesDir: File, storyId: String, pageIndex: Int): File =
        File(
            storyDir(filesDir, storyId),
            "page_$pageIndex.png",
        )
}

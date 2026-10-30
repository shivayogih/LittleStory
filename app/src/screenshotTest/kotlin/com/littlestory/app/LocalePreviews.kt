package com.littlestory.app

import androidx.compose.ui.tooling.preview.Preview

/**
 * Compose Preview Screenshot Testing (AGP 9.5 test suite, experimental): every key screen renders
 * in English, Kannada, and Kannada at 200 % font scale on a 430 × 932 dp phone (the design frame).
 * Reference images are committed only after the owner approves them (CLAUDE.md: never auto-approve
 * baselines).
 */

@Preview(name = "en", locale = "en", widthDp = 430, heightDp = 932, showBackground = true)
@Preview(name = "kn", locale = "kn", widthDp = 430, heightDp = 932, showBackground = true)
@Preview(name = "kn-200", locale = "kn", fontScale = 2f, widthDp = 430, heightDp = 932, showBackground = true)
annotation class LocalePreviews

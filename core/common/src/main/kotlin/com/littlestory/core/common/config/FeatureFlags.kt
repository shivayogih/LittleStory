package com.littlestory.core.common.config

/**
 * On/off switches for simulated or optional features (no remote config in the MVP).
 * A plain data class so Hilt can provide it and tests can override it.
 */
data class FeatureFlags(
    val demoSubscription: Boolean = true,
    val simulatedNarration: Boolean = true,
    val simulatedDownloads: Boolean = true,
    val bedtimeReminder: Boolean = true,
    val notificationsInbox: Boolean = true,
    /**
     * Labels the in-app privacy policy and family-safety text as a draft awaiting legal review.
     * Turn off only once a reviewer has approved the final wording (docs/security/play-families-compliance.md).
     */
    val draftLegalText: Boolean = true,
    /** Debug builds only: offers en-XA / ar-XB in the language list. */
    val pseudoLocales: Boolean = false,
)

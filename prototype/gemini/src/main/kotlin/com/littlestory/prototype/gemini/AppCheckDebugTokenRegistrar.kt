package com.littlestory.prototype.gemini

import com.google.firebase.appcheck.debug.InternalDebugSecretProvider
import com.google.firebase.components.Component
import com.google.firebase.components.ComponentRegistrar

/**
 * Gives the App Check debug provider a fixed token (`littlestory.appCheckDebugToken` in
 * local.properties) that the owner registered in the Firebase console, so a debug build installed
 * from a file works without reading logcat. Without a token the provider makes its own and logs it.
 */
class AppCheckDebugTokenRegistrar : ComponentRegistrar {
    override fun getComponents(): List<Component<*>> =
        if (BuildConfig.APP_CHECK_DEBUG_TOKEN.isEmpty()) {
            emptyList()
        } else {
            listOf(
                Component
                    .builder(InternalDebugSecretProvider::class.java)
                    .factory { InternalDebugSecretProvider { BuildConfig.APP_CHECK_DEBUG_TOKEN } }
                    .build(),
            )
        }
}

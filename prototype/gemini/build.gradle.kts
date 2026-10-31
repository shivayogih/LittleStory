import groovy.json.JsonSlurper
import java.util.Properties

plugins {
    id("littlestory.android.library")
    id("littlestory.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

/**
 * Debug-only Gemini prototype (docs/api/backend-ai-accounts-design.md, "Prototype before P-B1").
 * Reads the owner's Firebase config from app/google-services.json (git-ignored). Without it every
 * field is empty, the writer reports itself unavailable and the app keeps the simulation.
 */
private val debugPackage = "com.littlestory.app.debug"
private val firebase: Map<String, String> =
    run {
        val file = rootProject.file("app/google-services.json")
        if (!file.exists()) return@run emptyMap()
        @Suppress("UNCHECKED_CAST")
        val json = JsonSlurper().parse(file) as Map<String, Any?>
        val project = json["project_info"] as Map<String, Any?>
        val clients = json["client"] as List<Map<String, Any?>>
        val client =
            clients.firstOrNull {
                val info = it["client_info"] as Map<String, Any?>
                (info["android_client_info"] as Map<String, Any?>)["package_name"] == debugPackage
            } ?: return@run emptyMap()
        mapOf(
            "projectId" to project["project_id"].toString(),
            "appId" to (client["client_info"] as Map<String, Any?>)["mobilesdk_app_id"].toString(),
            "apiKey" to ((client["api_key"] as List<Map<String, Any?>>).first()["current_key"]).toString(),
        )
    }

fun localProperty(name: String): String {
    val file = rootProject.file("local.properties")
    if (!file.exists()) return ""
    val properties = Properties()
    file.inputStream().use { properties.load(it) }
    return properties.getProperty(name).orEmpty()
}

android {
    buildFeatures { buildConfig = true }
    defaultConfig {
        fun field(name: String, value: String) = buildConfigField("String", name, "\"$value\"")
        field("FIREBASE_PROJECT_ID", firebase["projectId"].orEmpty())
        field("FIREBASE_APP_ID", firebase["appId"].orEmpty())
        field("FIREBASE_API_KEY", firebase["apiKey"].orEmpty())
        // App Check debug token registered in the Firebase console; local.properties only, never committed.
        field("APP_CHECK_DEBUG_TOKEN", localProperty("littlestory.appCheckDebugToken"))
        field("TEXT_MODEL", providers.gradleProperty("littlestory.gemini.textModel").get())
        field("IMAGE_MODEL", providers.gradleProperty("littlestory.gemini.imageModel").get())
    }
}

dependencies {
    implementation(projects.domain)
    implementation(projects.core.common)
    implementation(libs.firebase.ai)
    implementation(libs.firebase.appcheck.debug)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
}

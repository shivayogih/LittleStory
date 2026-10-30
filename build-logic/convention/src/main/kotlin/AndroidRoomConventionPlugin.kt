import androidx.room.gradle.RoomExtension
import com.littlestory.buildlogic.libs
import com.littlestory.buildlogic.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/** Room with KSP and exported schemas (committed under schemas/ so migrations can be reviewed). */
class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.google.devtools.ksp")
        pluginManager.apply("androidx.room")
        extensions.configure<RoomExtension> {
            schemaDirectory("$projectDir/schemas")
        }
        dependencies {
            add("implementation", libs.library("androidx-room-runtime"))
            add("implementation", libs.library("androidx-room-ktx"))
            add("ksp", libs.library("androidx-room-compiler"))
            add("testImplementation", libs.library("androidx-room-testing"))
        }
    }
}

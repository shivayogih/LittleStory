package com.littlestory.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.littlestory.app.skeleton.SkeletonScreen
import com.littlestory.core.common.language.SupportedLanguages
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.language.AppLanguage
import dagger.hilt.android.AndroidEntryPoint

// AppCompatActivity (not ComponentActivity) so per-app language changes recreate the activity on API < 33.
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            var selected by remember { mutableStateOf(AppLanguage.current()) }
            LittleStoryTheme {
                SkeletonScreen(
                    languages = SupportedLanguages.enabled,
                    selected = selected,
                    onLanguageSelected = {
                        selected = it
                        AppLanguage.apply(it)
                    },
                )
            }
        }
    }
}

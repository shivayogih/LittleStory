package com.littlestory.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.littlestory.app.navigation.AppNavHost
import com.littlestory.core.common.safety.ParentalGateChallenges
import com.littlestory.core.designsystem.theme.LittleStoryTheme
import com.littlestory.core.ui.safety.LocalParentalGateChallenges
import com.littlestory.feature.home.navigation.HomeRoute
import com.littlestory.feature.onboarding.navigation.OnboardingRoute
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// AppCompatActivity (not ComponentActivity) so per-app language changes recreate the activity on API < 33.
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val appViewModel: AppViewModel by viewModels()

    @Inject lateinit var parentalGateChallenges: ParentalGateChallenges

    override fun onCreate(savedInstanceState: Bundle?) {
        // The system splash stays until the first-run flag is read, so there is no blank frame (SCR-19).
        installSplashScreen().setKeepOnScreenCondition { appViewModel.startState.value == StartState.Loading }
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val start by appViewModel.startState.collectAsStateWithLifecycle()
            LittleStoryTheme {
                CompositionLocalProvider(LocalParentalGateChallenges provides parentalGateChallenges) {
                    when (start) {
                        StartState.Loading -> Unit
                        StartState.Onboarding -> AppNavHost(startDestination = OnboardingRoute)
                        StartState.Home -> AppNavHost(startDestination = HomeRoute)
                    }
                }
            }
        }
    }
}

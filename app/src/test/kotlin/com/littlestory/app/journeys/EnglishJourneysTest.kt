package com.littlestory.app.journeys

import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Journeys A–G with the device in English, on a phone-sized screen (Pixel-class 411 × 891 dp). */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], application = HiltTestApplication::class, qualifiers = "en-w411dp-h891dp")
class EnglishJourneysTest : Journeys("en")

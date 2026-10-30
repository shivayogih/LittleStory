package com.littlestory.app.journeys

import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Journeys A–G with the device in Kannada, on a phone-sized screen (Pixel-class 411 × 891 dp). */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], application = HiltTestApplication::class, qualifiers = "kn-w411dp-h891dp")
class KannadaJourneysTest : Journeys("kn")

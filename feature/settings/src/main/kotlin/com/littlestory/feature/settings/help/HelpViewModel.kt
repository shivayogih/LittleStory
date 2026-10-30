package com.littlestory.feature.settings.help

import androidx.lifecycle.ViewModel
import com.littlestory.core.common.config.AppInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/** SCR-22 (LS-115): static help text plus the build's version from `:app`. */
@HiltViewModel
class HelpViewModel
    @Inject
    constructor(
        val appInfo: AppInfo,
    ) : ViewModel()

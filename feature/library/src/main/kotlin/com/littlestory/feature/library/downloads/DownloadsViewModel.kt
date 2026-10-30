package com.littlestory.feature.library.downloads

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlestory.core.common.config.AppConfig
import com.littlestory.domain.browse.catalogue
import com.littlestory.domain.browse.flatMapContext
import com.littlestory.domain.browse.observeBrowseContext
import com.littlestory.domain.repository.CatalogRepository
import com.littlestory.domain.repository.DownloadsRepository
import com.littlestory.domain.repository.LibraryRepository
import com.littlestory.domain.repository.PreferencesRepository
import com.littlestory.domain.repository.ProfilesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SUBSCRIPTION_TIMEOUT_MS = 5_000L

@Immutable
data class DownloadItem(
    val storyId: String,
    val title: String,
    val sizeBytes: Long,
    val paletteId: String,
    val motifId: String,
)

@Immutable
data class DownloadsUiState(
    val loading: Boolean = true,
    val items: List<DownloadItem> = emptyList(),
    val quotaBytes: Long = AppConfig.Downloads.SIMULATED_QUOTA_BYTES,
) {
    val usedBytes: Long get() = items.sumOf { it.sizeBytes }

    /** Share of the simulated quota in use; never drawn thinner than the design's minimum once something is saved. */
    val usedFraction: Float
        get() =
            if (items.isEmpty()) {
                0f
            } else {
                (usedBytes.toFloat() / quotaBytes).coerceIn(AppConfig.Downloads.STORAGE_BAR_MIN_FRACTION, 1f)
            }
}

/** SCR-17 (LS-101): simulated offline downloads. Nothing is fetched; sizes are illustrative. */
@HiltViewModel
class DownloadsViewModel
    @Inject
    constructor(
        profiles: ProfilesRepository,
        preferences: PreferencesRepository,
        catalog: CatalogRepository,
        library: LibraryRepository,
        private val downloads: DownloadsRepository,
    ) : ViewModel() {
        val state: StateFlow<DownloadsUiState> =
            observeBrowseContext(profiles, preferences)
                .flatMapContext { ctx ->
                    combine(catalog.catalogue(ctx.languageTag), library.created(ctx.childId), downloads.downloads) {
                        catalogue,
                        created,
                        rows,
                        ->
                        val stories = (created + catalogue.stories).associateBy { it.id }
                        DownloadsUiState(
                            loading = false,
                            items =
                                rows.mapNotNull { row ->
                                    stories[row.storyId]?.let {
                                        DownloadItem(it.id, it.title, row.sizeBytes, it.art.paletteId, it.art.motifId)
                                    }
                                },
                        )
                    }
                }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS), DownloadsUiState())

        fun onRemove(storyId: String) {
            viewModelScope.launch { downloads.remove(storyId) }
        }
    }

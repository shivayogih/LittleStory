package com.littlestory.data.settings

import com.littlestory.core.common.language.SupportedLanguages
import com.littlestory.data.catalog.MockContentSource
import com.littlestory.data.db.NotificationReadDao
import com.littlestory.data.db.NotificationReadEntity
import com.littlestory.domain.model.NotificationItem
import com.littlestory.domain.model.NotificationKind
import com.littlestory.domain.model.NotificationSection
import com.littlestory.domain.model.Plan
import com.littlestory.domain.repository.NotificationsRepository
import com.littlestory.domain.repository.SubscriptionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val FALLBACK = SupportedLanguages.default.tag
private val lenientJson = Json { ignoreUnknownKeys = true }

@Serializable
internal data class NotificationEntry(
    val id: String,
    val title: String = "",
    val body: String = "",
    val section: String = "",
    val kind: String = "",
)

@Serializable
internal data class NotificationsFile(val notifications: List<NotificationEntry> = emptyList())

@Serializable
internal data class PlanEntry(
    val id: String,
    val priceMinorUnits: Long = 0,
    val currencyCode: String = "",
    val billedYearly: Boolean = false,
    val bestValue: Boolean = false,
    val maxChildren: Int = 1,
)

@Serializable
internal data class PlansFile(val plans: List<PlanEntry> = emptyList())

private inline fun <reified T> MockContentSource.parse(fileName: String): T? =
    read(fileName)?.let { text -> runCatching { lenientJson.decodeFromString<T>(text) }.getOrNull() }

/**
 * Mock inbox (LS-112): items come from `mock/notifications_{tag}.json`, English defines the ids
 * and structure and a translation overrides text per item. Read state is kept in Room.
 */
class NotificationsRepositoryImpl(
    private val source: MockContentSource,
    private val readDao: NotificationReadDao,
    private val dispatcher: CoroutineDispatcher,
) : NotificationsRepository {
    override fun notifications(languageTag: String): Flow<List<NotificationItem>> =
        combine(entries(languageTag), readDao.observeReadIds()) { items, readIds ->
            items.map { it.copy(read = it.id in readIds) }
        }

    override suspend fun markRead(id: String) = readDao.upsert(NotificationReadEntity(id))

    override suspend fun markAllRead() {
        val ids = source.parse<NotificationsFile>("notifications_$FALLBACK.json")?.notifications.orEmpty()
        readDao.upsertAll(ids.map { NotificationReadEntity(it.id) })
    }

    private fun entries(languageTag: String): Flow<List<NotificationItem>> =
        flow {
            val language = languageTag.substringBefore('-').lowercase()
            val base = source.parse<NotificationsFile>("notifications_$FALLBACK.json")?.notifications.orEmpty()
            val local =
                if (language == FALLBACK) {
                    emptyMap()
                } else {
                    source
                        .parse<NotificationsFile>("notifications_$language.json")
                        ?.notifications
                        .orEmpty()
                        .associateBy { it.id }
                }
            emit(
                base.map { entry ->
                    val text = local[entry.id]
                    NotificationItem(
                        id = entry.id,
                        title = text?.title?.ifBlank { null } ?: entry.title,
                        body = text?.body?.ifBlank { null } ?: entry.body,
                        read = false,
                        section = enumValueOrNull<NotificationSection>(entry.section) ?: NotificationSection.Earlier,
                        kind = enumValueOrNull<NotificationKind>(entry.kind) ?: NotificationKind.NewStories,
                    )
                },
            )
        }.flowOn(dispatcher)
}

/** Illustrative plans from `mock/plans.json` (LS-113). Prices are not localized text, so there is one file. */
class SubscriptionRepositoryImpl(
    private val source: MockContentSource,
    private val dispatcher: CoroutineDispatcher,
) : SubscriptionRepository {
    override val plans: Flow<List<Plan>> =
        flow {
            emit(
                source.parse<PlansFile>("plans.json")?.plans.orEmpty().map {
                    Plan(it.id, it.priceMinorUnits, it.currencyCode, it.billedYearly, it.bestValue, it.maxChildren)
                },
            )
        }.flowOn(dispatcher)
}

private inline fun <reified E : Enum<E>> enumValueOrNull(name: String): E? =
    enumValues<E>().firstOrNull {
        it.name ==
            name
    }

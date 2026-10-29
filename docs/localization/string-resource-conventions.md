# String resource conventions

Purpose: tell developers and translators how strings are named, where they live, and how to write them so they translate correctly into Kannada and future languages.

Status: Draft for owner review — P1, 29 Oct 2026

Sources: brief §4.4 rules 2–4, 12, 17; §9.4 · [../architecture/module-structure.md](../architecture/module-structure.md) (string ownership) · workbook Translations sheet (295 planned keys) · design doc §3.7 (suggestion chips).

## 1. Where strings live

| Kind | Location |
| --- | --- |
| Feature copy | the feature module's `src/main/res/values/strings.xml` + `values-kn/strings.xml` |
| Shared actions, nav labels, content descriptions, generic errors, enum labels | `:core:ui` (single owner: localization engineer) |
| App name, splash | `:app` |
| Story titles, synopses, pages, notification items | JSON in `:data/src/main/assets/mock/*_{tag}.json`, not `strings.xml` |
| Never | Kotlin/Compose literals, ViewModels, `:domain`, `:core:common` |

## 2. Naming

`<scope>_<element>[_<qualifier>]`, lowercase snake case.

| Prefix | Use | Example |
| --- | --- | --- |
| `<screen>_` | copy owned by one screen | `setup_title`, `create_prompt_label` |
| `action_` | buttons and links reused across screens | `action_continue`, `action_try_again` |
| `nav_` | bottom navigation | `nav_my_stories` |
| `cd_` | content descriptions (TalkBack) | `cd_favourite_add` |
| `meta_` | metadata formats | `meta_category_minutes` |
| `error_` / `<screen>_error_` | errors | `create_error_prompt_empty` |
| `state_` | toggled state labels | `state_downloaded` |
| `label_` | small shared labels | `label_available_offline` |
| `interest_`, `category_`, `theme_`, `mood_` | enum labels keyed by stable ID | `category_indian_tales` |

Keys describe meaning, not English wording, so they stay valid when copy changes.

## 3. Formatting rules

| Rule | Do | Don't |
| --- | --- | --- |
| Positional arguments | `<string name="home_title">Stories for %1$s</string>` | `"Stories for " + name` |
| Plurals for counts | `<plurals name="category_story_count"><item quantity="one">%1$d story</item><item quantity="other">%1$d stories</item></plurals>` | `"$count STORIES"` |
| Whole sentences | one key per sentence, word order free for the translator | building sentences from fragments |
| Case | write normal case; uppercase is a style decision that must not apply to Kannada (no case) | `TONIGHT'S ADVENTURE` in the resource |
| Apostrophes and quotes | use typographic `’` or escape `\'` | unescaped `'` (aapt error) |
| Numbers, dates, prices | pass formatted values from locale-aware formatters as arguments | `"%1$d:%2$02d"` time building |
| Brand | `LittleStory` is not translated; mark `translatable="false"` in `:app` | translating the brand |
| Gender | write gender-neutral copy or use the child's name | "helped **his** friend" (R-06) |
| Context for translators | XML comment above each non-obvious key: screen ID, max length | – |

## 4. Fixing the prototype's concatenation

The prototype builds suggestion text as `{child} had {suggestion in lowercase}.` (design §3.7). That fails in Kannada, where word order differs and there is no lowercase. Each suggestion becomes a complete sentence with the child's name as an argument:

```xml
<!-- values/strings.xml (:feature:create) — SCR-07 suggestion chips fill the prompt -->
<string name="create_suggestion_school_label">A day at school</string>
<string name="create_suggestion_school_text">%1$s helped a friend at school and played cricket in the evening…</string>
<string name="create_suggestion_family_label">A family adventure</string>
<string name="create_suggestion_family_text">%1$s had a family adventure.</string>
<string name="create_suggestion_funny_label">Something funny happened</string>
<string name="create_suggestion_funny_text">Something funny happened to %1$s today.</string>
```

```xml
<!-- values-kn/strings.xml — Draft, needs native review -->
<string name="create_suggestion_family_text">%1$s ಕುಟುಂಬದೊಂದಿಗೆ ಒಂದು ಸಾಹಸ ಮಾಡಿದರು.</string>
```

The same approach replaces `Create {child}'s profile` (`setup_create_profile`, `%1$s`), `Results for "{query}"` (`discover_results_for`, `%1$s`) and `Reminder saved for {time}` (`reminder_saved_toast`, time pre-formatted with the locale's time format).

## 5. Compose usage

```kotlin
Text(text = stringResource(R.string.home_title, childName))
Text(text = pluralStringResource(R.plurals.category_story_count, count, count))
Icon(painter = …, contentDescription = stringResource(CoreUiR.string.cd_favourite_add))
```

Tests look nodes up by test tag or by `stringResource` in the test, never by an English literal, so the same test runs in `en` and `kn`.

## 6. Translation status markers

Every Kannada string added before review carries a comment and a workbook row:

```xml
<!-- Needs native review -->
<string name="nav_my_stories">ನನ್ನ ಕಥೆಗಳು</string>
```

Statuses: Missing → Draft → Needs native review → Approved (only the named reviewer sets Approved).

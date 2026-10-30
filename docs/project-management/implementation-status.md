# Implementation status and Kannada draft ledger

Maintained alongside the code until the owner syncs it into `LittleStory_Jira_Backlog.xlsx` (Backlog *Status* column and *Translations* sheet). The workbook is not edited by agents because the file contains drawings and images that a script rewrite would drop.

## Ticket status

| Ticket | State | Notes |
|---|---|---|
| LS-013 | Implemented | 24 vector icons; the design's 21st icon is still unidentified |
| LS-014–LS-020 | Implemented | brand, buttons, form, feedback, story, chrome components and the debug component catalogue |
| LS-032, LS-042 | Implemented | Hilt app graph, pseudo-locales in debug |
| LS-037, LS-038 | Implemented | domain models and validators, Room schema v1, DataStore preferences |
| LS-050 | Implemented | typed routes for every feature module, registered in `:app` |
| LS-051 | Implemented | SplashScreen API held until first-run flag loads (SCR-19 realised by the system splash) |
| LS-052 | Implemented | system bar icon styling, night scene |
| LS-060 | Implemented | three slides, skip, pager dots |
| LS-061, LS-045 | Implemented | setup form; language select is registry-driven; parent name field dropped per D14; avatar add tile omitted |
| LS-062 | Implemented | active child state; switching children is covered at the repository level, the switcher UI arrives with Settings |
| LS-036 | Implemented (P4) | `assets/mock/{stories,categories}_{en,kn}.json`, loader with per-field English fallback, `CatalogRepository`; notifications, plans and profiles JSON arrive with their screens |
| LS-044 | Implemented, draft | Kannada text for five stories and 13 categories; `story_lanterns_jaipur` is deliberately missing to prove the fallback; needs native review |
| LS-070 | Implemented (P4) | Home: hero, continue card (only when progress exists), explore tiles, made-for row ranked by interests, culture banner |
| LS-071, LS-072 | Implemented (P4) | Discover: search (title, synopsis, category; locale-aware), chips, featured, grid, bedtime row, no-results state |
| LS-073 | Implemented (P4) | Category: any category id or `all`; sort Recommended, Shortest first, Newest; story-count plural |
| LS-074 | Implemented (P4, wired in P5) | Story detail: favourite and simulated download toggles; Listen opens the Player, Read opens the Reader |
| LS-082 | Implemented (P5) | Simulated generation (4 % every 120 ms, three stages), generated story built from `generated_{en,kn}.json` and saved to Room (schema v2, auto-migration adds the age range); notifications and plans mock JSON |
| LS-080, LS-081 | Implemented (P5) | Create form: prompt with counter and empty-state error, suggestion pills, hero select, theme chips, more options (mood, duration, story language), safety note |
| LS-083 | Implemented (P5) | Generating: night scene, progress and stages; close pauses, Try again resumes from 8 %, Back to your idea; auto-advances to Story Ready |
| LS-084 | Implemented (P5) | Story Ready: cover, synopsis, peek, favourite/download, Saved row opens My Stories on Created by me, Listen and Read |
| LS-090, LS-091 | Implemented (P5) | Reader: page-by-page with progress, A−/A+ text size (0.9–1.3, remembered), favourite, play opens the Player; page saved as reading progress |
| LS-092–LS-094 | Implemented (P5) | Player: simulated narration labelled as such, seek, ±15 s, chapters, speed cycle (remembered), volume, sleep-timer sheet (10/15/30 min, end of story); position saved as listening progress so Home and My Stories can resume |
| LS-100 | Implemented (P5) | My Stories: search, tabs (All, Created by me, Downloaded, Favourites), recently played card, rows with play; created stories open Story Ready |
| LS-101 | Implemented (P5) | Downloads: simulated storage card (1 GB quota), list with remove, empty state and tip |
| LS-110 | Implemented (P5) | Profile hub: child card, children, language sheet (UI and story language), reminder/notifications/Wi-Fi toggles, audio summary, downloads, subscription (behind `demoSubscription`) |
| LS-063 | Implemented (P5) | Child profiles: switch active child, edit and add open the Setup form |
| LS-111 | Implemented (P5) | Bedtime reminder: toggle, time picker, days (locale first day of week); saved on the device only, nothing is scheduled and no permission is requested |
| LS-112 | Implemented (P5) | Notifications inbox from mock JSON: Today/Earlier, unread dots, mark one or all read; taps open My Stories, Reminder or Discover |
| LS-113 | Implemented (P5) | Subscription: illustrative plans with locale-formatted prices, confirmation dialog; no billing |
| LS-075 | Implemented (P6) | One `toggle` on `FavouritesRepository`, serialised in the singleton implementation; every story surface uses it, so rapid taps from any screen settle consistently |
| LS-046 | Implemented (P6) | Language preferences (SCR-20): app language (AppCompat) and story language listed from the registry; Profile subtitle shows both when they differ. Built from settings components (D10), owner to approve |
| LS-114 | Implemented (P6) | Privacy & parent controls (SCR-21): what is kept and what leaves the device, delete all local data with confirmation, then onboarding. Content owner to approve |
| LS-115 | Implemented (P6) | Help & about (SCR-22): four questions, version from `:app` BuildConfig via `AppInfo`, font licences. Content owner to approve |
| LS-043 | Draft (P6) | Glossary extended with P5/P6 terms; every Kannada value is a draft |
| LS-120 | Implemented (P7) | Kover aggregates every module; `check` fails when view models and `:domain` drop below 80 % line coverage (95 % when introduced). Report: `build/reports/kover/html` |
| LS-121 | Implemented (P7) | Journeys A–G in English and Kannada as Robolectric tests on the real app graph (`app/src/test/.../journeys`); 14 tests; manual gaps listed in `docs/testing/integration-test-plan.md` §2 |
| LS-122 (part) | Trial (P7) | Compose Preview Screenshot Testing in `:app` for Home, Reader, Player, Profile, Language × EN, KN, KN 200 %; images rendered in CI, references await owner approval |
| LS-124 | Reviewed (P7), owner sign-off open | All 22 design screens compared with the app in `docs/design/fidelity-review.md`; four High findings fixed (row titles, list art size, clipped label, brand overlap at 200 %) and two Medium (row eyebrow case, centred onboarding); the remaining Medium items are listed for the owner |
| LS-125 | Implemented (P7) | Hardcoded-values audit in `docs/architecture/hardcoding-audit.md`; four layout counts named; scanner gained text-argument, language-tag, URL and layout-count rules |
| LS-123 | Implemented (P7), device pass open | Automated checks on 12 main screens in EN and KN (`AccessibilityChecksTest`): every tappable node has a name and a 48 dp target. Fixed: search and text fields, "See all" links, story cards and rows, short tabs. Contrast and TalkBack stay manual (`docs/design/accessibility-spec.md` §6) |
| LS-126 | Reviewed (P7) | `docs/security/security-review.md`: backup and device transfer now exclude all app data; no permissions, only `MainActivity` exported; release logging (T-08) still open |
| NFR-12 | Implemented (P7) | Generating screen says the story is simulated on this device, as the Player already did for narration |
| Bottom nav | Wired (P5) | All five tabs navigate; Home bell, avatar, Create and Resume are connected |

Known limitations in P5/P6: Language, Privacy and Help have no design and reuse settings components; narration, downloads and generation are simulated; the reminder is not scheduled (D13); the Profile child card follows D14 (no parent name); bottom-sheet content is not captured by the root screenshot.

P5 and P6 are merged into `development` with the owner's permission; everything still awaits owner testing, and no text is Approved.

## Kannada strings: Draft, needs native review

Mock story and category text in `data/src/main/assets/mock/*_kn.json` (LS-044) is also an agent draft. None is Approved.

Every Kannada string below is a draft written by an agent. None is Approved. Source of truth is the `values-kn/strings.xml` of each module.

| Key | Module | English | Kannada (draft) | Status |
|---|---|---|---|---|
| `action_back` | core:ui | Back | ಹಿಂದೆ | Draft — needs native review |
| `action_continue` | core:ui | Continue | ಮುಂದುವರಿಸಿ | Draft — needs native review |
| `action_skip` | core:ui | Skip | ಬಿಟ್ಟುಬಿಡಿ | Draft — needs native review |
| `action_try_again` | core:ui | Try again | ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಿ | Draft — needs native review |
| `catalog_back` | core:ui | Back | ಹಿಂದೆ | Draft — needs native review |
| `catalog_clear` | core:ui | Clear search | ಹುಡುಕಾಟ ಅಳಿಸಿ | Draft — needs native review |
| `catalog_empty_text` | core:ui | Create your first story to see it here. | ನಿಮ್ಮ ಮೊದಲ ಕಥೆಯನ್ನು ರಚಿಸಿದರೆ ಇಲ್ಲಿ ಕಾಣುತ್ತದೆ. | Draft — needs native review |
| `catalog_empty_title` | core:ui | No stories yet | ಇನ್ನೂ ಕಥೆಗಳಿಲ್ಲ | Draft — needs native review |
| `catalog_eyebrow` | core:ui | 8 MIN · 42% COMPLETE | 8 ನಿಮಿಷ · 42% ಪೂರ್ಣ | Draft — needs native review |
| `catalog_favourite` | core:ui | Add to favourites | ಮೆಚ್ಚಿನವುಗಳಿಗೆ ಸೇರಿಸಿ | Draft — needs native review |
| `catalog_label_name` | core:ui | Child’s name | ಮಗುವಿನ ಹೆಸರು | Draft — needs native review |
| `catalog_meta` | core:ui | Friendship · 8 min | ಸ್ನೇಹ · 8 ನಿಮಿಷ | Draft — needs native review |
| `catalog_note` | core:ui | Stories stay on this device. | ಕಥೆಗಳು ಈ ಸಾಧನದಲ್ಲೇ ಇರುತ್ತವೆ. | Draft — needs native review |
| `catalog_option_one` | core:ui | Calm | ಶಾಂತ | Draft — needs native review |
| `catalog_option_three` | core:ui | Magical | ಮಾಯಾಜಾಲ | Draft — needs native review |
| `catalog_option_two` | core:ui | Funny | ಹಾಸ್ಯ | Draft — needs native review |
| `catalog_placeholder` | core:ui | Type a name | ಹೆಸರು ಟೈಪ್ ಮಾಡಿ | Draft — needs native review |
| `catalog_primary` | core:ui | Continue | ಮುಂದುವರಿಸಿ | Draft — needs native review |
| `catalog_search` | core:ui | Search stories | ಕಥೆಗಳನ್ನು ಹುಡುಕಿ | Draft — needs native review |
| `catalog_secondary` | core:ui | Not now | ಈಗ ಬೇಡ | Draft — needs native review |
| `catalog_section` | core:ui | Continue listening | ಕೇಳುವುದನ್ನು ಮುಂದುವರಿಸಿ | Draft — needs native review |
| `catalog_see_all` | core:ui | See all | ಎಲ್ಲವನ್ನೂ ನೋಡಿ | Draft — needs native review |
| `catalog_specimen` | core:designsystem | Little Stories. Big Imagination. | ಪುಟ್ಟ ಕಥೆಗಳು. ದೊಡ್ಡ ಕಲ್ಪನೆ. | Draft — needs native review |
| `catalog_tab_create` | core:ui | Create | ರಚಿಸಿ | Draft — needs native review |
| `catalog_tab_discover` | core:ui | Discover | ಅನ್ವೇಷಿಸಿ | Draft — needs native review |
| `catalog_tab_home` | core:ui | Home | ಮುಖಪುಟ | Draft — needs native review |
| `catalog_tab_profile` | core:ui | Profile | ಪ್ರೊಫೈಲ್ | Draft — needs native review |
| `catalog_tab_stories` | core:ui | My Stories | ನನ್ನ ಕಥೆಗಳು | Draft — needs native review |
| `catalog_title` | core:ui | The Little Elephant Who Shared | ಹಂಚಿಕೊಂಡ ಪುಟ್ಟ ಆನೆ | Draft — needs native review |
| `catalog_title_long` | core:ui | Aarav and the Magical Cricket Bat of the Moonlit Garden | ಆರವ್ ಮತ್ತು ಚಂದ್ರನ ಬೆಳಕಿನ ತೋಟದ ಮಾಯಾ ಕ್ರಿಕೆಟ್ ಬ್ಯಾಟ್ | Draft — needs native review |
| `catalog_toggle` | core:ui | Bedtime reminder | ಮಲಗುವ ಸಮಯದ ಜ್ಞಾಪನೆ | Draft — needs native review |
| `error_generic` | core:ui | Something went wrong. Please try again. | ಏನೋ ತಪ್ಪಾಗಿದೆ. ದಯವಿಟ್ಟು ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಿ. | Draft — needs native review |
| `home_greeting` | feature:home | Good evening | ಶುಭ ಸಂಜೆ | Draft — needs native review |
| `home_title_for_child` | feature:home | Stories for %1$s | %1$s ಅವರಿಗಾಗಿ ಕಥೆಗಳು | Draft — needs native review |
| `home_title_generic` | feature:home | Stories for you | ನಿಮಗಾಗಿ ಕಥೆಗಳು | Draft — needs native review |
| `interest_adventure` | core:ui | Adventure | ಸಾಹಸ | Draft — needs native review |
| `interest_animals` | core:ui | Animals | ಪ್ರಾಣಿಗಳು | Draft — needs native review |
| `interest_cricket` | core:ui | Cricket | ಕ್ರಿಕೆಟ್ | Draft — needs native review |
| `interest_music` | core:ui | Music | ಸಂಗೀತ | Draft — needs native review |
| `interest_nature` | core:ui | Nature | ಪ್ರಕೃತಿ | Draft — needs native review |
| `interest_space` | core:ui | Space | ಬಾಹ್ಯಾಕಾಶ | Draft — needs native review |
| `meta_minutes` | core:ui | %1$d min | %1$d ನಿಮಿಷ | Draft — needs native review |
| `onboarding_body_1` | feature:onboarding | Turn school days, family moments and tiny acts of kindness into magical adventures. | ಶಾಲೆಯ ದಿನಗಳು, ಕುಟುಂಬದ ಕ್ಷಣಗಳು ಮತ್ತು ಸಣ್ಣ ದಯೆಯ ಕೆಲಸಗಳನ್ನು ಮಾಯಾ ಸಾಹಸಗಳನ್ನಾಗಿ ಮಾಡಿ. | Draft — needs native review |
| `onboarding_body_2` | feature:onboarding | Stories shaped around their name, age and favourite things—with parents always in control. | ಮಗುವಿನ ಹೆಸರು, ವಯಸ್ಸು ಮತ್ತು ಇಷ್ಟಗಳ ಸುತ್ತ ರೂಪುಗೊಂಡ ಕಥೆಗಳು—ಪೋಷಕರ ನಿಯಂತ್ರಣದಲ್ಲಿ. | Draft — needs native review |
| `onboarding_body_3` | feature:onboarding | Gentle narration, offline stories and a sleep timer make bedtime feel wonderfully calm. | ಮೃದುವಾದ ನಿರೂಪಣೆ, ಆಫ್‌ಲೈನ್ ಕಥೆಗಳು ಮತ್ತು ನಿದ್ರಾ ಟೈಮರ್ ಮಲಗುವ ಸಮಯವನ್ನು ಶಾಂತವಾಗಿಸುತ್ತವೆ. | Draft — needs native review |
| `onboarding_cta_get_started` | feature:onboarding | Get Started | ಪ್ರಾರಂಭಿಸಿ | Draft — needs native review |
| `onboarding_eyebrow` | feature:onboarding | Little Stories. Big Imagination. | ಪುಟ್ಟ ಕಥೆಗಳು. ದೊಡ್ಡ ಕಲ್ಪನೆ. | Draft — needs native review |
| `onboarding_pager_description` | feature:onboarding | Slide %1$d of %2$d | ಸ್ಲೈಡ್ %1$d / %2$d | Draft — needs native review |
| `onboarding_title_1` | feature:onboarding | Every Day Has a Story | ಪ್ರತಿ ದಿನವೂ ಒಂದು ಕಥೆ | Draft — needs native review |
| `onboarding_title_2` | feature:onboarding | Your Child, the Hero | ನಿಮ್ಮ ಮಗು, ನಾಯಕ | Draft — needs native review |
| `onboarding_title_3` | feature:onboarding | Listen, Dream, Repeat | ಕೇಳಿ, ಕನಸು ಕಾಣಿ, ಮತ್ತೆ ಕೇಳಿ | Draft — needs native review |
| `setup_age_years[one]` | feature:onboarding | %1$d year | %1$d ವರ್ಷ | Draft — needs native review |
| `setup_age_years[other]` | feature:onboarding | %1$d years | %1$d ವರ್ಷಗಳು | Draft — needs native review |
| `setup_badge` | feature:onboarding | Private & parent-managed | ಖಾಸಗಿ ಮತ್ತು ಪೋಷಕರ ನಿರ್ವಹಣೆ | Draft — needs native review |
| `setup_cta_create` | feature:onboarding | Create profile | ಪ್ರೊಫೈಲ್ ರಚಿಸಿ | Draft — needs native review |
| `setup_cta_create_named` | feature:onboarding | Create %1$s’s profile | %1$s ಅವರ ಪ್ರೊಫೈಲ್ ರಚಿಸಿ | Draft — needs native review |
| `setup_cta_save` | feature:onboarding | Save changes | ಬದಲಾವಣೆಗಳನ್ನು ಉಳಿಸಿ | Draft — needs native review |
| `setup_error_name_empty` | feature:onboarding | Please enter your child’s name. | ದಯವಿಟ್ಟು ನಿಮ್ಮ ಮಗುವಿನ ಹೆಸರನ್ನು ನಮೂದಿಸಿ. | Draft — needs native review |
| `setup_error_name_too_long` | feature:onboarding | Names can be up to %1$d characters. | ಹೆಸರು ಗರಿಷ್ಠ %1$d ಅಕ್ಷರಗಳಷ್ಟಿರಬಹುದು. | Draft — needs native review |
| `setup_label_age` | feature:onboarding | Age | ವಯಸ್ಸು | Draft — needs native review |
| `setup_label_avatar` | feature:onboarding | Choose an avatar | ಅವತಾರ ಆಯ್ಕೆಮಾಡಿ | Draft — needs native review |
| `setup_label_duration` | feature:onboarding | Story duration | ಕಥೆಯ ಅವಧಿ | Draft — needs native review |
| `setup_label_interests` | feature:onboarding | Favourite interests (optional) | ಇಷ್ಟದ ವಿಷಯಗಳು (ಐಚ್ಛಿಕ) | Draft — needs native review |
| `setup_label_language` | feature:onboarding | Language | ಭಾಷೆ | Draft — needs native review |
| `setup_label_name` | feature:onboarding | Child’s name | ಮಗುವಿನ ಹೆಸರು | Draft — needs native review |
| `setup_placeholder_name` | feature:onboarding | First name | ಮೊದಲ ಹೆಸರು | Draft — needs native review |
| `setup_skip` | feature:onboarding | Skip for now | ಈಗ ಬಿಟ್ಟುಬಿಡಿ | Draft — needs native review |
| `setup_subtitle` | feature:onboarding | A few details help us choose safe, age-appropriate adventures. | ಕೆಲವು ವಿವರಗಳು ಸುರಕ್ಷಿತ, ವಯಸ್ಸಿಗೆ ಸೂಕ್ತವಾದ ಸಾಹಸಗಳನ್ನು ಆಯ್ಕೆ ಮಾಡಲು ಸಹಾಯ ಮಾಡುತ್ತವೆ. | Draft — needs native review |
| `setup_title` | feature:onboarding | Let’s make stories feel like home. | ಕಥೆಗಳು ಮನೆಯಂತೆ ಅನಿಸಲಿ. | Draft — needs native review |
| `category_all_description` | feature:discover | Every story in the library, ready for tonight. | ಇಂದು ರಾತ್ರಿಗೆ ಸಿದ್ಧವಾಗಿರುವ ಗ್ರಂಥಾಲಯದ ಎಲ್ಲಾ ಕಥೆಗಳು. | Draft — needs native review |
| `category_all_name` | feature:discover | All stories | ಎಲ್ಲಾ ಕಥೆಗಳು | Draft — needs native review |
| `category_empty_text` | feature:discover | New stories are on their way. | ಹೊಸ ಕಥೆಗಳು ಬರುತ್ತಿವೆ. | Draft — needs native review |
| `category_empty_title` | feature:discover | No stories here yet | ಇಲ್ಲಿ ಇನ್ನೂ ಕಥೆಗಳಿಲ್ಲ | Draft — needs native review |
| `category_heading` | feature:discover | Stories for curious minds | ಕುತೂಹಲದ ಮನಸ್ಸುಗಳಿಗೆ ಕಥೆಗಳು | Draft — needs native review |
| `category_sort_label` | feature:discover | Sort stories | ಕಥೆಗಳನ್ನು ವಿಂಗಡಿಸಿ | Draft — needs native review |
| `category_sort_newest` | feature:discover | Newest | ಹೊಸದು ಮೊದಲು | Draft — needs native review |
| `category_sort_recommended` | feature:discover | Recommended | ಶಿಫಾರಸು ಮಾಡಿದ್ದು | Draft — needs native review |
| `category_sort_shortest` | feature:discover | Shortest first | ಚಿಕ್ಕದು ಮೊದಲು | Draft — needs native review |
| `category_story_count[one]` | feature:discover | %1$d STORY | %1$d ಕಥೆ | Draft — needs native review |
| `category_story_count[other]` | feature:discover | %1$d STORIES | %1$d ಕಥೆಗಳು | Draft — needs native review |
| `detail_download_add` | feature:discover | Download %1$s | %1$s ಡೌನ್‌ಲೋಡ್ ಮಾಡಿ | Draft — needs native review |
| `detail_download_remove` | feature:discover | Remove download of %1$s | %1$s ಡೌನ್‌ಲೋಡ್ ತೆಗೆದುಹಾಕಿ | Draft — needs native review |
| `detail_lesson_title` | feature:discover | A little lesson | ಒಂದು ಪುಟ್ಟ ಪಾಠ | Draft — needs native review |
| `detail_listen` | feature:discover | Listen to Story | ಕಥೆ ಕೇಳಿ | Draft — needs native review |
| `detail_meta` | feature:discover | Ages %1$d–%2$d · %3$d min · %4$s | %1$d–%2$d ವರ್ಷ · %3$d ನಿಮಿಷ · %4$s | Draft — needs native review |
| `detail_narration_available` | feature:discover | Available | ಲಭ್ಯ | Draft — needs native review |
| `detail_narration_text` | feature:discover | Warm, expressive voice · %1$s | ಬೆಚ್ಚಗಿನ, ಭಾವಪೂರ್ಣ ಧ್ವನಿ · %1$s | Draft — needs native review |
| `detail_narration_title` | feature:discover | Narrated story | ನಿರೂಪಿತ ಕಥೆ | Draft — needs native review |
| `detail_not_found_text` | feature:discover | This story is no longer available. | ಈ ಕಥೆ ಇನ್ನು ಲಭ್ಯವಿಲ್ಲ. | Draft — needs native review |
| `detail_not_found_title` | feature:discover | Story not found | ಕಥೆ ಸಿಗಲಿಲ್ಲ | Draft — needs native review |
| `detail_read` | feature:discover | Read Story | ಕಥೆ ಓದಿ | Draft — needs native review |
| `discover_bedtime` | feature:discover | Bedtime favourites | ಮಲಗುವ ಸಮಯದ ಮೆಚ್ಚಿನವು | Draft — needs native review |
| `discover_empty_action` | feature:discover | Clear search | ಹುಡುಕಾಟ ಅಳಿಸಿ | Draft — needs native review |
| `discover_empty_text` | feature:discover | Try a different title or explore one of our story collections. | ಬೇರೆ ಶೀರ್ಷಿಕೆ ಪ್ರಯತ್ನಿಸಿ ಅಥವಾ ನಮ್ಮ ಕಥಾ ಸಂಗ್ರಹಗಳನ್ನು ಅನ್ವೇಷಿಸಿ. | Draft — needs native review |
| `discover_empty_title` | feature:discover | No stories found | ಯಾವುದೇ ಕಥೆ ಸಿಗಲಿಲ್ಲ | Draft — needs native review |
| `discover_featured_cta` | feature:discover | Listen now | ಈಗ ಕೇಳಿ | Draft — needs native review |
| `discover_featured_kicker` | feature:discover | FEATURED THIS WEEK | ಈ ವಾರದ ವಿಶೇಷ | Draft — needs native review |
| `discover_filter_all` | feature:discover | All | ಎಲ್ಲವೂ | Draft — needs native review |
| `discover_notifications` | feature:discover | Notifications | ಅಧಿಸೂಚನೆಗಳು | Draft — needs native review |
| `discover_popular` | feature:discover | Popular right now | ಈಗ ಜನಪ್ರಿಯ | Draft — needs native review |
| `discover_results` | feature:discover | Results for “%1$s” | “%1$s” ಗಾಗಿ ಫಲಿತಾಂಶಗಳು | Draft — needs native review |
| `discover_search_clear` | feature:discover | Clear search | ಹುಡುಕಾಟ ಅಳಿಸಿ | Draft — needs native review |
| `discover_search_placeholder` | feature:discover | Search stories, themes, or characters | ಕಥೆಗಳು, ವಿಷಯಗಳು ಅಥವಾ ಪಾತ್ರಗಳನ್ನು ಹುಡುಕಿ | Draft — needs native review |
| `discover_title` | feature:discover | Discover | ಅನ್ವೇಷಿಸಿ | Draft — needs native review |
| `discover_view_all` | feature:discover | View all | ಎಲ್ಲವನ್ನೂ ನೋಡಿ | Draft — needs native review |
| `home_continue_eyebrow` | feature:home | %1$d MIN · %2$d%% COMPLETE | %1$d ನಿಮಿಷ · %2$d%% ಪೂರ್ಣ | Draft — needs native review |
| `home_continue_resume` | feature:home | Resume story | ಕಥೆ ಮುಂದುವರಿಸಿ | Draft — needs native review |
| `home_continue_title` | feature:home | Continue listening | ಕೇಳುವುದನ್ನು ಮುಂದುವರಿಸಿ | Draft — needs native review |
| `home_culture_body` | feature:home | Timeless wisdom, family traditions and joyful festivals. | ಕಾಲಾತೀತ ಜ್ಞಾನ, ಕುಟುಂಬ ಸಂಪ್ರದಾಯಗಳು ಮತ್ತು ಸಂತಸದ ಹಬ್ಬಗಳು. | Draft — needs native review |
| `home_culture_kicker` | feature:home | ROOTED IN WONDER | ಅದ್ಭುತದ ಬೇರುಗಳು | Draft — needs native review |
| `home_culture_link` | feature:home | Explore Indian stories | ಭಾರತೀಯ ಕಥೆಗಳನ್ನು ಅನ್ವೇಷಿಸಿ | Draft — needs native review |
| `home_culture_title` | feature:home | Stories from every corner of India | ಭಾರತದ ಪ್ರತಿ ಮೂಲೆಯ ಕಥೆಗಳು | Draft — needs native review |
| `home_explore_title` | feature:home | Explore stories | ಕಥೆಗಳನ್ನು ಅನ್ವೇಷಿಸಿ | Draft — needs native review |
| `home_hero_body` | feature:home | Turn today’s little moments into magical adventures. | ಇಂದಿನ ಪುಟ್ಟ ಕ್ಷಣಗಳನ್ನು ಮಾಯಾ ಸಾಹಸಗಳನ್ನಾಗಿ ಮಾಡಿ. | Draft — needs native review |
| `home_hero_cta` | feature:home | Create a Story | ಕಥೆ ರಚಿಸಿ | Draft — needs native review |
| `home_hero_kicker` | feature:home | TONIGHT’S ADVENTURE | ಇಂದು ರಾತ್ರಿಯ ಸಾಹಸ | Draft — needs native review |
| `home_hero_title` | feature:home | Tonight, anything is possible. | ಇಂದು ರಾತ್ರಿ ಏನು ಬೇಕಾದರೂ ಸಾಧ್ಯ. | Draft — needs native review |
| `home_made_for_child` | feature:home | Made for %1$s | %1$s ಅವರಿಗಾಗಿ ಮಾಡಿದ್ದು | Draft — needs native review |
| `home_made_for_generic` | feature:home | Picked for you | ನಿಮಗಾಗಿ ಆಯ್ದವು | Draft — needs native review |
| `home_notifications` | feature:home | Notifications | ಅಧಿಸೂಚನೆಗಳು | Draft — needs native review |
| `home_profiles` | feature:home | Child profiles | ಮಕ್ಕಳ ಪ್ರೊಫೈಲ್‌ಗಳು | Draft — needs native review |
| `home_see_all` | feature:home | See all | ಎಲ್ಲವನ್ನೂ ನೋಡಿ | Draft — needs native review |
| `nav_create` | app | Create | ರಚಿಸಿ | Draft — needs native review |
| `nav_discover` | app | Discover | ಅನ್ವೇಷಿಸಿ | Draft — needs native review |
| `nav_home` | app | Home | ಮುಖಪುಟ | Draft — needs native review |
| `nav_profile` | app | Profile | ಪ್ರೊಫೈಲ್ | Draft — needs native review |
| `nav_stories` | app | My Stories | ನನ್ನ ಕಥೆಗಳು | Draft — needs native review |
| `story_card_meta` | core:ui | %1$s · %2$d min | %1$s · %2$d ನಿಮಿಷ | Draft — needs native review |
| `story_favourite_add` | core:ui | Add %1$s to favourites | %1$s ಅನ್ನು ಮೆಚ್ಚಿನವುಗಳಿಗೆ ಸೇರಿಸಿ | Draft — needs native review |
| `story_favourite_remove` | core:ui | Remove %1$s from favourites | %1$s ಅನ್ನು ಮೆಚ್ಚಿನವುಗಳಿಂದ ತೆಗೆದುಹಾಕಿ | Draft — needs native review |
| `story_list_meta` | core:ui | Ages %1$d–%2$d · %3$d min | %1$d–%2$d ವರ್ಷದವರಿಗೆ · %3$d ನಿಮಿಷ | Draft — needs native review |
| `story_offline` | core:ui | Available offline | ಆಫ್‌ಲೈನ್‌ನಲ್ಲಿ ಲಭ್ಯ | Draft — needs native review |

### P5 strings (Create, Reader, Player, My Stories, Settings)

Generated from the module XML files; story text in `generated_kn.json` and `notifications_kn.json` is also an agent draft.

| Key | Module | English | Kannada (draft) | Status |
|---|---|---|---|---|
| `create_counter` | feature:create | %1$d/%2$d | %1$d/%2$d | Draft — needs native review |
| `create_duration_minutes[one]` | feature:create | %1$d minute | %1$d ನಿಮಿಷ | Draft — needs native review |
| `create_duration_minutes[other]` | feature:create | %1$d minutes | %1$d ನಿಮಿಷಗಳು | Draft — needs native review |
| `create_duration_title` | feature:create | Duration | ಅವಧಿ | Draft — needs native review |
| `create_error_empty` | feature:create | Tell us one little moment to begin the story. | ಕಥೆ ಆರಂಭಿಸಲು ಒಂದು ಪುಟ್ಟ ಕ್ಷಣವನ್ನು ಹೇಳಿ. | Draft — needs native review |
| `create_fill_family` | feature:create | %1$s had a family adventure. | %1$s ಕುಟುಂಬದೊಂದಿಗೆ ಒಂದು ಸಾಹಸ ಮಾಡಿದರು. | Draft — needs native review |
| `create_fill_funny` | feature:create | %1$s saw something funny happen today. | %1$s ಇಂದು ಏನೋ ತಮಾಷೆಯನ್ನು ನೋಡಿದರು. | Draft — needs native review |
| `create_fill_kindness` | feature:create | %1$s did an act of kindness today. | %1$s ಇಂದು ಒಂದು ದಯೆಯ ಕೆಲಸ ಮಾಡಿದರು. | Draft — needs native review |
| `create_fill_learned` | feature:create | %1$s learned something new today. | %1$s ಇಂದು ಹೊಸದನ್ನು ಕಲಿತರು. | Draft — needs native review |
| `create_fill_school` | feature:create | %1$s helped a friend at school and played cricket in the evening. | %1$s ಶಾಲೆಯಲ್ಲಿ ಗೆಳೆಯನಿಗೆ ಸಹಾಯ ಮಾಡಿ, ಸಂಜೆ ಕ್ರಿಕೆಟ್ ಆಡಿದರು. | Draft — needs native review |
| `create_hero_change` | feature:create | Choose the hero | ನಾಯಕನನ್ನು ಆಯ್ಕೆಮಾಡಿ | Draft — needs native review |
| `create_hero_eyebrow` | feature:create | STORY HERO | ಕಥೆಯ ನಾಯಕ | Draft — needs native review |
| `create_hero_fallback` | feature:create | My little one | ನನ್ನ ಪುಟ್ಟ ಮಗು | Draft — needs native review |
| `create_hero_name_age[one]` | feature:create | %1$s, %2$d year | %1$s, %2$d ವರ್ಷ | Draft — needs native review |
| `create_hero_name_age[other]` | feature:create | %1$s, %2$d years | %1$s, %2$d ವರ್ಷಗಳು | Draft — needs native review |
| `create_intro_body` | feature:create | Tell us about your child’s day. We’ll turn the little moments into a magical adventure. | ನಿಮ್ಮ ಮಗುವಿನ ದಿನದ ಬಗ್ಗೆ ಹೇಳಿ. ಪುಟ್ಟ ಕ್ಷಣಗಳನ್ನು ನಾವು ಮಾಯಾ ಸಾಹಸವನ್ನಾಗಿ ಮಾಡುತ್ತೇವೆ. | Draft — needs native review |
| `create_intro_title` | feature:create | Let’s turn today into a story. | ಇಂದಿನ ದಿನವನ್ನು ಕಥೆಯನ್ನಾಗಿಸೋಣ. | Draft — needs native review |
| `create_language_title` | feature:create | Story language | ಕಥೆಯ ಭಾಷೆ | Draft — needs native review |
| `create_mood_adventurous` | feature:create | Adventurous | ಸಾಹಸಮಯ | Draft — needs native review |
| `create_mood_calm` | feature:create | Calm | ಶಾಂತ | Draft — needs native review |
| `create_mood_funny` | feature:create | Funny | ಹಾಸ್ಯ | Draft — needs native review |
| `create_mood_magical` | feature:create | Magical | ಮಾಯಾಜಾಲ | Draft — needs native review |
| `create_mood_title` | feature:create | Mood | ಭಾವ | Draft — needs native review |
| `create_more_options` | feature:create | More options | ಇನ್ನಷ್ಟು ಆಯ್ಕೆಗಳು | Draft — needs native review |
| `create_more_options_collapsed` | feature:create | Collapsed | ಮುಚ್ಚಿದೆ | Draft — needs native review |
| `create_more_options_expanded` | feature:create | Expanded | ತೆರೆದಿದೆ | Draft — needs native review |
| `create_parent_guided` | feature:create | Parent guided | ಪೋಷಕರ ಮಾರ್ಗದರ್ಶನ | Draft — needs native review |
| `create_prompt_label` | feature:create | What happened today? | ಇಂದು ಏನಾಯಿತು? | Draft — needs native review |
| `create_prompt_placeholder` | feature:create | We helped a friend at school and played cricket in the evening… | ಶಾಲೆಯಲ್ಲಿ ಗೆಳೆಯನಿಗೆ ಸಹಾಯ ಮಾಡಿ, ಸಂಜೆ ಕ್ರಿಕೆಟ್ ಆಡಿದೆವು… | Draft — needs native review |
| `create_prompt_placeholder_named` | feature:create | %1$s helped a friend at school and played cricket in the evening… | %1$s ಶಾಲೆಯಲ್ಲಿ ಗೆಳೆಯನಿಗೆ ಸಹಾಯ ಮಾಡಿ, ಸಂಜೆ ಕ್ರಿಕೆಟ್ ಆಡಿದರು… | Draft — needs native review |
| `create_safety_note` | feature:create | Made safely, just for your family. Your story details stay private and are never shared publicly. | ನಿಮ್ಮ ಕುಟುಂಬಕ್ಕಾಗಿಯೇ ಸುರಕ್ಷಿತವಾಗಿ ರಚಿಸಲಾಗಿದೆ. ನಿಮ್ಮ ಕಥೆಯ ವಿವರಗಳು ಖಾಸಗಿಯಾಗಿರುತ್ತವೆ, ಎಂದಿಗೂ ಸಾರ್ವಜನಿಕವಾಗಿ ಹಂಚಿಕೊಳ್ಳುವುದಿಲ್ಲ. | Draft — needs native review |
| `create_submit` | feature:create | Create My Story | ನನ್ನ ಕಥೆ ರಚಿಸಿ | Draft — needs native review |
| `create_suggestion_family` | feature:create | A family adventure | ಕುಟುಂಬದ ಸಾಹಸ | Draft — needs native review |
| `create_suggestion_funny` | feature:create | Something funny happened | ಏನೋ ತಮಾಷೆ ನಡೆಯಿತು | Draft — needs native review |
| `create_suggestion_kindness` | feature:create | An act of kindness | ಒಂದು ದಯೆಯ ಕೆಲಸ | Draft — needs native review |
| `create_suggestion_learned` | feature:create | I learned something new | ಹೊಸದನ್ನು ಕಲಿತೆ | Draft — needs native review |
| `create_suggestion_school` | feature:create | A day at school | ಶಾಲೆಯ ಒಂದು ದಿನ | Draft — needs native review |
| `create_theme_adventure` | feature:create | Adventure | ಸಾಹಸ | Draft — needs native review |
| `create_theme_animals` | feature:create | Animals | ಪ್ರಾಣಿಗಳು | Draft — needs native review |
| `create_theme_bedtime` | feature:create | Bedtime | ಮಲಗುವ ಸಮಯ | Draft — needs native review |
| `create_theme_fantasy` | feature:create | Fantasy | ಕಲ್ಪನಾಲೋಕ | Draft — needs native review |
| `create_theme_friendship` | feature:create | Friendship | ಸ್ನೇಹ | Draft — needs native review |
| `create_theme_indian_folklore` | feature:create | Indian folklore | ಭಾರತೀಯ ಜಾನಪದ | Draft — needs native review |
| `create_theme_learning` | feature:create | Learning | ಕಲಿಕೆ | Draft — needs native review |
| `create_theme_title` | feature:create | Story theme | ಕಥೆಯ ವಿಷಯ | Draft — needs native review |
| `create_title` | feature:create | Create a Story | ಕಥೆ ರಚಿಸಿ | Draft — needs native review |
| `generating_back_to_idea` | feature:create | Back to your idea | ನಿಮ್ಮ ಆಲೋಚನೆಗೆ ಹಿಂತಿರುಗಿ | Draft — needs native review |
| `generating_body` | feature:create | We’re weaving today’s moments into something wonderful. | ಇಂದಿನ ಕ್ಷಣಗಳನ್ನು ಅದ್ಭುತವಾದದ್ದಾಗಿ ಹೆಣೆಯುತ್ತಿದ್ದೇವೆ. | Draft — needs native review |
| `generating_close` | feature:create | Stop creating the story | ಕಥೆ ರಚನೆ ನಿಲ್ಲಿಸಿ | Draft — needs native review |
| `generating_eyebrow` | feature:create | A STORY FOR YOU | ನಿಮಗಾಗಿ ಒಂದು ಕಥೆ | Draft — needs native review |
| `generating_eyebrow_named` | feature:create | A STORY FOR %1$s | %1$s ಅವರಿಗಾಗಿ ಒಂದು ಕಥೆ | Draft — needs native review |
| `generating_footnote` | feature:create | This usually takes less than a minute. | ಇದಕ್ಕೆ ಸಾಮಾನ್ಯವಾಗಿ ಒಂದು ನಿಮಿಷಕ್ಕಿಂತ ಕಡಿಮೆ ಸಮಯ ಬೇಕು. | Draft — needs native review |
| `generating_paused_body` | feature:create | Your idea is still here. Continue whenever you’re ready. | ನಿಮ್ಮ ಆಲೋಚನೆ ಇಲ್ಲೇ ಇದೆ. ಸಿದ್ಧವಾದಾಗ ಮುಂದುವರಿಸಿ. | Draft — needs native review |
| `generating_paused_title` | feature:create | Story creation paused | ಕಥೆ ರಚನೆ ನಿಲ್ಲಿಸಲಾಗಿದೆ | Draft — needs native review |
| `generating_percent` | feature:create | %1$d%% | %1$d%% | Draft — needs native review |
| `generating_stage_active` | feature:create | In progress | ನಡೆಯುತ್ತಿದೆ | Draft — needs native review |
| `generating_stage_done` | feature:create | Done | ಮುಗಿದಿದೆ | Draft — needs native review |
| `generating_stage_imagining` | feature:create | Imagining the adventure | ಸಾಹಸವನ್ನು ಕಲ್ಪಿಸಿಕೊಳ್ಳುತ್ತಿದ್ದೇವೆ | Draft — needs native review |
| `generating_stage_narration` | feature:create | Preparing the narration | ನಿರೂಪಣೆ ಸಿದ್ಧಪಡಿಸುತ್ತಿದ್ದೇವೆ | Draft — needs native review |
| `generating_stage_pending` | feature:create | Not started | ಇನ್ನೂ ಆರಂಭವಾಗಿಲ್ಲ | Draft — needs native review |
| `generating_stage_writing` | feature:create | Writing the story | ಕಥೆ ಬರೆಯುತ್ತಿದ್ದೇವೆ | Draft — needs native review |
| `generating_title` | feature:create | Creating your little adventure… | ನಿಮ್ಮ ಪುಟ್ಟ ಸಾಹಸ ರೂಪುಗೊಳ್ಳುತ್ತಿದೆ… | Draft — needs native review |
| `generating_try_again` | feature:create | Try again | ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಿ | Draft — needs native review |
| `ready_download_add` | feature:create | Download %1$s | %1$s ಡೌನ್‌ಲೋಡ್ ಮಾಡಿ | Draft — needs native review |
| `ready_download_remove` | feature:create | Remove download of %1$s | %1$s ಡೌನ್‌ಲೋಡ್ ತೆಗೆದುಹಾಕಿ | Draft — needs native review |
| `ready_eyebrow` | feature:create | A MAGICAL ADVENTURE | ಒಂದು ಮಾಯಾ ಸಾಹಸ | Draft — needs native review |
| `ready_listen` | feature:create | Listen Now | ಈಗ ಕೇಳಿ | Draft — needs native review |
| `ready_made_for` | feature:create | Made for %1$s | %1$s ಅವರಿಗಾಗಿ ಮಾಡಿದ್ದು | Draft — needs native review |
| `ready_meta` | feature:create | %1$s · %2$d min · Ages %3$d–%4$d | %1$s · %2$d ನಿಮಿಷ · %3$d–%4$d ವರ್ಷ | Draft — needs native review |
| `ready_not_found_text` | feature:create | This story is no longer available. | ಈ ಕಥೆ ಇನ್ನು ಲಭ್ಯವಿಲ್ಲ. | Draft — needs native review |
| `ready_not_found_title` | feature:create | Story not found | ಕಥೆ ಸಿಗಲಿಲ್ಲ | Draft — needs native review |
| `ready_peek_title` | feature:create | A little peek inside | ಒಳಗಿನ ಒಂದು ಪುಟ್ಟ ನೋಟ | Draft — needs native review |
| `ready_read` | feature:create | Read Story | ಕಥೆ ಓದಿ | Draft — needs native review |
| `ready_ribbon` | feature:create | Your story is ready | ನಿಮ್ಮ ಕಥೆ ಸಿದ್ಧವಾಗಿದೆ | Draft — needs native review |
| `ready_saved_body` | feature:create | Find it anytime in Created by Me | ನಾನು ರಚಿಸಿದವು ವಿಭಾಗದಲ್ಲಿ ಯಾವಾಗ ಬೇಕಾದರೂ ಹುಡುಕಿ | Draft — needs native review |
| `ready_saved_title` | feature:create | Saved to My Stories | ನನ್ನ ಕಥೆಗಳಲ್ಲಿ ಉಳಿಸಲಾಗಿದೆ | Draft — needs native review |
| `player_download` | feature:reader | Download | ಡೌನ್‌ಲೋಡ್ | Draft — needs native review |
| `player_downloaded` | feature:reader | Downloaded | ಡೌನ್‌ಲೋಡ್ ಆಗಿದೆ | Draft — needs native review |
| `player_favourite` | feature:reader | Favourite | ಮೆಚ್ಚಿನದು | Draft — needs native review |
| `player_forward[one]` | feature:reader | Forward %1$d second | %1$d ಸೆಕೆಂಡ್ ಮುಂದೆ | Draft — needs native review |
| `player_forward[other]` | feature:reader | Forward %1$d seconds | %1$d ಸೆಕೆಂಡುಗಳು ಮುಂದೆ | Draft — needs native review |
| `player_language_narration` | feature:reader | %1$s · Simulated narration | %1$s · ಅನುಕರಿಸಿದ ನಿರೂಪಣೆ | Draft — needs native review |
| `player_loved` | feature:reader | Loved | ಇಷ್ಟವಾಯಿತು | Draft — needs native review |
| `player_next_chapter` | feature:reader | Next chapter | ಮುಂದಿನ ಅಧ್ಯಾಯ | Draft — needs native review |
| `player_now_playing` | feature:reader | NOW PLAYING | ಈಗ ಕೇಳುತ್ತಿರುವುದು | Draft — needs native review |
| `player_now_playing_for` | feature:reader | NOW PLAYING FOR %1$s | %1$s ಅವರಿಗಾಗಿ ಈಗ ಕೇಳುತ್ತಿರುವುದು | Draft — needs native review |
| `player_pause` | feature:reader | Pause | ವಿರಾಮ | Draft — needs native review |
| `player_play` | feature:reader | Play | ಕೇಳಿ | Draft — needs native review |
| `player_previous_chapter` | feature:reader | Previous chapter | ಹಿಂದಿನ ಅಧ್ಯಾಯ | Draft — needs native review |
| `player_rewind[one]` | feature:reader | Back %1$d second | %1$d ಸೆಕೆಂಡ್ ಹಿಂದೆ | Draft — needs native review |
| `player_rewind[other]` | feature:reader | Back %1$d seconds | %1$d ಸೆಕೆಂಡುಗಳು ಹಿಂದೆ | Draft — needs native review |
| `player_seek` | feature:reader | Story position | ಕಥೆಯ ಸ್ಥಾನ | Draft — needs native review |
| `player_sleep_body` | feature:reader | Audio will gently stop when the timer ends. | ಟೈಮರ್ ಮುಗಿದಾಗ ಧ್ವನಿ ಮೆಲ್ಲನೆ ನಿಲ್ಲುತ್ತದೆ. | Draft — needs native review |
| `player_sleep_cancel` | feature:reader | Cancel | ರದ್ದುಮಾಡಿ | Draft — needs native review |
| `player_sleep_end_of_story` | feature:reader | End of story | ಕಥೆಯ ಕೊನೆಯಲ್ಲಿ | Draft — needs native review |
| `player_sleep_minutes[one]` | feature:reader | %1$d minute | %1$d ನಿಮಿಷ | Draft — needs native review |
| `player_sleep_minutes[other]` | feature:reader | %1$d minutes | %1$d ನಿಮಿಷಗಳು | Draft — needs native review |
| `player_sleep_timer` | feature:reader | Sleep timer | ನಿದ್ರಾ ಟೈಮರ್ | Draft — needs native review |
| `player_sleep_title` | feature:reader | Sleep timer | ನಿದ್ರಾ ಟೈಮರ್ | Draft — needs native review |
| `player_speed` | feature:reader | Speed | ವೇಗ | Draft — needs native review |
| `player_speed_description` | feature:reader | Playback speed %1$s times | ಕೇಳುವ ವೇಗ %1$s ಪಟ್ಟು | Draft — needs native review |
| `player_speed_value` | feature:reader | %1$s× | %1$s× | Draft — needs native review |
| `player_state_off` | feature:reader | Off | ಆಫ್ | Draft — needs native review |
| `player_state_on` | feature:reader | On | ಆನ್ | Draft — needs native review |
| `player_story_text` | feature:reader | Story text | ಕಥೆಯ ಪಠ್ಯ | Draft — needs native review |
| `player_time` | feature:reader | %1$s:%2$02d | %1$s:%2$02d | Draft — needs native review |
| `player_volume` | feature:reader | Volume | ಧ್ವನಿ ಮಟ್ಟ | Draft — needs native review |
| `reader_next` | feature:reader | Next | ಮುಂದಿನದು | Draft — needs native review |
| `reader_not_found_text` | feature:reader | This story is no longer available. | ಈ ಕಥೆ ಇನ್ನು ಲಭ್ಯವಿಲ್ಲ. | Draft — needs native review |
| `reader_not_found_title` | feature:reader | Story not found | ಕಥೆ ಸಿಗಲಿಲ್ಲ | Draft — needs native review |
| `reader_page_of` | feature:reader | %1$d of %2$d | %2$d ರಲ್ಲಿ %1$d | Draft — needs native review |
| `reader_play` | feature:reader | Play narration | ನಿರೂಪಣೆ ಕೇಳಿ | Draft — needs native review |
| `reader_previous` | feature:reader | Previous | ಹಿಂದಿನದು | Draft — needs native review |
| `reader_text_larger` | feature:reader | Larger text | ದೊಡ್ಡ ಅಕ್ಷರ | Draft — needs native review |
| `reader_text_larger_label` | feature:reader | A+ | ಅ+ | Draft — needs native review |
| `reader_text_smaller` | feature:reader | Smaller text | ಚಿಕ್ಕ ಅಕ್ಷರ | Draft — needs native review |
| `reader_text_smaller_label` | feature:reader | A− | ಅ− | Draft — needs native review |
| `reader_title` | feature:reader | Reading | ಓದುತ್ತಿದ್ದೇವೆ | Draft — needs native review |
| `downloads_available` | feature:library | AVAILABLE OFFLINE | ಆಫ್‌ಲೈನ್‌ನಲ್ಲಿ ಲಭ್ಯ | Draft — needs native review |
| `downloads_empty_action` | feature:library | Find a story | ಒಂದು ಕಥೆ ಹುಡುಕಿ | Draft — needs native review |
| `downloads_empty_text` | feature:library | Save a few favourites for journeys, power cuts and cosy nights away. | ಪ್ರಯಾಣ, ವಿದ್ಯುತ್ ಕಡಿತ ಮತ್ತು ಮನೆಯಿಂದ ದೂರದ ಬೆಚ್ಚಗಿನ ರಾತ್ರಿಗಳಿಗಾಗಿ ಕೆಲವು ಮೆಚ್ಚಿನ ಕಥೆಗಳನ್ನು ಉಳಿಸಿ. | Draft — needs native review |
| `downloads_empty_title` | feature:library | No downloads yet | ಇನ್ನೂ ಡೌನ್‌ಲೋಡ್‌ಗಳಿಲ್ಲ | Draft — needs native review |
| `downloads_quota` | feature:library | %1$s GB available for stories | ಕಥೆಗಳಿಗೆ %1$s GB ಲಭ್ಯ | Draft — needs native review |
| `downloads_remove` | feature:library | Remove download of %1$s | %1$s ಡೌನ್‌ಲೋಡ್ ತೆಗೆದುಹಾಕಿ | Draft — needs native review |
| `downloads_size` | feature:library | %1$s MB · Audio & story | %1$s MB · ಧ್ವನಿ ಮತ್ತು ಕಥೆ | Draft — needs native review |
| `downloads_tip` | feature:library | Offline means worry-free. Downloaded stories work without internet and stay private on this device. | ಆಫ್‌ಲೈನ್ ಎಂದರೆ ಚಿಂತೆಯಿಲ್ಲ. ಡೌನ್‌ಲೋಡ್ ಮಾಡಿದ ಕಥೆಗಳು ಇಂಟರ್ನೆಟ್ ಇಲ್ಲದೆ ಕೆಲಸ ಮಾಡುತ್ತವೆ ಮತ್ತು ಈ ಸಾಧನದಲ್ಲೇ ಖಾಸಗಿಯಾಗಿರುತ್ತವೆ. | Draft — needs native review |
| `downloads_title` | feature:library | Offline Downloads | ಆಫ್‌ಲೈನ್ ಡೌನ್‌ಲೋಡ್‌ಗಳು | Draft — needs native review |
| `downloads_used` | feature:library | %1$s MB used | %1$s MB ಬಳಸಲಾಗಿದೆ | Draft — needs native review |
| `stories_collection` | feature:library | Your collection | ನಿಮ್ಮ ಸಂಗ್ರಹ | Draft — needs native review |
| `stories_empty_action` | feature:library | Discover stories | ಕಥೆಗಳನ್ನು ಅನ್ವೇಷಿಸಿ | Draft — needs native review |
| `stories_empty_downloaded_text` | feature:library | Download a story and bedtime can travel anywhere. | ಒಂದು ಕಥೆಯನ್ನು ಡೌನ್‌ಲೋಡ್ ಮಾಡಿ, ಮಲಗುವ ಸಮಯ ಎಲ್ಲಿ ಬೇಕಾದರೂ ಜೊತೆಗಿರುತ್ತದೆ. | Draft — needs native review |
| `stories_empty_downloaded_title` | feature:library | Ready for offline adventures? | ಆಫ್‌ಲೈನ್ ಸಾಹಸಗಳಿಗೆ ಸಿದ್ಧವೇ? | Draft — needs native review |
| `stories_empty_text` | feature:library | Tap the heart on a story you love to keep it close. | ನಿಮಗೆ ಇಷ್ಟವಾದ ಕಥೆಯ ಹೃದಯವನ್ನು ಒತ್ತಿ, ಅದನ್ನು ಹತ್ತಿರ ಇಟ್ಟುಕೊಳ್ಳಿ. | Draft — needs native review |
| `stories_empty_title` | feature:library | No stories here yet | ಇಲ್ಲಿ ಇನ್ನೂ ಕಥೆಗಳಿಲ್ಲ | Draft — needs native review |
| `stories_offline` | feature:library | Available offline | ಆಫ್‌ಲೈನ್‌ನಲ್ಲಿ ಲಭ್ಯ | Draft — needs native review |
| `stories_open_downloads` | feature:library | Offline downloads | ಆಫ್‌ಲೈನ್ ಡೌನ್‌ಲೋಡ್‌ಗಳು | Draft — needs native review |
| `stories_play` | feature:library | Play %1$s | %1$s ಕೇಳಿ | Draft — needs native review |
| `stories_recently_played` | feature:library | Recently played | ಇತ್ತೀಚೆಗೆ ಕೇಳಿದ್ದು | Draft — needs native review |
| `stories_remaining` | feature:library | %1$s:%2$02d remaining | %1$s:%2$02d ಉಳಿದಿದೆ | Draft — needs native review |
| `stories_row_meta` | feature:library | %1$d min · %2$d–%3$d years | %1$d ನಿಮಿಷ · %2$d–%3$d ವರ್ಷ | Draft — needs native review |
| `stories_search` | feature:library | Search your stories | ನಿಮ್ಮ ಕಥೆಗಳನ್ನು ಹುಡುಕಿ | Draft — needs native review |
| `stories_search_clear` | feature:library | Clear search | ಹುಡುಕಾಟ ಅಳಿಸಿ | Draft — needs native review |
| `stories_tab_all` | feature:library | All | ಎಲ್ಲವೂ | Draft — needs native review |
| `stories_tab_created` | feature:library | Created by Me | ನಾನು ರಚಿಸಿದವು | Draft — needs native review |
| `stories_tab_downloaded` | feature:library | Downloaded | ಡೌನ್‌ಲೋಡ್ ಆದವು | Draft — needs native review |
| `stories_tab_favourites` | feature:library | Favourites | ಮೆಚ್ಚಿನವು | Draft — needs native review |
| `stories_title` | feature:library | My Stories | ನನ್ನ ಕಥೆಗಳು | Draft — needs native review |
| `children_active` | feature:settings | Active | ಸಕ್ರಿಯ | Draft — needs native review |
| `children_add` | feature:settings | Add child | ಮಗುವನ್ನು ಸೇರಿಸಿ | Draft — needs native review |
| `children_add_another` | feature:settings | Add another child | ಇನ್ನೊಂದು ಮಗುವನ್ನು ಸೇರಿಸಿ | Draft — needs native review |
| `children_add_another_body` | feature:settings | Personalise stories for every little listener | ಪ್ರತಿ ಪುಟ್ಟ ಕೇಳುಗರಿಗೂ ಕಥೆಗಳನ್ನು ವೈಯಕ್ತೀಕರಿಸಿ | Draft — needs native review |
| `children_age_language[one]` | feature:settings | %1$d year · %2$s | %1$d ವರ್ಷ · %2$s | Draft — needs native review |
| `children_age_language[other]` | feature:settings | %1$d years · %2$s | %1$d ವರ್ಷಗಳು · %2$s | Draft — needs native review |
| `children_banner` | feature:settings | Private family profiles. Only you can see and manage your children’s details. | ಖಾಸಗಿ ಕುಟುಂಬ ಪ್ರೊಫೈಲ್‌ಗಳು. ನಿಮ್ಮ ಮಕ್ಕಳ ವಿವರಗಳನ್ನು ನೀವು ಮಾತ್ರ ನೋಡಬಹುದು ಮತ್ತು ನಿರ್ವಹಿಸಬಹುದು. | Draft — needs native review |
| `children_edit` | feature:settings | Edit %1$s’s profile | %1$s ಅವರ ಪ್ರೊಫೈಲ್ ಬದಲಿಸಿ | Draft — needs native review |
| `children_interest_separator` | feature:settings | " · " | " · " | Draft — needs native review |
| `children_switch` | feature:settings | Switch | ಬದಲಿಸಿ | Draft — needs native review |
| `children_title` | feature:settings | Child Profiles | ಮಕ್ಕಳ ಪ್ರೊಫೈಲ್‌ಗಳು | Draft — needs native review |
| `notifications_earlier` | feature:settings | EARLIER | ಹಿಂದಿನವು | Draft — needs native review |
| `notifications_empty_text` | feature:settings | New stories and reminders will appear here. | ಹೊಸ ಕಥೆಗಳು ಮತ್ತು ಜ್ಞಾಪನೆಗಳು ಇಲ್ಲಿ ಕಾಣಿಸುತ್ತವೆ. | Draft — needs native review |
| `notifications_empty_title` | feature:settings | You’re all caught up | ಎಲ್ಲವನ್ನೂ ನೋಡಿದ್ದೀರಿ | Draft — needs native review |
| `notifications_mark_all` | feature:settings | Mark all read | ಎಲ್ಲವನ್ನೂ ಓದಿದೆ ಎಂದು ಗುರುತಿಸಿ | Draft — needs native review |
| `notifications_title` | feature:settings | Notifications | ಅಧಿಸೂಚನೆಗಳು | Draft — needs native review |
| `notifications_today` | feature:settings | TODAY | ಇಂದು | Draft — needs native review |
| `notifications_unread` | feature:settings | Unread | ಓದಿಲ್ಲ | Draft — needs native review |
| `profile_add_child` | feature:settings | Add a child profile | ಮಗುವಿನ ಪ್ರೊಫೈಲ್ ಸೇರಿಸಿ | Draft — needs native review |
| `profile_audio` | feature:settings | Audio preferences | ಧ್ವನಿ ಆದ್ಯತೆಗಳು | Draft — needs native review |
| `profile_audio_subtitle` | feature:settings | Simulated narration · %1$s× speed | ಅನುಕರಿಸಿದ ನಿರೂಪಣೆ · %1$s× ವೇಗ | Draft — needs native review |
| `profile_cancel` | feature:settings | Cancel | ರದ್ದುಮಾಡಿ | Draft — needs native review |
| `profile_children_count[one]` | feature:settings | %1$d child profile | %1$d ಮಗುವಿನ ಪ್ರೊಫೈಲ್ | Draft — needs native review |
| `profile_children_count[other]` | feature:settings | %1$d child profiles | %1$d ಮಕ್ಕಳ ಪ್ರೊಫೈಲ್‌ಗಳು | Draft — needs native review |
| `profile_downloads` | feature:settings | Download settings | ಡೌನ್‌ಲೋಡ್ ಸೆಟ್ಟಿಂಗ್‌ಗಳು | Draft — needs native review |
| `profile_downloads_any` | feature:settings | Any network | ಯಾವುದೇ ನೆಟ್‌ವರ್ಕ್ | Draft — needs native review |
| `profile_downloads_wifi` | feature:settings | Wi-Fi only | ವೈ-ಫೈ ಮಾತ್ರ | Draft — needs native review |
| `profile_edit_child` | feature:settings | Edit child profile | ಮಗುವಿನ ಪ್ರೊಫೈಲ್ ಬದಲಿಸಿ | Draft — needs native review |
| `profile_family` | feature:settings | Your family | ನಿಮ್ಮ ಕುಟುಂಬ | Draft — needs native review |
| `profile_footer` | feature:settings | Stories created with care. Profiles are private and parent-managed. | ಕಾಳಜಿಯಿಂದ ರಚಿಸಿದ ಕಥೆಗಳು. ಪ್ರೊಫೈಲ್‌ಗಳು ಖಾಸಗಿ ಮತ್ತು ಪೋಷಕರ ನಿರ್ವಹಣೆಯಲ್ಲಿವೆ. | Draft — needs native review |
| `profile_group_app` | feature:settings | LITTLESTORY | ಲಿಟಲ್‌ಸ್ಟೋರಿ | Draft — needs native review |
| `profile_group_children` | feature:settings | CHILDREN | ಮಕ್ಕಳು | Draft — needs native review |
| `profile_group_experience` | feature:settings | EXPERIENCE | ಅನುಭವ | Draft — needs native review |
| `profile_help` | feature:settings | Help & about | ಸಹಾಯ ಮತ್ತು ಮಾಹಿತಿ | Draft — needs native review |
| `profile_help_subtitle` | feature:settings | Coming soon | ಶೀಘ್ರದಲ್ಲೇ ಬರಲಿದೆ | Draft — needs native review |
| `profile_language` | feature:settings | Language preferences | ಭಾಷೆಯ ಆದ್ಯತೆಗಳು | Draft — needs native review |
| `profile_manage_children` | feature:settings | Manage children | ಮಕ್ಕಳನ್ನು ನಿರ್ವಹಿಸಿ | Draft — needs native review |
| `profile_notifications` | feature:settings | Notifications | ಅಧಿಸೂಚನೆಗಳು | Draft — needs native review |
| `profile_notifications_subtitle` | feature:settings | Stories and family updates | ಕಥೆಗಳು ಮತ್ತು ಕುಟುಂಬದ ಸುದ್ದಿಗಳು | Draft — needs native review |
| `profile_privacy` | feature:settings | Privacy & parent controls | ಗೌಪ್ಯತೆ ಮತ್ತು ಪೋಷಕರ ನಿಯಂತ್ರಣ | Draft — needs native review |
| `profile_privacy_subtitle` | feature:settings | Your family’s data stays on this device | ನಿಮ್ಮ ಕುಟುಂಬದ ಮಾಹಿತಿ ಈ ಸಾಧನದಲ್ಲೇ ಇರುತ್ತದೆ | Draft — needs native review |
| `profile_private_note` | feature:settings | Private and parent-managed | ಖಾಸಗಿ ಮತ್ತು ಪೋಷಕರ ನಿರ್ವಹಣೆ | Draft — needs native review |
| `profile_reminder` | feature:settings | Bedtime reminders | ಮಲಗುವ ಸಮಯದ ಜ್ಞಾಪನೆಗಳು | Draft — needs native review |
| `profile_reminder_every_day` | feature:settings | Every day at %1$s | ಪ್ರತಿದಿನ %1$s ಕ್ಕೆ | Draft — needs native review |
| `profile_reminder_off` | feature:settings | Off | ಆಫ್ | Draft — needs native review |
| `profile_reminder_some_days` | feature:settings | Some days at %1$s | ಕೆಲವು ದಿನ %1$s ಕ್ಕೆ | Draft — needs native review |
| `profile_subscription` | feature:settings | Subscription | ಚಂದಾದಾರಿಕೆ | Draft — needs native review |
| `profile_subscription_subtitle` | feature:settings | Free plan | ಉಚಿತ ಯೋಜನೆ | Draft — needs native review |
| `profile_title` | feature:settings | Profile | ಪ್ರೊಫೈಲ್ | Draft — needs native review |
| `reminder_panel_body` | feature:settings | We’ll keep it quiet and calm, just one reminder. | ಶಾಂತವಾಗಿ, ಸದ್ದಿಲ್ಲದೆ, ಒಂದೇ ಒಂದು ಜ್ಞಾಪನೆ. | Draft — needs native review |
| `reminder_panel_title` | feature:settings | A gentle nudge for storytime | ಕಥೆಯ ಸಮಯಕ್ಕೆ ಒಂದು ಮೃದು ನೆನಪು | Draft — needs native review |
| `reminder_quiet_body` | feature:settings | No sound, no bright alerts | ಶಬ್ದವಿಲ್ಲ, ಪ್ರಖರ ಎಚ್ಚರಿಕೆಗಳಿಲ್ಲ | Draft — needs native review |
| `reminder_quiet_title` | feature:settings | Quiet bedtime experience | ಶಾಂತ ಮಲಗುವ ಅನುಭವ | Draft — needs native review |
| `reminder_repeat` | feature:settings | Repeat on | ಪುನರಾವರ್ತಿಸುವ ದಿನಗಳು | Draft — needs native review |
| `reminder_save` | feature:settings | Save reminder | ಜ್ಞಾಪನೆ ಉಳಿಸಿ | Draft — needs native review |
| `reminder_saved` | feature:settings | Reminder saved for %1$s | %1$s ಕ್ಕೆ ಜ್ಞಾಪನೆ ಉಳಿಸಲಾಗಿದೆ | Draft — needs native review |
| `reminder_time` | feature:settings | Reminder time | ಜ್ಞಾಪನೆಯ ಸಮಯ | Draft — needs native review |
| `reminder_time_done` | feature:settings | Done | ಆಯಿತು | Draft — needs native review |
| `reminder_title` | feature:settings | Bedtime Reminder | ಮಲಗುವ ಸಮಯದ ಜ್ಞಾಪನೆ | Draft — needs native review |
| `reminder_toggle` | feature:settings | Bedtime reminder | ಮಲಗುವ ಸಮಯದ ಜ್ಞಾಪನೆ | Draft — needs native review |
| `reminder_toggle_hint` | feature:settings | Pause anytime | ಯಾವಾಗ ಬೇಕಾದರೂ ನಿಲ್ಲಿಸಿ | Draft — needs native review |
| `subscription_benefit_collections` | feature:settings | Premium story collections | ಪ್ರೀಮಿಯಂ ಕಥಾ ಸಂಗ್ರಹಗಳು | Draft — needs native review |
| `subscription_benefit_narration` | feature:settings | Audio narration | ಧ್ವನಿ ನಿರೂಪಣೆ | Draft — needs native review |
| `subscription_benefit_offline` | feature:settings | Offline downloads | ಆಫ್‌ಲೈನ್ ಡೌನ್‌ಲೋಡ್‌ಗಳು | Draft — needs native review |
| `subscription_benefit_personalised` | feature:settings | More personalised stories | ಇನ್ನಷ್ಟು ವೈಯಕ್ತಿಕ ಕಥೆಗಳು | Draft — needs native review |
| `subscription_benefit_profiles` | feature:settings | Multiple child profiles | ಹಲವು ಮಕ್ಕಳ ಪ್ರೊಫೈಲ್‌ಗಳು | Draft — needs native review |
| `subscription_benefit_weekly` | feature:settings | New stories every week | ಪ್ರತಿ ವಾರ ಹೊಸ ಕಥೆಗಳು | Draft — needs native review |
| `subscription_best_value` | feature:settings | BEST VALUE | ಉತ್ತಮ ಮೌಲ್ಯ | Draft — needs native review |
| `subscription_body` | feature:settings | More wonder, more listening, and stories that grow with your family. | ಇನ್ನಷ್ಟು ಅದ್ಭುತ, ಇನ್ನಷ್ಟು ಕೇಳುವಿಕೆ, ಮತ್ತು ನಿಮ್ಮ ಕುಟುಂಬದೊಂದಿಗೆ ಬೆಳೆಯುವ ಕಥೆಗಳು. | Draft — needs native review |
| `subscription_choose` | feature:settings | Choose %1$s plan | %1$s ಯೋಜನೆ ಆಯ್ಕೆಮಾಡಿ | Draft — needs native review |
| `subscription_dialog_done` | feature:settings | Done | ಆಯಿತು | Draft — needs native review |
| `subscription_dialog_text` | feature:settings | This is a prototype, so no payment was made. You selected the %1$s plan. | ಇದು ಮಾದರಿ ಆವೃತ್ತಿ, ಆದ್ದರಿಂದ ಯಾವುದೇ ಪಾವತಿ ಆಗಿಲ್ಲ. ನೀವು %1$s ಯೋಜನೆಯನ್ನು ಆಯ್ಕೆ ಮಾಡಿದ್ದೀರಿ. | Draft — needs native review |
| `subscription_dialog_title` | feature:settings | Plan selected | ಯೋಜನೆ ಆಯ್ಕೆಯಾಗಿದೆ | Draft — needs native review |
| `subscription_kicker` | feature:settings | LITTLESTORY PREMIUM | ಲಿಟಲ್‌ಸ್ಟೋರಿ ಪ್ರೀಮಿಯಂ | Draft — needs native review |
| `subscription_note` | feature:settings | Illustrative pricing only. No real payment will be processed. | ಬೆಲೆಗಳು ಉದಾಹರಣೆಗೆ ಮಾತ್ರ. ಯಾವುದೇ ನಿಜವಾದ ಪಾವತಿ ನಡೆಯುವುದಿಲ್ಲ. | Draft — needs native review |
| `subscription_period_family[one]` | feature:settings | per year · %1$d child | ವರ್ಷಕ್ಕೆ · %1$d ಮಗು | Draft — needs native review |
| `subscription_period_family[other]` | feature:settings | per year · up to %1$d children | ವರ್ಷಕ್ಕೆ · %1$d ಮಕ್ಕಳವರೆಗೆ | Draft — needs native review |
| `subscription_period_free` | feature:settings | Try the magic | ಮಾಯೆಯನ್ನು ಪ್ರಯತ್ನಿಸಿ | Draft — needs native review |
| `subscription_period_month` | feature:settings | per month | ತಿಂಗಳಿಗೆ | Draft — needs native review |
| `subscription_period_year` | feature:settings | per year · best value | ವರ್ಷಕ್ಕೆ · ಉತ್ತಮ ಮೌಲ್ಯ | Draft — needs native review |
| `subscription_plan_family` | feature:settings | Premium Family | ಪ್ರೀಮಿಯಂ ಕುಟುಂಬ | Draft — needs native review |
| `subscription_plan_free` | feature:settings | Free | ಉಚಿತ | Draft — needs native review |
| `subscription_plan_monthly` | feature:settings | Premium Monthly | ಪ್ರೀಮಿಯಂ ಮಾಸಿಕ | Draft — needs native review |
| `subscription_plan_yearly` | feature:settings | Premium Yearly | ಪ್ರೀಮಿಯಂ ವಾರ್ಷಿಕ | Draft — needs native review |
| `subscription_title` | feature:settings | Make every bedtime magical. | ಪ್ರತಿ ಮಲಗುವ ಸಮಯವನ್ನೂ ಮಾಯಾಮಯವಾಗಿಸಿ. | Draft — needs native review |

### P6 strings (Language, Privacy, Help)

| Key | Module | English | Kannada (draft) | Status |
|---|---|---|---|---|
| `help_a_children` | feature:settings | Profile → Manage children → Add a child profile. | ಪ್ರೊಫೈಲ್ → ಮಕ್ಕಳನ್ನು ನಿರ್ವಹಿಸಿ → ಮಗುವಿನ ಪ್ರೊಫೈಲ್ ಸೇರಿಸಿ. | Draft — needs native review |
| `help_a_language` | feature:settings | Profile → Language preferences. The app and stories can use different languages. | ಪ್ರೊಫೈಲ್ → ಭಾಷೆಯ ಆದ್ಯತೆಗಳು. ಆ್ಯಪ್ ಮತ್ತು ಕಥೆಗಳು ಬೇರೆ ಬೇರೆ ಭಾಷೆಗಳನ್ನು ಬಳಸಬಹುದು. | Draft — needs native review |
| `help_a_offline` | feature:settings | Yes. All stories are on the device; downloading only adds a story to the Downloaded tab. | ಹೌದು. ಎಲ್ಲಾ ಕಥೆಗಳು ಸಾಧನದಲ್ಲೇ ಇವೆ; ಡೌನ್‌ಲೋಡ್‌ಗಳು ಕೇವಲ ಡೌನ್‌ಲೋಡ್ ಮಾಡಿದವು ಟ್ಯಾಬ್‌ಗಾಗಿ ಗುರುತಿಸುತ್ತವೆ. | Draft — needs native review |
| `help_a_stories` | feature:settings | Tell us about your child’s day on Create. This version builds the story on the device from a template; narration is simulated. | ರಚಿಸಿ ಪುಟದಲ್ಲಿ ನಿಮ್ಮ ಮಗುವಿನ ದಿನದ ಬಗ್ಗೆ ತಿಳಿಸಿ. ಈ ಆವೃತ್ತಿ ಸಾಧನದಲ್ಲೇ ಮಾದರಿಯಿಂದ ಕಥೆ ರಚಿಸುತ್ತದೆ; ನಿರೂಪಣೆ ಅನುಕರಿಸಿದ್ದು. | Draft — needs native review |
| `help_group_about` | feature:settings | ABOUT | ಆ್ಯಪ್ ಬಗ್ಗೆ | Draft — needs native review |
| `help_group_questions` | feature:settings | QUESTIONS | ಪ್ರಶ್ನೆಗಳು | Draft — needs native review |
| `help_licences_body` | feature:settings | Fonts: %1$s, under the SIL Open Font License 1.1. Built with Android Jetpack libraries under the Apache License 2.0. | ಅಕ್ಷರಶೈಲಿಗಳು: %1$s, SIL ಓಪನ್ ಫಾಂಟ್ ಲೈಸೆನ್ಸ್ 1.1 ಅಡಿಯಲ್ಲಿ. Apache ಲೈಸೆನ್ಸ್ 2.0 ಅಡಿಯ Android Jetpack ಲೈಬ್ರರಿಗಳಿಂದ ನಿರ್ಮಿಸಲಾಗಿದೆ. | Draft — needs native review |
| `help_licences_title` | feature:settings | Open-source licences | ಮುಕ್ತ ಮೂಲ ಪರವಾನಗಿಗಳು | Draft — needs native review |
| `help_q_children` | feature:settings | How do I add another child? | ಇನ್ನೊಂದು ಮಗುವನ್ನು ಹೇಗೆ ಸೇರಿಸುವುದು? | Draft — needs native review |
| `help_q_language` | feature:settings | How do I change the language? | ಭಾಷೆಯನ್ನು ಹೇಗೆ ಬದಲಾಯಿಸುವುದು? | Draft — needs native review |
| `help_q_offline` | feature:settings | Can we listen offline? | ಆಫ್‌ಲೈನ್‌ನಲ್ಲಿ ಕೇಳಬಹುದೇ? | Draft — needs native review |
| `help_q_stories` | feature:settings | How are stories made? | ಕಥೆಗಳನ್ನು ಹೇಗೆ ರಚಿಸಲಾಗುತ್ತದೆ? | Draft — needs native review |
| `help_title` | feature:settings | Help & about | ಸಹಾಯ ಮತ್ತು ಮಾಹಿತಿ | Draft — needs native review |
| `help_version_body` | feature:settings | %1$s (build %2$d) | %1$s (ಬಿಲ್ಡ್ %2$d) | Draft — needs native review |
| `help_version_title` | feature:settings | Version | ಆವೃತ್ತಿ | Draft — needs native review |
| `language_app_group` | feature:settings | APP LANGUAGE | ಆ್ಯಪ್ ಭಾಷೆ | Draft — needs native review |
| `language_fallback_note` | feature:settings | If a story is not yet available in the chosen language, it is shown in English. | ಆಯ್ಕೆ ಮಾಡಿದ ಭಾಷೆಯಲ್ಲಿ ಕಥೆ ಇನ್ನೂ ಲಭ್ಯವಿಲ್ಲದಿದ್ದರೆ, ಅದು ಇಂಗ್ಲಿಷ್‌ನಲ್ಲಿ ತೋರಿಸಲಾಗುತ್ತದೆ. | Draft — needs native review |
| `language_story_group` | feature:settings | STORY LANGUAGE | ಕಥೆಯ ಭಾಷೆ | Draft — needs native review |
| `language_title` | feature:settings | Language preferences | ಭಾಷೆಯ ಆದ್ಯತೆಗಳು | Draft — needs native review |
| `privacy_banner` | feature:settings | Everything stays on this device. LittleStory has no account, no ads and no tracking. | ಎಲ್ಲವೂ ಈ ಸಾಧನದಲ್ಲೇ ಇರುತ್ತದೆ. ಲಿಟಲ್‌ಸ್ಟೋರಿಯಲ್ಲಿ ಖಾತೆ, ಜಾಹೀರಾತು ಅಥವಾ ಟ್ರ್ಯಾಕಿಂಗ್ ಇಲ್ಲ. | Draft — needs native review |
| `privacy_confirm_action` | feature:settings | Delete | ಅಳಿಸಿ | Draft — needs native review |
| `privacy_confirm_body` | feature:settings | This cannot be undone. Profiles, favourites, progress, downloads and created stories will be gone. | ಇದನ್ನು ಹಿಂಪಡೆಯಲಾಗುವುದಿಲ್ಲ. ಪ್ರೊಫೈಲ್‌ಗಳು, ಮೆಚ್ಚಿನವುಗಳು, ಪ್ರಗತಿ, ಡೌನ್‌ಲೋಡ್‌ಗಳು ಮತ್ತು ರಚಿಸಿದ ಕಥೆಗಳು ಇಲ್ಲವಾಗುತ್ತವೆ. | Draft — needs native review |
| `privacy_confirm_title` | feature:settings | Delete everything? | ಎಲ್ಲವನ್ನೂ ಅಳಿಸಬೇಕೆ? | Draft — needs native review |
| `privacy_creation_body` | feature:settings | Stories are made on this device from a built-in template; what you type is never sent anywhere. | ಕಥೆಗಳನ್ನು ಈ ಸಾಧನದಲ್ಲೇ ಒಳಗೊಂಡಿರುವ ಮಾದರಿಯಿಂದ ರಚಿಸಲಾಗುತ್ತದೆ; ನೀವು ಬರೆದದ್ದು ಎಲ್ಲಿಗೂ ಕಳುಹಿಸಲಾಗುವುದಿಲ್ಲ. | Draft — needs native review |
| `privacy_creation_title` | feature:settings | Story creation | ಕಥೆ ರಚನೆ | Draft — needs native review |
| `privacy_delete_action` | feature:settings | Delete all data | ಎಲ್ಲಾ ಮಾಹಿತಿ ಅಳಿಸಿ | Draft — needs native review |
| `privacy_delete_body` | feature:settings | Removes every child profile, favourite, progress, download, created story and setting. The app starts fresh. | ಎಲ್ಲಾ ಮಕ್ಕಳ ಪ್ರೊಫೈಲ್‌ಗಳು, ಮೆಚ್ಚಿನವುಗಳು, ಪ್ರಗತಿ, ಡೌನ್‌ಲೋಡ್‌ಗಳು, ರಚಿಸಿದ ಕಥೆಗಳು ಮತ್ತು ಸೆಟ್ಟಿಂಗ್‌ಗಳನ್ನು ತೆಗೆದುಹಾಕುತ್ತದೆ. ಆ್ಯಪ್ ಹೊಸದಾಗಿ ಆರಂಭವಾಗುತ್ತದೆ. | Draft — needs native review |
| `privacy_delete_title` | feature:settings | Delete all data on this device | ಈ ಸಾಧನದ ಎಲ್ಲಾ ಮಾಹಿತಿಯನ್ನು ಅಳಿಸಿ | Draft — needs native review |
| `privacy_group_controls` | feature:settings | PARENT CONTROLS | ಪೋಷಕರ ನಿಯಂತ್ರಣ | Draft — needs native review |
| `privacy_group_data` | feature:settings | YOUR DATA | ನಿಮ್ಮ ಮಾಹಿತಿ | Draft — needs native review |
| `privacy_shared_body` | feature:settings | Nothing. This version does not connect to the internet. | ಏನೂ ಇಲ್ಲ. ಈ ಆವೃತ್ತಿ ಇಂಟರ್ನೆಟ್‌ಗೆ ಸಂಪರ್ಕಿಸುವುದಿಲ್ಲ. | Draft — needs native review |
| `privacy_shared_title` | feature:settings | What leaves the device | ಸಾಧನದಿಂದ ಹೊರಗೆ ಹೋಗುವುದು | Draft — needs native review |
| `privacy_stored_body` | feature:settings | Each child’s first name, age, avatar and interests, plus favourites, reading progress, downloads and the stories you create. | ಪ್ರತಿ ಮಗುವಿನ ಮೊದಲ ಹೆಸರು, ವಯಸ್ಸು, ಅವತಾರ ಮತ್ತು ಆಸಕ್ತಿಗಳು, ಜೊತೆಗೆ ಮೆಚ್ಚಿನವುಗಳು, ಓದುವ ಪ್ರಗತಿ, ಡೌನ್‌ಲೋಡ್‌ಗಳು ಮತ್ತು ನೀವು ರಚಿಸಿದ ಕಥೆಗಳು. | Draft — needs native review |
| `privacy_stored_title` | feature:settings | What we keep | ನಾವು ಉಳಿಸಿಕೊಳ್ಳುವುದು | Draft — needs native review |
| `privacy_title` | feature:settings | Privacy & parent controls | ಗೌಪ್ಯತೆ ಮತ್ತು ಪೋಷಕರ ನಿಯಂತ್ರಣ | Draft — needs native review |
| `profile_help_subtitle` | feature:settings | Questions, version and licences | ಪ್ರಶ್ನೆಗಳು, ಆವೃತ್ತಿ ಮತ್ತು ಪರವಾನಗಿಗಳು | Draft — needs native review |
| `profile_language_split` | feature:settings | %1$s · Stories: %2$s | %1$s · ಕಥೆಗಳು: %2$s | Draft — needs native review |

### P7 strings (simulation label)

| Key | Module | English | Kannada (draft) | Status |
|---|---|---|---|---|
| `generating_simulated` | feature:create | Simulated: your story is put together on this device from sample stories. No AI is used yet. | ಅನುಕರಿಸಿದ ಕಥೆ: ಈ ಸಾಧನದಲ್ಲೇ ಮಾದರಿ ಕಥೆಗಳಿಂದ ನಿಮ್ಮ ಕಥೆಯನ್ನು ಜೋಡಿಸಲಾಗುತ್ತದೆ. ಇನ್ನೂ ಯಾವುದೇ AI ಬಳಸುತ್ತಿಲ್ಲ. | Draft — needs native review |

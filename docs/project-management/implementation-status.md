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
| LS-074 | Implemented (P4) | Story detail: favourite and simulated download toggles; Listen and Read are no-ops until Player and Reader exist |
| Bottom nav | Partly wired | Home and Discover tabs navigate; Create, My Stories and Profile do nothing until their milestones |

All of the above await owner review; none is Approved or merged.

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

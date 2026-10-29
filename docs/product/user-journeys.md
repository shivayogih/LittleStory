# LittleStory — User Journeys

Purpose: map journeys A–G from the brief onto the screens that exist in the design pack, show each as a flow, and record where the design is silent.

Status: Draft for owner review — P1, 29 Oct 2026

Sources: master orchestrator brief §6.1; `docs/design/LittleStory_Figma_Design_Details.md` §3 (interactions per screen); workbook Screens sheet (entry and exit paths). The live Figma Make prototype could not be re-read during P0, so every path below is *Needs Figma verification* against the prototype.

## Coverage summary

| Journey | Brief path | Covered by design? | Screens | Requirements |
| --- | --- | --- | --- | --- |
| A First-time experience | Launch → Welcome/onboarding → (language) → Child profile → Home | Mostly. Splash missing; language is a field inside Setup, not a step | SCR-19 (missing), SCR-01, SCR-02, SCR-03 | FR-01, FR-02, FR-20, FR-25 |
| B Discover and read | Home → Discover → Story details → Read → Save → My Stories | Yes. "Save" is the favourite heart; saved stories appear in My Stories ▸ Favourites | SCR-03, SCR-04a/b, SCR-05, SCR-06, SCR-10, SCR-12 | FR-04–FR-07, FR-11, FR-13, FR-14 |
| C Create a story | Create → validation → simulated generation → result → Reader → Save | Yes. Save is automatic ("Saved to My Stories") | SCR-07, SCR-08, SCR-09, SCR-10, SCR-12 | FR-08–FR-11, FR-21 |
| D Revisit saved stories | Home → My Stories → saved story → Reader → progress | Yes. Continue card on Home and Recently played in My Stories resume the Player; list rows open Player or Detail | SCR-03, SCR-12, SCR-11a, SCR-10 | FR-14, FR-24 |
| E Parent controls | Profile area → Parent settings → language, safety, privacy | Partly. Hub exists; Privacy, Help, Language, Account and Audio rows route to placeholders | SCR-13, SCR-14, SCR-16, SCR-18, SCR-15, SCR-20–23 (missing) | FR-03, FR-16–FR-19, FR-22, FR-23 |
| F Simulated audio | Detail or Reader → Player → controls → back to Reader | Yes | SCR-06, SCR-10, SCR-11a, SCR-11b | FR-12 |
| G Change language | Settings → Language → EN / ಕನ್ನಡ → UI and content update → persists | **No design.** Profile's "Language preferences" row routes to Setup in the prototype | SCR-13, SCR-20 (missing), SCR-02 | FR-20, FR-21 |

Legend for the diagrams: solid arrows are designed transitions; dotted arrows (`-.->`) are proposed behaviour with no design, pending decision D10.

## Journey A — First-time experience

```mermaid
flowchart TD
    L["App launch"] -.-> SPL["Splash SCR-19 (no design)"]
    SPL -.-> FR{"First run?"}
    FR -->|yes| O1["Onboarding slide 1 SCR-01"]
    FR -->|no| H["Home SCR-03"]
    O1 -->|Continue| O2["Onboarding slide 2"]
    O2 -->|Continue| O3["Onboarding slide 3"]
    O3 -->|Get Started| S["Setup SCR-02"]
    O1 -->|Skip| S
    O2 -->|Skip| S
    O3 -->|Skip| S
    S -->|"Pick language EN or KN"| LANG["UI switches language immediately"]
    LANG --> S
    S -->|"Create child's profile"| H
    S -->|"Skip for now"| H
```

Notes:

- Splash and the first-run decision have no design (SCR-19, FR-25, decision D10). Proposal: Android SplashScreen API with the LittleStory symbol, then route on a persisted first-run flag.
- The onboarding scene is identical on all three slides; there is no swipe or back between slides in the prototype (open question R-09).
- The Setup language select shows seven languages in the design. Proposed (D9): show only registry-enabled English and ಕನ್ನಡ, each in its own script, and apply the choice at once.
- "Skip for now" creates no profile. What Home shows with no profile is undesigned (edge case, see [edge-cases.md](edge-cases.md)).

## Journey B — Discover and read

```mermaid
flowchart TD
    H["Home SCR-03"] -->|"Explore stories: See all"| D["Discover SCR-04a"]
    H -->|"Category tile or culture banner"| C["Category SCR-05"]
    H -->|"Made for child: story card"| DET["Story detail SCR-06"]
    D -->|"Type in search"| DS["Results for query"]
    DS -->|no match| DE["No results SCR-04b"]
    DE -->|"Clear search"| D
    D -->|"View all"| C
    D -->|"Story card or featured card"| DET
    DS --> DET
    C --> DET
    DET -->|"Read Story"| R["Reader SCR-10"]
    DET -->|"Listen to Story"| P["Player SCR-11a"]
    DET -->|"Heart"| FAV["Favourited"]
    R -->|"Heart"| FAV
    FAV --> MS["My Stories SCR-12, Favourites tab"]
```

Notes:

- The brief's "Save story" maps to the favourite heart; the design has no separate save action. Confirm with the owner (open question below).
- Featured "Listen now" opens Story detail in the prototype, although the label suggests the Player (open question in Screens sheet).
- The Category screen is hardcoded to Indian Tales in the prototype; the implementation must be data-driven for every category (LS-073, R-18).

## Journey C — Create a story

```mermaid
flowchart TD
    H["Home hero: Create a Story"] --> CR["Create SCR-07"]
    NAV["Bottom nav: Create"] --> CR
    CR -->|"Tap Create My Story"| V{"Text entered?"}
    V -->|no| ERR["Error banner: tell us one little moment"]
    ERR -->|"User types"| CR
    V -->|yes| G["Generating SCR-08, simulated"]
    G -->|"Close"| PA["Paused state"]
    PA -->|"Try again"| G
    PA -->|"Back to your idea"| CR
    G -->|"100 percent"| RDY["Story ready SCR-09"]
    RDY -->|"Read Story"| R["Reader SCR-10"]
    RDY -->|"Listen Now"| P["Player SCR-11a"]
    RDY -->|"Saved to My Stories row"| MS["My Stories SCR-12, Created by Me"]
```

Notes:

- Generation is a deterministic simulation (FR-09, NFR-12); the same input gives the same story. Timing values live in `AppConfig`, not in screens.
- The created story is generated in the selected story language (English or ಕನ್ನಡ, FR-21) and saved automatically to "Created by Me".
- The placeholder example sentence is gendered ("helped his friend") and must be rewritten as neutral, translatable copy (R-06).

```mermaid
sequenceDiagram
    actor Parent
    participant CS as Create screen
    participant VM as CreateViewModel
    participant UC as GenerateStory use case
    participant Repo as StoryGenerationRepository (simulated)
    participant Lib as SavedStoriesRepository
    Parent->>CS: Tap Create My Story
    CS->>VM: onCreateClicked
    VM->>VM: validate request (limits from AppConfig)
    alt text empty
        VM-->>CS: state with validation error (UiText)
    else valid
        VM->>UC: generate(request, storyLanguage)
        UC->>Repo: start job
        loop progress ticks
            Repo-->>VM: progress percent and stage
        end
        Repo-->>UC: generated story (stable id)
        UC->>Lib: save to Created by Me
        VM-->>CS: navigate to Story ready
    end
```

## Journey D — Revisit saved stories

```mermaid
flowchart TD
    H["Home SCR-03"] -->|"Continue card: Resume story"| P["Player SCR-11a"]
    NAV["Bottom nav: My Stories"] --> MS["My Stories SCR-12"]
    MS -->|"Recently played"| P
    MS -->|"Tab: All, Created by Me, Downloaded, Favourites"| LIST["Filtered list"]
    LIST -->|"Row play"| P
    LIST -->|"Row"| DET["Story detail SCR-06"]
    DET -->|"Read Story"| R["Reader SCR-10, resumes last page"]
    P -->|"Story text"| R
    MS -->|"Download icon"| DL["Offline downloads SCR-17"]
```

Notes:

- Reading and listening progress must persist across restarts (FR-24, TC-033). Whether the Reader resumes at the last page is not shown in the design; proposed yes.
- Downloads are simulated in the MVP and must be labelled as such (NFR-12).

## Journey E — Parent controls

```mermaid
flowchart TD
    NAV["Bottom nav: Profile"] --> PR["Profile hub SCR-13"]
    PR -->|"Account settings"| ACC["Account settings SCR-23 (no design; prototype opens Setup)"]
    PR -->|"Manage children"| CP["Child profiles SCR-14"]
    CP -->|"Switch"| ACT["Active child changes everywhere"]
    CP -->|"Edit or Add child"| S["Setup SCR-02"]
    PR -->|"Language preferences"| LP["Language SCR-20 (no design)"]
    PR -->|"Bedtime reminders"| BR["Reminder SCR-16"]
    PR -->|"Notifications"| NO["Notifications SCR-18"]
    PR -->|"Audio preferences"| AU["Audio prefs SCR-23 (no design; prototype opens Player)"]
    PR -->|"Download settings"| DL["Offline downloads SCR-17"]
    PR -->|"Subscription"| SUB["Demo subscription SCR-15"]
    PR -->|"Privacy and parent controls"| PRIV["Privacy SCR-21 (no design)"]
    PR -->|"Help and about"| HELP["Help SCR-22 (no design)"]
```

Notes:

- In the prototype, toggles sit inside row buttons; they must be separate touch targets (TC-041).
- Privacy & parent controls (FR-22) and Help & about (FR-23) are Blocked tickets (LS-114, LS-115) until designs or an approved pattern exist.
- Demo subscription sits behind a feature flag and states that no payment is processed (FR-19, NFR-12).

## Journey F — Simulated audio

```mermaid
flowchart TD
    DET["Story detail: Listen to Story"] --> P["Player SCR-11a"]
    R["Reader: play narration"] --> P
    P -->|"Play or pause, seek, rewind or forward 15 s, chapters"| P
    P -->|"Speed: 0.75, 1, 1.25, 1.5x"| P
    P -->|"Sleep timer"| ST["Sleep timer sheet SCR-11b"]
    ST -->|"Pick option"| P
    ST -->|"Cancel or tap outside"| P
    P -->|"Story text"| R2["Reader SCR-10"]
    P -->|"Back"| PREV["Previous screen"]
```

Notes:

- Playback is simulated: there is no audio file or TTS. The Player must show that narration is simulated (TC-036); where it is shown is an open design question.
- The 15-second rewind/forward glyphs overlap in the design (LS-094, R-20).

## Journey G — Change language (no design)

```mermaid
flowchart TD
    PR["Profile hub SCR-13"] -.->|"Language preferences"| LP["Language SCR-20 (proposed list screen)"]
    LP -.->|"Select English or ಕನ್ನಡ"| APPLY["setApplicationLocales via LanguageRepository"]
    APPLY -.-> RECREATE["Activity recreated; UI and mock content reload in new language"]
    RECREATE -.-> SAME["Same screen, same story, same page"]
    SAME -.-> RESTART["Choice persists after restart"]
    S["Setup SCR-02: language field"] -->|"designed"| APPLY
```

```mermaid
sequenceDiagram
    actor User
    participant UI as Language screen
    participant VM as LanguageViewModel
    participant Repo as LanguageRepository
    participant OS as AppCompat per-app locale
    participant Data as Story repositories
    User->>UI: Select ಕನ್ನಡ
    UI->>VM: onLanguageSelected kn
    VM->>Repo: setAppLanguage kn
    Repo->>OS: setApplicationLocales kn
    OS-->>UI: configuration change, activity recreated
    UI->>VM: collect state with restored navigation
    VM->>Data: observe stories for locale kn
    Data-->>VM: Kannada content, English fallback per field
    VM-->>UI: render in Kannada
```

Notes:

- No language screen exists in the design. Proposal (D9/D10): a simple list built from existing settings-row components, each language in its own script with a check mark, reading the `SupportedLanguage` registry.
- Story IDs are identical across languages, so favourites, progress and created stories survive a language change (TC-048).
- Open question: one setting for app language and default story language, or two? (Screens sheet SCR-20.)

## Open questions

| # | Question | Journey | Owner decision |
| --- | --- | --- | --- |
| Q1 | Is "save" the favourite heart, or should a separate save exist? | B | Owner |
| Q2 | Splash design and first-run routing | A | D10 |
| Q3 | Language screen design; one or two language settings | G, E | D9, D10 |
| Q4 | Does the Reader resume at the last page? | D | Owner |
| Q5 | Where is "simulated narration" disclosed on the Player? | F | Owner |
| Q6 | What does Home show after "Skip for now" with no profile? | A | Owner |

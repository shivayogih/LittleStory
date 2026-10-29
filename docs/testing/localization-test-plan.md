# Localization test plan

Purpose: verify that English and Kannada are complete, correct and robust, and that new languages can be added without feature-code changes.
Status: Draft for owner review — P1, 29 Oct 2026
Sources: master orchestrator brief §4.4, §8.7, §17.8; workbook tickets LS-040…LS-048, Test Cases TC-003, 006, 007, 012, 028, 044, 048…052, 054; Risks R-03, R-04, R-12, R-13; ADR-005…ADR-009.

## 1. Automated checks

| ID | Check | How | Fails when | Test case |
| --- | --- | --- | --- | --- |
| L-01 | Key parity | `checkTranslations` Gradle task compares every `values/strings.xml` key with each `values-*/strings.xml` | A key is missing in `values-kn` | TC-050 |
| L-02 | Format-argument parity | Same task compares positional arguments (`%1$s`, `%2$d`) and plural quantities | Count or type differs between languages | TC-050 |
| L-03 | Hardcoded text | Android Lint `HardcodedText`, `SetTextI18n` as errors; Detekt rule for string literals in composables (if approved) | A user-facing literal in Kotlin/XML | TC-051 |
| L-04 | Missing translation | Lint `MissingTranslation` as error | Untranslated key | TC-050 |
| L-05 | Registry vs config | Unit test: `SupportedLanguage` enabled tags == `locales_config.xml` locales == `values-*` folders present | Any mismatch | TC-059 |
| L-06 | Mock-content parity | Unit test: every story ID exists in `stories_kn.json` or is listed as an expected fallback | Unlisted gap | TC-060 |
| L-07 | English fallback | Loader returns English fields with `isFallback = true`, never empty/raw keys | Crash or blank | TC-049 |
| L-08 | Pseudo-locale expansion | Screenshots in `en-XA` on key screens | Clipping or untranslated literal visible | TC-052 |
| L-09 | RTL readiness | Screenshots in `ar-XB`; Lint `RtlHardcoded` | Left/right assumptions | new |
| L-10 | Locale formatting | Unit tests for times, durations, counts via locale-aware APIs | Manual string assembly | TC-044 |

## 2. Behavioural checks (UI / integration)

| ID | Scenario | Expected | Test case |
| --- | --- | --- | --- |
| L-11 | Onboarding in Kannada | All text Kannada, no clipping | TC-003 |
| L-12 | Language list | Picker shows only registry-enabled languages, each in its own script: English, ಕನ್ನಡ | TC-006 |
| L-13 | Switch in Setup | UI switches immediately; no crash; state kept | TC-007 |
| L-14 | Switch mid-journey | Back stack, form input and saved data survive | TC-048 |
| L-15 | Restart persistence | Selected language applied after process kill and on API 26 and API 33+ | TC-048 |
| L-16 | Search in Kannada | Kannada titles found by Kannada query | TC-012 |
| L-17 | Kannada generation | Simulated story produced in Kannada | TC-028 |
| L-18 | Kannada input | Kannada keyboard (e.g. Gboard Kannada) in Create text field, counter counts characters correctly | TC-061 |
| L-19 | Kannada at 200 % font | Reader and forms readable, no overlap | TC-032 |

## 3. Manual checks

| ID | Check | Who |
| --- | --- | --- |
| L-20 | Native Kannada review of every string and mock story; glossary consistency (`docs/localization/glossary.md`) | Named native reviewer (R-12, D12, LS-047) |
| L-21 | TalkBack in Kannada on a device with a Kannada TTS voice; document devices without one | QA (TC-054, R-13) |
| L-22 | Kannada glyph rendering with the bundled fallback font on API 26 and latest | QA (R-03, D8) |
| L-23 | Cultural appropriateness of names, festivals, examples | Native reviewer |

## 4. Status rules

- Every Kannada string starts as "Draft" or "Needs native review" in the Translations sheet; only the native reviewer can mark "Approved".
- Release is blocked while any shipped Kannada string is not approved, unless the owner explicitly accepts the risk.
- Adding a language later re-runs L-01…L-10 for the new `values-xx` with no feature-code change (L-05 guards this).

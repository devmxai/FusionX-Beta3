# FusionX Clean UI

FusionX Clean UI is the active Android-first editor shell and native engine
foundation for rebuilding FusionX in controlled production phases.

This repository is no longer a mock UI experiment. The original Flutter editor
surface is wired to a real Android playback/scrub pipeline, and the current
goal is to turn that foundation into a professional mobile editor step by step.

## Current Status

Current milestone:

- `Beta 5`
- this beta is considered the first practical handoff point after the long
  preview/scrub stabilization cycle
- import, first-frame rendering, playback, seek, trim, and bidirectional scrub
  are now working inside the original product UI at a level good enough to move
  forward into the next engine phases
- reverse scrub has been significantly improved and the original “fully broken”
  state is no longer the baseline
- fine-grain polish can still continue later, but the current preview/scrub
  problem is considered closed for this beta milestone so the roadmap can move
  on

Built so far:

- the original FusionX editor UI remains the main product surface
- Flutter is still the UI layer
- Android native playback foundation is wired behind the original UI
- Vulkan bootstrap/runtime groundwork is present in the Android native layer
- a dedicated proxy conformer and proxy scrub session are wired into the
  Android engine
- real local video import works from device media storage
- the native preview surface is attached inside the original canvas
- real `play`, `pause`, `seek`, and `trim` work on device
- forward and reverse scrub now both work in a usable way on real clips
- the media bottom sheet is connected to Android media browsing for:
  - `Video`
  - `Image` browsing
- versioned APK workflow is in place
- build history is tracked in [docs/build-history.md](docs/build-history.md)

## Architecture Direction

Target architecture:

- Flutter UI only
- shared engine contracts and timeline authority
- Android native engine with `Kotlin + C++`
- Android media stack based on:
  - `MediaExtractor`
  - `MediaCodec`
  - `MediaMuxer`
  - `Oboe`
- Vulkan GPU compositor
- independent export pipeline
- iOS native engine later

Current implementation:

- the Android decoder preview path still owns loaded-clip playback and exact
  seek
- Vulkan bootstrap/runtime capability probing remains wired into the native
  layer, but clip preview runtime ownership still sits with the decoder path
- live scrub is routed through a dedicated proxy path:
  - source clip playback remains on the main decoder session
  - scrub can prepare a separate low-resolution proxy clip in app cache
  - scrub can use a dedicated native scrub session when the proxy is ready
- the latest scrub improvements include:
  - all-intra-oriented proxy generation
  - indexed proxy/source time mapping
  - direction-flip handling improvements in Flutter
  - reverse-start dead-zone reduction
- the next large decision is no longer “can scrub work at all”, but rather how
  to grow from this baseline into audio, project complexity, and deeper engine
  architecture

## What Works Right Now

Inside the original editor UI:

- attach native preview surface
- import a real local video clip
- show first frame
- play clip
- pause clip
- seek clip
- trim start and trim end
- browse local device videos from the bottom sheet
- build and prepare a dedicated scrub proxy clip in the Android engine
- scrub forward through the clip
- scrub backward through the clip
- change scrub direction without falling back to the earlier broken behavior

## What Is Not Built Yet

Not implemented yet:

- audio engine
- export pipeline
- effects
- transitions
- multi-track engine
- text engine
- lip sync engine
- iOS engine
- final shared native core
- image import into the playback engine

## Phase Closure

The long preview/scrub stabilization phase is now considered complete for the
beta roadmap.

What that means in practice:

- this repository now has a device-proven editor baseline that can import,
  preview, play, trim, and scrub inside the real product UI
- the earlier “scrub is fundamentally broken” blocker is no longer the
  dominating problem
- the engine is now good enough to move on to broader phases such as audio,
  richer timeline behavior, and larger project complexity

What this does **not** mean:

- the editor is not yet feature-complete
- polish opportunities still exist, especially for very fine micro-scrub feel
- the current beta is a practical shipping milestone for the foundation, not
  the end of engine work

## Beta 5 Notes

What `Beta 5` means:

- the original product UI is preserved as the only editor surface
- Android preview/playback/trim/import are established as a real working base
- proxy-based scrub is no longer experimental only; it is now part of the real
  engine path
- reverse scrub has been improved enough to stop blocking the roadmap
- this beta is the handoff point from “stabilize preview/scrub” to “expand the
  engine”

Main technical highlights of this beta cycle:

- first-frame and surface ownership regressions were recovered
- proxy scrub was moved to an all-intra-oriented path
- proxy/source time mapping was upgraded from simple duration ratio toward
  indexed frame mapping
- reverse-start direction flip handling was tightened in both timeline and
  screen stabilization code
- end-of-scrub and playback handoff behavior was hardened
- the engine is now substantially more predictable on real Android devices than
  the earlier beta baselines

Next major phase after `Beta 5`:

- audio phase 0
- richer project/timeline structure
- continued renderer/compositor evolution
- export and later advanced editor behaviors

## Repository Notes

- release APK files are generated locally and versioned in the local workflow
  but are not part of the repository source history
- progress is documented incrementally under `docs/`
- the current development approach is phase-based and device-verified

## Run

```bash
flutter pub get
flutter run
```

## Validation

Common checks used during development:

```bash
flutter analyze
flutter test
flutter build apk --release
```

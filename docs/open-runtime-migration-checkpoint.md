# Open-runtime migration checkpoint

This branch is a checkpoint for replacing the bundled `io.mzio` runtime dependencies with open-source/MIT-compatible implementations.

## Baseline

- Repository: `AurinVos/mzmine`
- Base branch: `main`
- Base commit: `2f39501633c0d135e1a5343d95f31b745c56ce81`
- Base change: `Remove mandatory MZmine account gating`

No claim is made here that the runtime is already free of proprietary dependencies. The login gate has been removed, but the `io.mzio` binary bundle is still present in the build graph.

## Current proprietary/unclear dependency roots

The main remaining build edges are:

`mzmine-community/build.gradle`

```gradle
implementation(libs.bundles.mzio)
```

`javafx-framework/build.gradle.kts`

```kotlin
implementation("io.mzio:memory-management:1.0.0")
implementation("io.mzio:taskcontroller:1.0.0")
```

`taskcontroller/build.gradle.kts`

```kotlin
implementation("io.mzio:memory-management:1.0.0")
```

The version catalogue currently contains:

- `io.mzio:global-events`
- `io.mzio:memory-management`
- `io.mzio:mzmine-core`
- `io.mzio:taskcontroller`
- `io.mzio:user-client`
- `io.mzio:user-management`
- `io.mzio:user-management-fx`

## Verified MIT source that can be reused

### Task controller

Verified source commit:

`fa4a736986fdbea384daabea549d5fdd933a9a9f`

Files at that commit carry explicit MZmine MIT headers:

- `taskcontroller/src/main/java/io/github/mzmine/taskcontrol/TaskController.java`
- `taskcontroller/src/main/java/io/github/mzmine/taskcontrol/TaskControllerImpl.java`
- `taskcontroller/src/main/java/io/github/mzmine/taskcontrol/TaskService.java`
- `taskcontroller/src/main/java/io/github/mzmine/taskcontrol/threadpools/FixedThreadPoolTask.java`
- `taskcontroller/src/main/java/io/github/mzmine/taskcontrol/threadpools/ProvidedThreadPoolTask.java`
- `taskcontroller/src/main/java/io/github/mzmine/taskcontrol/threadpools/ThreadPoolTask.java`
- `taskcontroller/src/main/java/io/github/mzmine/taskcontrol/threadpools/VirtualThreadPoolTask.java`
- `taskcontroller/src/main/java/io/github/mzmine/taskcontrol/utils/TaskUtils.java`

Transition evidence:

`8d85549908ecf04a7df7b776eab2724b23b429c4` (`move deps`)

This commit removes the source implementations and introduces the local `io.mzio` task-controller and memory-management JARs.

### Memory-map storage

Use the same verified MIT commit:

`fa4a736986fdbea384daabea549d5fdd933a9a9f`

Reusable files:

- `memory-management/src/main/java/io.github.mzmine.util/MemoryMapStorage.java`
- `memory-management/src/main/java/io.github.mzmine.util/MemoryMapStorages.java`

The newer `MemoryMapSnapshot` and `MemoryMapStorageStats` APIs should be independently implemented from current public call-site semantics rather than copied from binaries.

### CLI parser

Verified source commit:

`36f0cd628bd6c78111446cae2788c8ddcb3b28ca`

Reusable file:

- `mzmine-community/src/main/java/io/github/mzmine/main/MZmineArgumentParser.java`

Transition commit:

`0c3b50f91398bf185735ebe043c32006a4507f59`

This removes the old MIT parser and introduces `io.mzio:mzmine-core`.

Current required parser methods are visible in `MZmineCore.java` and `ArgsToConfigUtils.java`, including:

- `getBatchFile()`
- `getCsvDatabase()`
- `getMetadataFile()`
- `getOutBaseFile()`
- `getOverrideDataFiles()`
- `getOverrideSpectralLibrariesFiles()`
- `getProjectImport()`
- `isKeepRunningAfterBatch()`
- `getNumCores()`
- `getPreferencesFile()`
- `getTempDirectory()`
- `isIgnoreParameterWarnings()`
- `isKeepInMemory()`
- `isLoadTdfPseudoProfile()`

Account-related CLI options should not be carried into the replacement except, if desired, as deprecated recognized-and-ignored compatibility flags.

### Workspace code

Verified MIT source commit:

`3debbc0ec6afff6d2672444906d9fdfb5513626f`

Reusable historical source/design:

- `mzmine-community/src/main/java/io/github/mzmine/gui/mainwindow/workspaces/Workspace.java`
- `mzmine-community/src/main/java/io/github/mzmine/gui/mainwindow/workspaces/WorkspaceTags.java`
- `mzmine-community/src/main/java/io/github/mzmine/gui/mainwindow/workspaces/WorkspaceMenuUtils.java`

Later refactor:

`f6aca448a6d6807721b0207f6164064cc407e15d`

Current workspace implementation should be adapted to remove licence eligibility and account menus entirely.

## Components to remove rather than emulate

The following account/licensing components are not needed for local scientific processing and should be deleted from the open-runtime path:

- `CurrentUserService`
- `MZmineUser`
- `MzLicense`
- `UserAuthStore`
- `UserLoginService`
- `UsersController`
- `UsersViewState`
- `LoginOptions`
- `UserNotificationUtils`
- `UserActiveService`
- `UserType`
- `ServiceRestricted`
- `ServiceRestrictionUtils`
- `AuthRequiredEvent`
- `AuthServerNotReachedEvent`

The temporary application-level class added by the login-removal work:

`io.github.mzmine.taskcontrol.auth.TaskAuthService`

is transitional and should disappear once the open task-controller implementation no longer has an authorization stage.

## Independent replacements required

Do not decompile or copy implementation code from the `io.mzio` JARs.

Implement independently from public call sites/tests:

- task change event types used by the task view
- `TaskResultSummary`
- `MemoryMapSnapshot`
- `MemoryMapStorageStats`
- a minimal MZmine-owned event bus replacing `io.mzio.events.*`
- a small MZmine-owned result type replacing `io.mzio.general.Result`
- an MZmine-owned exit/shutdown helper replacing `MZmineExit`
- ordinary constants for still-useful documentation links replacing `MzioMZmineLinks`

## Workflow gating to remove

Current wizard workflow types still contain licensing abstractions such as:

- `ServiceRestricted`
- `UserActiveService`
- `MZmineUser`
- `checkUserForServices(...)`
- `getUnlockingServices()`

These should be removed rather than replaced with fake all-access users.

Affected areas include workflow classes for DDA, DIA, imaging, MS1-only, target plates, deconvolution, library generation, and `WizardWorkflows`.

## Known account-dependent call sites still to remove

Examples include:

- `MZminePreferences.handleLoadedParameters(...)` restoring users through `UsersController`
- `DeleteRowsTask` adding account username to provenance
- `CompoundAnnotationController` adding account nickname
- `SiriusApiFingerIdTask` deriving an opt-out key from the account username
- account/login entries in workspace and introduction UI
- `UsersTab`

Replace account-derived metadata with optional local configuration only where scientifically useful; otherwise remove it.

## Completion criteria

A completed implementation should satisfy:

1. `runtimeClasspath` contains no `io.mzio` artifact.
2. `git grep -n "io\.mzio"` finds no operational Java/Kotlin imports.
3. GUI starts without an account.
4. Headless batch starts without an account.
5. Task submission, cancellation, nested pools, and high-priority execution work.
6. Memory-mapped double/float/int storage works.
7. RAM-only storage modes work.
8. CLI parsing and exit status remain functional.
9. Scientific algorithms are unchanged by this infrastructure migration.

Testing may be completed by a separate agent.

## Provenance rule

For every restored implementation, record:

- historical source path
- exact source commit
- explicit MIT header presence
- adaptation made for current APIs

Do not use a decompiler/disassembler to reproduce current `io.mzio` implementation details.

## Status at this checkpoint

Completed:

- removed mandatory login gating on `main`
- restored and adapted the MIT command-line parser into the active source set
- redirected startup and configuration handling away from `MZmineCoreArgumentParser`
- identified the seven `io.mzio` artifacts
- identified the three root build dependency edges
- traced verified MIT task-controller source
- traced verified MIT memory-management source
- traced verified MIT CLI parser source
- traced verified MIT workspace source
- established deletion vs independent-reimplementation scope

Not yet completed:

- remaining source restoration into current modules
- adaptation to current APIs
- removal of `libs.bundles.mzio`
- removal of all account/user call sites
- independent replacement of newer infrastructure APIs
- compilation/testing

# MIT open-runtime migration

## Outcome

The active MZmine build no longer depends on the private `io.mzio` runtime bundle. The repository
now contains MIT-licensed implementations of the runtime facilities needed by the application and
has no account or licence gate for local processing.

## Provenance and clean-room boundary

The following source was restored from public commit
`fa4a736986fdbea384daabea549d5fdd933a9a9f`; each source file at that commit contains the MZmine MIT
licence header:

- task controller, service, utilities, and fixed/provided/virtual thread-pool tasks;
- `MemoryMapStorage` and `MemoryMapStorages`.

Commit `8d85549908ecf04a7df7b776eab2724b23b429c4` is the transition evidence: it removed these sources and
introduced the private `io.mzio` task-controller and memory-management artifacts. The restored
storage design was adapted independently to the public JDK 26 foreign-memory API and current MZmine
call sites. Current private binaries were not decompiled, disassembled, or used as an implementation
reference.

The command-line parser was previously restored from public MIT source commit
`36f0cd628bd6c78111446cae2788c8ddcb3b28ca`; transition commit
`0c3b50f91398bf185735ebe043c32006a4507f59` replaced it with `io.mzio:mzmine-core`.

## Replacements

- The active `taskcontroller` module again owns task scheduling, high-priority execution, nested
  pools, task-list notifications, and task-result aggregation.
- The `utils` module owns file-backed foreign-memory segments, RAM-only selection, allocation
  counters, snapshots, and cleanup.
- MZmine-owned event, result, link, workspace, and exit types replace the remaining private runtime
  APIs.
- User/account views, sign-in restoration, account expiry logic, account-derived provenance, and
  service-based wizard filtering were deleted rather than emulated.

## Removed material

- All seven `io.mzio` version-catalog entries and every build dependency on them.
- The complete `local-repo/io/mzio` binary repository.
- Committed Bruker BAF/TDF, SCIEX Wiff2, and Waters MassLynx native binaries whose redistribution
  terms are proprietary or unclear.
- Automatic SCIEX server downloading from the normal resource-processing path.

Raw formats that require vendor-native libraries are consequently unavailable in the MIT-only
binary unless an end user independently provides a legally compatible converter. This is an
intentional licensing boundary, not an attempt to recreate vendor code.

## Enforced completion criteria

`verifyOpenRuntime`, which is part of Gradle `check`, fails when:

1. `runtimeClasspath` resolves an artifact whose group is `io.mzio`;
2. an active Java/Kotlin source imports or references `io.mzio`;
3. the private local Maven repository or a known proprietary native binary path returns.

The build toolchain is JDK 26 (`gradle/libs.versions.toml`). Compile and test commands must set
`JAVA_HOME` to a JDK 26 installation; the completed migration was validated with Temurin 26.0.2.1.

## Remaining release work

The code migration is complete. Before publishing an MIT-only release, maintainers still need to:

- obtain legal review of the generated dependency report and all artwork/data assets (an automated
  licence allow-list is useful evidence but is not legal advice);
- document the vendor-format capability reduction in release notes and user documentation;
- decide whether separately downloadable vendor integrations belong in independent, clearly
  licensed plugins;
- run platform packaging and GUI/headless smoke tests on Linux, macOS, and Windows, since this
  environment validates Linux only.

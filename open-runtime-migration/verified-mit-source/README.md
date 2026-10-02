# Verified MIT source bundle

This directory is staging material for the open-runtime migration. It is intentionally outside the active build so that the development checkpoint remains explicit and does not claim to compile.

## Provenance

Task-controller and memory-management files:
- source repository: mzmine/mzmine
- source commit: fa4a736986fdbea384daabea549d5fdd933a9a9f
- each staged source file carries the MZmine MIT licence header
- transition evidence: 8d85549908ecf04a7df7b776eab2724b23b429c4 removes these source implementations and introduces local io.mzio binaries

CLI parser:
- source commit: 36f0cd628bd6c78111446cae2788c8ddcb3b28ca
- MZmineArgumentParser.java carries the MZmine MIT licence header
- transition evidence: 0c3b50f91398bf185735ebe043c32006a4507f59 removes the parser and introduces io.mzio:mzmine-core

Workspace source:
- source commit: 3debbc0ec6afff6d2672444906d9fdfb5513626f
- staged workspace files carry the MZmine MIT licence header

## Rule

These files may be adapted into current modules. Do not infer or copy later proprietary implementation details from io.mzio JARs. Newer APIs must be implemented independently from current public call sites/tests.

## Development status

This is a provenance-preserving source checkpoint only. The staged files have not yet been adapted to the 2026 APIs or wired into the Gradle build.

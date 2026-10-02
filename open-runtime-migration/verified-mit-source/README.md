# Verified MIT source bundle

This directory preserves the provenance inputs used for the open-runtime migration. The active,
adapted implementations now live in `taskcontroller` and `utils`.

## Provenance

Task-controller and memory-management files:

- source repository: `mzmine/mzmine`;
- source commit: `fa4a736986fdbea384daabea549d5fdd933a9a9f`;
- every staged source file carries the MZmine MIT licence header;
- transition evidence: `8d85549908ecf04a7df7b776eab2724b23b429c4` removes these source
  implementations and introduces local `io.mzio` binaries.

CLI parser:

- source commit: `36f0cd628bd6c78111446cae2788c8ddcb3b28ca`;
- transition evidence: `0c3b50f91398bf185735ebe043c32006a4507f59`.

Workspace source:

- source commit: `3debbc0ec6afff6d2672444906d9fdfb5513626f`.

## Rule

Do not infer or copy private implementation details from historical `io.mzio` JARs. Newer API
requirements must be implemented independently from public call sites, tests, and public platform
APIs. See `docs/open-runtime-migration-checkpoint.md` for the active implementation and release
boundary.

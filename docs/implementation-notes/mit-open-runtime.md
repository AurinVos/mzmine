# MIT open runtime

## Intention

Make the MZmine application buildable and runnable from its MIT-licensed source without the private
`io.mzio` runtime bundle, account services, licence gates, or committed vendor binaries. Scientific
algorithms remain unchanged; the migration replaces only shared runtime infrastructure and removes
features that existed solely to authenticate or license users.

## Decisions

- Historical task-controller and memory-storage source is used only from commit
  `fa4a736986fdbea384daabea549d5fdd933a9a9f`, where every restored file has an explicit MIT header.
  The storage implementation was independently adapted to JDK 26's foreign-memory API from current
  public call sites rather than from private binaries.
- Task-list events, result values, links, shutdown handling, and the application event bus are small
  MZmine-owned MIT implementations. No private JAR was decompiled or used as implementation input.
- Account/user views, persisted sign-in restoration, account-derived provenance, expiry checks, and
  wizard service restrictions are removed. Every local workflow is available without an account.
- Vendor-native libraries with non-MIT or unclear redistribution terms are not committed or fetched
  by the normal build. Their raw formats require a separately obtained compatible converter/library;
  open formats and the scientific processing runtime remain available.
- `verifyOpenRuntime` is part of `check` and rejects `io.mzio` runtime artifacts/imports and known
  proprietary binary paths.
- JDK 26 is the source/toolchain baseline. Tests must be executed with a JDK 26 `JAVA_HOME`.

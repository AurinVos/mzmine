# Open-runtime WIP source recovery

This directory contains historical MZmine source recovered from verified public commits whose Java files carry explicit MIT headers.

These files are intentionally outside active Gradle source sets. They are a provenance-safe staging area for the open-runtime migration and are not yet a buildable replacement.

See `docs/open-runtime-migration-checkpoint.md` for:
- source commit SHAs;
- transition commits where source was replaced by `io.mzio` binaries;
- the remaining adaptation work;
- the rule not to decompile or reproduce proprietary JAR implementations.

Next step: adapt these sources to the current public APIs, then move them into active modules and remove the corresponding `io.mzio` dependencies.

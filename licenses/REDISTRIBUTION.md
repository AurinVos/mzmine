# Using and redistributing this build

MZmine's own code permits use for any purpose, including commercial analysis.
The combined application is distributed under GPL version 3, with the unresolved
jimzMLParser component described below. The original MIT notices and
third-party license grants remain intact. Running the application and keeping
modifications within your organization do not require public source release.

Thermo software is neither downloaded nor bundled. To import Thermo RAW, select
an independently installed converter in Preferences, under that converter's own
terms, or supply converted mzML. imzML imaging import reads metadata and spectra
from imzML / .ibd pairs using `com.alanmrace:jimzmlparser:1.0.3`.

On 2026-10-03, the repository owner accepted using jimzMLParser despite its
unknown license status. The [decision and supporting evidence](../docs/implementation-notes/imzml-parser-license-decision.md)
are recorded separately. The parser's license remains **UNKNOWN / UNVERIFIED**.
This acceptance does not establish an upstream license, commercial redistribution
permission or GPL compatibility. Do not treat this build as verified for unrestricted
commercial redistribution. The exact artifact is an explicit build-check exception;
its unresolved status is retained in dependency inventories and the
[bundled component notice](third-party/jimzMLParser-1.0.3-UNKNOWN.txt).

## Release procedure

1. Run `gradlew :mzmine-community:checkLicense :mzmine-community:verifyOpenRuntime`.
2. Run the application tests.
3. Run `gradlew :mzmine-community:freeUsePortable`. This builds a fresh application
   image, verifies its runtime JARs and removed payloads, and creates both portable
   and corresponding-source archives in `mzmine-community/build/distributions/`.
   Alternatively, `jpackage` creates an installer with the same release gates.
   The historical `freeUsePortable` task and archive name are retained for build
   compatibility; they do not certify the parser's unresolved license.
   Supply the resulting
   source archive alongside each binary download, at the same location and with
   equivalent access. Include it with physical distributions. Do not publish a
   binary if any required source or build/install instructions are missing.
4. Inspect the final application image and its dependency notices, native
   libraries, JDK legal files and source manifest. A successful metadata check
   alone is not a legal compatibility certification. Confirm that the jimzMLParser
   UNKNOWN / UNVERIFIED disclosure accompanies every binary and source release.

The source archive captures the current tracked application files (including
uncommitted edits), Gradle build/install scripts, local dependency sources and
resolved dependency source artifacts. Source provenance and checksums are recorded.
Missing source artifacts are reported as release blockers rather than silently
omitted. Native dependencies and the bundled JDK require their exact upstream
source/build materials as well; Maven source JARs alone do not cover native code.
The build fetches checksum-pinned full source archives for these components and
aggregate artifacts without Maven source classifiers. Guava's empty placeholder
is checked by hash and verified to contain no executable classes.

The native release manifest currently covers Windows x64 with Temurin
26.0.2.1+1 and OpenJFX 26+27. Other platforms and toolchains must supply matching
source/build metadata before the release gates permit distribution. Application
compilation remains independent of this Windows release profile.

Keep component license texts, copyright notices, NOTICE files and modification
notices. The generated component inventory describes dependencies; it does not
replace their actual license texts. Shared libraries remain separate JARs so users
can replace LGPL components. No restriction on running modified versions is added.

The local GraphStream fork is preserved with its exact source archive and
provenance. Unknown CDK snapshots are replaced by the released CDK 2.11 artifacts.
Unused Colt (which included a no-military-use restriction), JCIP, JUnit 4 and
JJobs dependencies are excluded. Unshaded gRPC uses JDK TLS without native
BoringSSL. JasperReports uses its OpenPDF fallback without Adobe XMP; standard
PDF export is tested, while PDF/A conformance is not verified.

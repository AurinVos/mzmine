# jimzMLParser use with an unknown license

## Intention

Record the repository owner's decision to accept using jimzMLParser despite its
unresolved license status, so future work on imzML import preserves that decision
and the uncertainty behind it.

## Decisions

- On 2026-10-03, the repository owner stated: "I am ok using this parser.
  However, make note of this decision and the unknown license status". This is
  acceptance of using the parser with that uncertainty; it is not a license grant
  from the parser's copyright holders.
- The previously used dependency is `com.alanmrace:jimzmlparser:1.0.3`, from
  [AlanRace/jimzMLParser](https://github.com/AlanRace/jimzMLParser). Its license
  remains **unknown / unverified**. Do not describe it as confirmed proprietary,
  public domain, or licensed for unrestricted commercial use.
- The investigation found no explicit license grant in the published 1.0.3 POM,
  binary JAR or source JAR, or in the 198 reachable upstream commits examined at
  commit `efd9bafca24020e17a5d6ec3c8374dd1396e60c4`. NetBeans template comments
  referring to a license header do not supply a license. Permission for commercial
  use, modification and redistribution has therefore not been established.
- Upstream's latest commit examined was dated 2022-01-21. The repository was not
  archived, but the [license request opened on 2023-12-01](https://github.com/AlanRace/jimzMLParser/issues/4)
  remained open without replies when checked on 2026-10-03. An accidental omission
  is plausible, but neither inactivity nor public source availability establishes
  permission for all purposes.
- Preserve the unknown-license disclosure in dependency inventories, notices and
  release documentation if the parser is restored. The owner's acceptance must
  not be represented as an upstream license or as verified license compatibility.
- Restored the pre-removal reader and imaging metadata constructor, the imzML
  file picker entry, and `com.alanmrace:jimzmlparser:1.0.3`. Existing imaging data
  structures and plotting are reused. Binary read errors fail the import without
  adding an empty project file; cancellation is preserved.
- License normalization gives only this exact coordinate the explicit UNKNOWN /
  UNVERIFIED label. License checking allows that module/version/label combination;
  other unknown dependencies still fail. Runtime and package gates retain other
  exclusions and require the bundled unknown-license notice. Exact source
  collection remains required.
- The parser's native compression dependencies retain their own grants and
  notices. Full Zstd JNI 1.3.4-10 and LZ4 Java 1.4.1 source/build archives are
  pinned in `licenses/native-source-provenance.json`; native source validation
  remains active. The historical `freeUsePortable` name does not certify the
  parser's unknown license.

## Evidence

- [Upstream commit examined](https://github.com/AlanRace/jimzMLParser/commit/efd9bafca24020e17a5d6ec3c8374dd1396e60c4).
- [Published 1.0.3 POM](https://repo.maven.apache.org/maven2/com/alanmrace/jimzmlparser/1.0.3/jimzmlparser-1.0.3.pom)
  and [source JAR](https://repo.maven.apache.org/maven2/com/alanmrace/jimzmlparser/1.0.3/jimzmlparser-1.0.3-sources.jar).
- Parser removal in this fork: `e4fff9e8788c39f0e6cd8d8916bba78730400d2e`.

## Validation

- On Windows x64 with Temurin 26.0.2.1+1, compilation, `checkLicense` and
  `verifyOpenRuntime` passed. The generated dependency inventory contains the
  parser and its UNKNOWN / UNVERIFIED disclosure.
- All seven selected tests passed: five task cases covering continuous/processed
  .ibd spectra and all nine pixel coordinates, absent metadata/binary files and
  cancellation; plus normal/advanced import regression tests for continuous,
  processed and the 19,430-scan centroid fixture. Existing mass values,
  intensities, coordinates and processing statistics are checked by the
  regression suite.
- A fresh Windows portable package and matching corresponding-source archive
  must pass the existing content/source gates before release. The originating
  conversation handles replacing the local application and evaluating its GUI;
  this restoration does not claim interactive imaging plot validation.

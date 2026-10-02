Native source and license material for the Windows x86_64 package

Temurin runtime: Eclipse Adoptium 26.0.2.1+1. The source archive is the exact
vendor release asset, verified against its published SHA256. The included
Windows build metadata supplies configure arguments, tool versions and source
commit. The included temurin-build archive matches BUILD_SOURCE in the JDK
release file. OpenJDK native and Java code are GPL-2.0 with the applicable
Classpath/Assembly exceptions and component notices. Include the runtime legal
directory and this source material alongside the application distribution.

OpenJFX: runtime VersionInfo reports 26+27. The full 26-ga archive resolves to
commit 472e74b1e0c71cfd2b75711a18b108fa154ad618 and includes Java, native code,
WebKit, GStreamer, build scripts, and licenses. Maven sources jars alone do not
cover its native libraries. licenses/native/openjfx-26 contains the source's
LICENSE and all module src/main/legal notices for package inclusion. Native
libraries remain replaceable by replacing their platform-specific JavaFX jars;
rebuild instructions/scripts are in the full archive. The Gluon Maven release
build's precise local command line has not been independently reproduced.

DJL: pytorch-engine 0.26.0 is Apache-2.0 and its embedded properties specify
PyTorch 2.1.1. The engine jar contains no DLL, SO or dylib. It downloads native
libraries on first use; the source archive includes JNI code/build scripts.
Separately downloaded CPU/CUDA/ROCm payloads are outside this distribution's
native-source inventory. Do not redistribute them without auditing their
component licenses and collecting any required notices/source material.

JNA 5.13.0 is available under Apache-2.0 or LGPL-2.1-or-later. Selecting its
Apache option avoids LGPL corresponding-source obligations for JNA itself;
retain its Apache/libffi notices. jna-inchi 1.3.1 has an LGPL-2.1-or-later Java wrapper, whose exact source
and Maven build are included. It ships InChI 1.07.2 native binaries under MIT;
the exact InChI source/build archive is also included. Preserve both grants.

The acquisition manifest pins only this Windows package. A different JDK,
JavaFX version, platform or native payload requires a new manifest and audit.
Run config/fetch-native-sources.ps1 to fetch and verify each pinned artifact.
The -VerifyOnly switch performs read-only verification and fails on absent or
mismatched files. Source bundle release checks must require these files.

SQLite JDBC3.41.2.2 wrapper/build source and exact SQLite3.41.2 amalgamation,
JNA5.13 full native/libffi source, LZ4Java1.4.1 and ZstdJNI1.3.4-10 full native
source, and Netty4.1.123/4.1.124 native transport/DNS build/source are pinned.
Netcdf4 does not embed native binaries; it uses separately installed libraries.
The shaded gRPC artifact embedded GPL-incompatible OpenSSL/SSLeay BoringSSL
terms. The build is being switched to nonshaded grpc-netty with tcnative
excluded; BoringSSL/tcnative/APR material is deliberately absent from this
manifest. Fresh package contents must verify this exclusion before release.
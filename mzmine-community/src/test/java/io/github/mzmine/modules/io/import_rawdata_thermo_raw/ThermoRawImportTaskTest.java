/*
 * Copyright (c) 2026 The MZmine Development Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.mzmine.modules.io.import_rawdata_thermo_raw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.sun.jna.Platform;
import io.github.mzmine.datamodel.MZmineProject;
import io.github.mzmine.gui.preferences.MZminePreferences;
import io.github.mzmine.main.ConfigService;
import io.github.mzmine.modules.io.import_rawdata_all.AllSpectralDataImportParameters;
import io.github.mzmine.modules.io.import_rawdata_all.spectral_processor.ScanImportProcessorConfig;
import io.github.mzmine.taskcontrol.TaskStatus;
import io.github.mzmine.util.files.FileAndPathUtil;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ThermoRawImportTaskTest {

  @TempDir
  Path temporaryDirectory;

  @Test
  void cachedConverterIsNeverUsedWithoutExplicitPreference() throws Exception {
    Path cachedDirectory = Files.createDirectories(
        temporaryDirectory.resolve("external_tools/thermo_raw_file_parser"));
    Path cachedParser = Files.createFile(cachedDirectory.resolve(
        Platform.isWindows() ? "ThermoRawFileParser.exe" : "ThermoRawFileParser"));
    cachedParser.toFile().setExecutable(true);

    // Simulate a cache left by an older build. Import must not even resolve that cache.
    try (var paths = mockStatic(FileAndPathUtil.class, CALLS_REAL_METHODS)) {
      paths.when(() -> FileAndPathUtil.resolveInExternalToolsDir(anyString()))
          .thenReturn(cachedDirectory.toFile());
      // CALLS_REAL_METHODS records the stubbing call itself; only verify import-time lookups.
      paths.clearInvocations();
      assertImportFails(Optional.empty(), "separately installed ThermoRawFileParser");
      paths.verify(() -> FileAndPathUtil.resolveInExternalToolsDir(
          argThat(path -> path != null && path.contains("thermo_raw_file_parser"))), never());
    }
  }

  @Test
  void missingSelectedConverterFailsBeforeImportingData() {
    assertImportFails(Optional.of(temporaryDirectory.resolve("missing-parser").toFile()),
        "executable is missing or cannot be executed");
  }

  @Test
  void selectedDirectoryIsNotAnExecutable() {
    assertImportFails(Optional.of(temporaryDirectory.toFile()),
        "executable is missing or cannot be executed");
  }

  private void assertImportFails(Optional<File> parser, String diagnostic) {
    var preferences = mock(MZminePreferences.class);
    when(preferences.getOptionalValue(MZminePreferences.thermoRawFileParserPath))
        .thenReturn(parser);
    var project = mock(MZmineProject.class);
    var parameters = mock(AllSpectralDataImportParameters.class);

    try (var configuration = mockStatic(ConfigService.class)) {
      configuration.when(ConfigService::getPreferences).thenReturn(preferences);
      var task = new ThermoRawImportTask(null, project,
          temporaryDirectory.resolve("input.raw").toFile(), ThermoRawImportModule.class,
          parameters, Instant.EPOCH, ScanImportProcessorConfig.createDefault());
      task.run();

      assertEquals(TaskStatus.ERROR, task.getStatus());
      assertTrue(task.getErrorMessage().contains(diagnostic), task.getErrorMessage());
      assertTrue(task.getImportedRawDataFiles().isEmpty());
      verifyNoInteractions(project);
    }
  }
}

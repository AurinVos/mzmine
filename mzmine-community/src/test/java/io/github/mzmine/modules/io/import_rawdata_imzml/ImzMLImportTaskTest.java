/*
 * Copyright (c) 2004-2024 The mzmine Development Team
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.mzmine.modules.io.import_rawdata_imzml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.mzmine.datamodel.ImagingRawDataFile;
import io.github.mzmine.datamodel.ImagingScan;
import io.github.mzmine.modules.io.import_rawdata_all.spectral_processor.ScanImportProcessorConfig;
import io.github.mzmine.project.impl.MZmineProjectImpl;
import io.github.mzmine.taskcontrol.TaskStatus;
import java.io.File;
import java.time.Instant;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ImzMLImportTaskTest {

  @Test
  void missingMetadataFailsWithoutAddingAnEmptyFile() {
    var project = new MZmineProjectImpl();
    var task = createTask(project);

    task.run();

    assertEquals(TaskStatus.ERROR, task.getStatus());
    assertTrue(task.getErrorMessage().contains("Error parsing imzML"));
    assertTrue(task.getImportedRawDataFiles().isEmpty());
    assertEquals(0, project.getDataFiles().length);
  }

  @ParameterizedTest
  @ValueSource(strings = {"Example_Continuous", "Example_Processed"})
  void readsBinarySpectraAndCoordinates(String fixture) throws Exception {
    var project = new MZmineProjectImpl();
    var task = createTask(project, fixtureFile(fixture));
    task.run();

    assertEquals(TaskStatus.FINISHED, task.getStatus(), task.getErrorMessage());
    var raw = (ImagingRawDataFile) task.getImportedRawDataFiles().getFirst();
    assertEquals(1, project.getDataFiles().length);
    assertEquals(9, raw.getScans().size());
    assertEquals(3, raw.getImagingParam().getMaxNumberOfPixelX());
    assertEquals(3, raw.getImagingParam().getMaxNumberOfPixelY());
    for (int i = 0; i < 9; i++) {
      var scan = (ImagingScan) raw.getScans().get(i);
      assertEquals(new Coordinates(i % 3, i / 3, 0), scan.getCoordinates());
      assertEquals(8399, scan.getNumberOfDataPoints());
      assertEquals(100.08333587646484, scan.getMzValue(0), 1e-6);
      assertEquals(799.9166870117188, scan.getMzValue(8398), 1e-6);
      assertTrue(scan.getTIC() > 0);
    }
    raw.close();
  }

  @Test
  void missingBinaryFailsWithoutAddingAFile(@TempDir Path directory) throws Exception {
    var metadata = directory.resolve("Example_Continuous.imzML");
    Files.copy(fixtureFile("Example_Continuous").toPath(), metadata);
    var project = new MZmineProjectImpl();
    var task = createTask(project, metadata.toFile());
    task.run();
    assertEquals(TaskStatus.ERROR, task.getStatus());
    assertTrue(task.getImportedRawDataFiles().isEmpty());
    assertEquals(0, project.getDataFiles().length);
  }

  private static File fixtureFile(String name) throws Exception {
    return new File(ImzMLImportTaskTest.class.getClassLoader()
        .getResource("rawdatafiles/additional/" + name + ".imzML").toURI());
  }

  @Test
  void canceledImportStaysCanceled() {
    var task = createTask(new MZmineProjectImpl());
    task.cancel();

    task.run();

    assertEquals(TaskStatus.CANCELED, task.getStatus());
    assertTrue(task.getImportedRawDataFiles().isEmpty());
  }

  private static ImzMLImportTask createTask(MZmineProjectImpl project) {
    return createTask(project, new File("missing.imzML"));
  }

  private static ImzMLImportTask createTask(MZmineProjectImpl project, File file) {
    return new ImzMLImportTask(project, file,
        ScanImportProcessorConfig.createDefault(), ImzMLImportModule.class,
        new ImzMLImportParameters(), Instant.EPOCH, null);
  }
}

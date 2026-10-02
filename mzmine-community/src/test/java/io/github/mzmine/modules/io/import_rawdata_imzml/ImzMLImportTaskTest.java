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

import io.github.mzmine.modules.io.import_rawdata_all.spectral_processor.ScanImportProcessorConfig;
import io.github.mzmine.project.impl.MZmineProjectImpl;
import io.github.mzmine.taskcontrol.TaskStatus;
import java.io.File;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ImzMLImportTaskTest {

  @Test
  void savedBatchFailsClearlyWithoutAddingAnEmptyFile() {
    var project = new MZmineProjectImpl();
    var task = createTask(project);

    task.run();

    assertEquals(TaskStatus.ERROR, task.getStatus());
    assertEquals(ImzMLImportTask.UNAVAILABLE_MESSAGE, task.getErrorMessage());
    assertTrue(task.getImportedRawDataFiles().isEmpty());
    assertEquals(0, project.getDataFiles().length);
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
    return new ImzMLImportTask(project, new File("unavailable.imzML"),
        ScanImportProcessorConfig.createDefault(), ImzMLImportModule.class,
        new ImzMLImportParameters(), Instant.EPOCH, null);
  }
}

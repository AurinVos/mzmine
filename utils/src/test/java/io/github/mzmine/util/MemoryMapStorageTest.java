/*
 * Copyright (c) 2004-2026 The mzmine Development Team
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
package io.github.mzmine.util;

import static java.lang.foreign.ValueLayout.JAVA_DOUBLE;
import static java.lang.foreign.ValueLayout.JAVA_FLOAT;
import static java.lang.foreign.ValueLayout.JAVA_INT;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class MemoryMapStorageTest {
  @AfterEach
  void restoreDiskStorage() { MemoryMapStorage.setStoreAllInRam(false); }

  @Test
  void storesPrimitiveArraysInReadOnlyMappedSegments() throws IOException {
    var before = MemoryMapStorageStats.snapshot();
    var storage = MemoryMapStorage.create();
    var doubles = storage.storeData(new double[]{1.25, 2.5});
    var floats = storage.storeData(new float[]{3.5f});
    var ints = storage.storeData(new int[]{4, 5});

    assertTrue(doubles.isReadOnly());
    assertEquals(2.5, doubles.getAtIndex(JAVA_DOUBLE, 1));
    assertEquals(3.5f, floats.getAtIndex(JAVA_FLOAT, 0));
    assertEquals(5, ints.getAtIndex(JAVA_INT, 1));
    var after = MemoryMapStorageStats.snapshot();
    assertEquals(3, after.totalFilesCreated() - before.totalFilesCreated());
    assertEquals(2L * Double.BYTES + Float.BYTES + 2L * Integer.BYTES,
        after.totalUsedBytes() - before.totalUsedBytes());

    storage.discard(null);
    assertEquals(0, storage.getTemporaryFileCount());
  }

  @Test
  void ramModeReturnsNoStorage() {
    MemoryMapStorage.setStoreAllInRam(true);
    assertNull(MemoryMapStorage.forFeatureList());
    assertNull(MemoryMapStorage.forRawDataFile());
    assertNull(MemoryMapStorage.forMassList());
  }
}

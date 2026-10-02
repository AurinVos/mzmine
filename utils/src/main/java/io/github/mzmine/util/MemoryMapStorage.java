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

import java.io.IOException;
import io.github.mzmine.util.files.FileAndPathUtil;
import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

/**
 * Owns temporary, file-backed memory segments used by data models. This implementation is adapted
 * from the MIT-licensed historical MZmine storage at commit
 * {@code fa4a736986fdbea384daabea549d5fdd933a9a9f} to the Java foreign-memory API.
 */
public final class MemoryMapStorage {
  private static final AtomicLong TOTAL_FILES_CREATED = new AtomicLong();
  private static final AtomicLong TOTAL_RESERVED_BYTES = new AtomicLong();
  private static final AtomicLong TOTAL_USED_BYTES = new AtomicLong();
  private static volatile boolean storeFeaturesInRam;
  private static volatile boolean storeRawFilesInRam;
  private static volatile boolean storeMassListsInRam;

  private final List<Path> temporaryFiles = new ArrayList<>();
  private final List<Arena> arenas = new ArrayList<>();
  private long usedBytes;

  private MemoryMapStorage() { MemoryMapStorages.registerStorage(this); }

  public enum Source { RAW, MASS_LISTS, FEATURE_LIST }

  public static @Nullable MemoryMapStorage forSource(Source source) {
    return switch (source) {
      case RAW -> forRawDataFile();
      case MASS_LISTS -> forMassList();
      case FEATURE_LIST -> forFeatureList();
    };
  }
  public static @Nullable MemoryMapStorage forFeatureList() { return storeFeaturesInRam ? null : create(); }
  public static @Nullable MemoryMapStorage forRawDataFile() { return storeRawFilesInRam ? null : create(); }
  public static @Nullable MemoryMapStorage forMassList() { return storeMassListsInRam ? null : create(); }
  public static @NotNull MemoryMapStorage create() { return new MemoryMapStorage(); }
  public static boolean isStoreFeaturesInRam() { return storeFeaturesInRam; }
  public static void setStoreFeaturesInRam(boolean value) { storeFeaturesInRam = value; }
  public static boolean isStoreRawFilesInRam() { return storeRawFilesInRam; }
  public static void setStoreRawFilesInRam(boolean value) { storeRawFilesInRam = value; }
  public static boolean isStoreMassListsInRam() { return storeMassListsInRam; }
  public static void setStoreMassListsInRam(boolean value) { storeMassListsInRam = value; }
  public static void setStoreAllInRam(boolean value) {
    storeFeaturesInRam = value; storeRawFilesInRam = value; storeMassListsInRam = value;
  }

  public synchronized MemorySegment storeData(double[] data) { return storeData(data, 0, data.length); }
  public synchronized MemorySegment storeData(double[] data, int offset, int length) {
    MemorySegment segment = allocateMemorySegment(ValueLayout.JAVA_DOUBLE, length);
    MemorySegment.copy(data, offset, segment, ValueLayout.JAVA_DOUBLE, 0, length);
    return segment.asReadOnly();
  }
  public synchronized MemorySegment storeData(float[] data) { return storeData(data, 0, data.length); }
  public synchronized MemorySegment storeData(float[] data, int offset, int length) {
    MemorySegment segment = allocateMemorySegment(ValueLayout.JAVA_FLOAT, length);
    MemorySegment.copy(data, offset, segment, ValueLayout.JAVA_FLOAT, 0, length);
    return segment.asReadOnly();
  }
  public synchronized MemorySegment storeData(int[] data) { return storeData(data, 0, data.length); }
  public synchronized MemorySegment storeData(int[] data, int offset, int length) {
    MemorySegment segment = allocateMemorySegment(ValueLayout.JAVA_INT, length);
    MemorySegment.copy(data, offset, segment, ValueLayout.JAVA_INT, 0, length);
    return segment.asReadOnly();
  }

  public synchronized MemorySegment allocateMemorySegment(MemoryLayout layout, int count) {
    final long bytes = Math.multiplyExact(layout.byteSize(), count);
    if (bytes == 0) return MemorySegment.NULL.asSlice(0, 0);
    try {
      Path file = FileAndPathUtil.createTempFile("mzmine", ".tmp").toPath();
      Arena arena = Arena.ofShared();
      MemorySegment segment;
      try (FileChannel channel = FileChannel.open(file, StandardOpenOption.READ,
          StandardOpenOption.WRITE)) {
        segment = channel.map(FileChannel.MapMode.READ_WRITE, 0, bytes, arena);
      }
      temporaryFiles.add(file);
      arenas.add(arena);
      usedBytes += bytes;
      TOTAL_FILES_CREATED.incrementAndGet();
      TOTAL_RESERVED_BYTES.addAndGet(bytes);
      TOTAL_USED_BYTES.addAndGet(bytes);
      file.toFile().deleteOnExit();
      return segment;
    } catch (IOException e) {
      throw new IllegalStateException("Cannot allocate mapped storage", e);
    }
  }

  public synchronized void discard(Unsafe ignored) throws IOException {
    arenas.forEach(Arena::close);
    arenas.clear();
    for (Path file : temporaryFiles) Files.deleteIfExists(file);
    temporaryFiles.clear();
    usedBytes = 0;
  }
  public static long getTotalFilesCreated() { return TOTAL_FILES_CREATED.get(); }
  public static long getTotalReservedBytes() { return TOTAL_RESERVED_BYTES.get(); }
  public static long getTotalUsedBytes() { return TOTAL_USED_BYTES.get(); }
  public synchronized long getTemporaryFileCount() { return temporaryFiles.size(); }
  public synchronized long getUsedBytes() { return usedBytes; }
}

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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.mzmine.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import org.junit.jupiter.api.Test;

class MZmineArgumentParserTest {

  @Test
  void parsesRuntimeOptions() {
    final var parser = new MZmineArgumentParser(new String[]{"--batch", "workflow.mzbatch",
        "--pref", "preferences.xml", "--temp", "tmp", "--threads", "8", "--memory", "all",
        "--running", "--tdfpseudoprofile", "--ignore-parameter-warnings"});

    assertEquals(new File("workflow.mzbatch"), parser.getBatchFile());
    assertEquals(new File("preferences.xml"), parser.getPreferencesFile());
    assertEquals(new File("tmp"), parser.getTempDirectory());
    assertEquals("8", parser.getNumCores());
    assertEquals("all", parser.isKeepInMemory());
    assertTrue(parser.isKeepRunningAfterBatch());
    assertTrue(parser.isLoadTdfPseudoProfile());
    assertTrue(parser.isIgnoreParameterWarnings());
  }

  @Test
  void parsesBatchOverrides() {
    final var parser = new MZmineArgumentParser(new String[]{"--metadata", "metadata.tsv",
        "--output", "results/run", "--project", "input.mzmine", "--database", "compounds.csv"});

    assertEquals(new File("metadata.tsv"), parser.getMetadataFile());
    assertEquals("results/run", parser.getOutBaseFile());
    assertEquals(new File("input.mzmine"), parser.getProjectImport());
    assertEquals(new File("compounds.csv"), parser.getCsvDatabase());
    assertFalse(parser.isKeepRunningAfterBatch());
  }

  @Test
  void leavesAbsentOptionsUnset() {
    final var parser = new MZmineArgumentParser(new String[]{});

    assertNull(parser.getBatchFile());
    assertNull(parser.getOverrideDataFiles());
    assertNull(parser.getOverrideSpectralLibrariesFiles());
    assertNull(parser.isKeepInMemory());
  }

  @Test
  void rejectsUnknownOptions() {
    assertThrows(IllegalArgumentException.class,
        () -> new MZmineArgumentParser(new String[]{"--not-an-option"}));
  }
}

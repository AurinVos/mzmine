/*
 * Copyright (c) 2004-2026 The mzmine Development Team
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

package io.github.mzmine.main;

import io.github.mzmine.gui.preferences.MZminePreferences;
import io.github.mzmine.util.StringUtils;
import io.github.mzmine.util.files.FileAndPathUtil;
import io.mzio.mzmine.startup.MZmineCoreArgumentParser;
import io.mzio.mzmine.startup.MZmineExit;
import java.io.File;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Helper class to apply all parsed args from the
 * {@link io.mzio.mzmine.startup.MZmineCoreArgumentParser} to the {@link ConfigService}.
 */
class ArgsToConfigUtils {

  private static final Logger logger = Logger.getLogger(ArgsToConfigUtils.class.getName());

  /**
   * Parses all relevant arguments from the given program arguments and initialises the
   * {@link MZmineCoreArgumentParser} instance in this class.
   *
   * @param argsParser The args parser
   */
  static void applyArgsToConfig(final MZmineCoreArgumentParser argsParser) {
    ConfigService.setTdfPseudoProfile(argsParser.isLoadTdfPseudoProfile());

    checkAndLoadArgsConfiguration(argsParser);
    TmpFileCleanup.runCleanup(); // clean old temp files in old dir

    // parse args temp dir after config was loaded, so we can override
    checkAndOverrideArgsTempDir(argsParser);
    applyTempDirFromConfiguration();
    TmpFileCleanup.runCleanup(); // clean temp files in new dir

    // Saved accounts are not restored during local startup.

    checkAndOverrideArgsMemoryOption(argsParser);

    setNumThreadsOverride(argsParser);

    // Account-related CLI options do not gate or alter local processing.

    ConfigService.setIgnoreParameterWarningsInBatch(argsParser.isIgnoreParameterWarnings());
  }

  static void checkAndOverrideArgsTempDir(MZmineCoreArgumentParser argsParser) {
    // override temp directory
    final File tempDirectory = argsParser.getTempDirectory();
    if (tempDirectory != null) {
      // needs to be accessible
      if (FileAndPathUtil.createDirectory(tempDirectory)) {
        ConfigService.getPreferences().setParameter(MZminePreferences.tempDirectory, tempDirectory);
      } else {
        logger.log(Level.WARNING,
            "Cannot create or access temp file directory that was set via program argument: "
                + tempDirectory.getAbsolutePath());
      }
    }
  }

  static void checkAndOverrideArgsMemoryOption(@NotNull final MZmineCoreArgumentParser argsParser) {
    KeepInMemory keepInMemory;
    try {
      var memory = argsParser.isKeepInMemory();
      if (StringUtils.hasValue(memory)) {
        keepInMemory = KeepInMemory.parse(memory);

        // set to preferences
        ConfigService.getPreferences().setParameter(MZminePreferences.memoryOption, keepInMemory);
      } else {
        keepInMemory = ConfigService.getPreferences().getParameter(MZminePreferences.memoryOption)
            .getValue();
      }
    } catch (Exception exception) {
      logger.warning("Issue while reading keep in memory option from CLI argument");
      MZmineExit.exit(1);
      return;
    }

    if (keepInMemory == null) {
      keepInMemory = KeepInMemory.NONE;
    }

    // apply memory management option
    keepInMemory.enforceToMemoryMapping();
  }

  static void checkAndLoadArgsConfiguration(@NotNull final MZmineCoreArgumentParser argsParser) {
    // override preferences file by command line argument pref
    final File prefFile = Objects.requireNonNullElse(argsParser.getPreferencesFile(),
        MZmineConfiguration.CONFIG_FILE);
    if ("null".equals(prefFile.getName())) {
      logger.info("Preference file was set to null, not loading configuration.");
      return;
    }

    // Load configuration
    if (prefFile.exists() && prefFile.canRead()) {
      try {
        ConfigService.getConfiguration().loadConfiguration(prefFile, true);
      } catch (Exception e) {
        logger.log(Level.WARNING, "Error while reading configuration " + prefFile.getAbsolutePath(),
            e);
      }
    } else {
      logger.log(Level.WARNING, "Cannot read configuration " + prefFile.getAbsolutePath());
    }
  }

  /**
   * Set number of cores to automatic or to fixed number
   */
  static void setNumThreadsOverride(@NotNull final MZmineCoreArgumentParser argsParser) {
    final String numCores = argsParser.getNumCores();
    if (numCores != null) {
      // set to preferences
      var parameter = ConfigService.getPreferences().getParameter(MZminePreferences.numOfThreads);
      if (numCores.equalsIgnoreCase("auto") || numCores.equalsIgnoreCase("automatic")) {
        parameter.setAutomatic(true);
      } else {
        try {
          parameter.setValue(Integer.parseInt(numCores));
        } catch (Exception ex) {
          logger.log(Level.SEVERE,
              "Cannot parse command line argument threads (int) set to " + numCores);
          throw new IllegalArgumentException("numCores was set to " + numCores, ex);
        }
      }
    }
  }

  static void applyTempDirFromConfiguration() {
    final File tempDir = ConfigService.getConfiguration().getPreferences()
        .getParameter(MZminePreferences.tempDirectory).getValue();
    if (tempDir == null) {
      logger.warning(
          () -> "Invalid temporary directory. Defaulting to system temp directory. %s".formatted(
              System.getProperty("java.io.tmpdir")));
      return;
    }

    if (!tempDir.exists()) {
      if (!tempDir.mkdirs()) {
        logger.warning(
            () -> "Could not create temporary directory %s. Defaulting to system temp directory. %s".formatted(
                tempDir.getAbsolutePath(), System.getProperty("java.io.tmpdir")));
        return;
      }
    }

    if (tempDir.isDirectory()) {
      FileAndPathUtil.setTempDir(tempDir.getAbsoluteFile());
      logger.finest(() -> "Default temporary directory is %s".formatted(
          System.getProperty("java.io.tmpdir")));
      logger.finest(() -> "Working temporary directory is %s".formatted(
          FileAndPathUtil.getTempDir().toString()));
    }
  }
}

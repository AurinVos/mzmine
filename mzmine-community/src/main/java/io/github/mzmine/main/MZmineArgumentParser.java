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

import io.github.mzmine.util.files.FileAndPathUtil;
import io.github.mzmine.util.io.SemverVersionReader;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jetbrains.annotations.Nullable;

/**
 * Parses MZmine command-line arguments without relying on the bundled proprietary runtime.
 *
 * <p>This is adapted from the MIT-licensed parser at commit
 * {@code 36f0cd628bd6c78111446cae2788c8ddcb3b28ca}. The newer batch override options are an
 * independent implementation based on the public MZmine call sites.
 */
public final class MZmineArgumentParser {

  private static final Logger logger = Logger.getLogger(MZmineArgumentParser.class.getName());

  private final Options options = createOptions();
  private @Nullable File batchFile;
  private @Nullable File[] overrideDataFiles;
  private @Nullable File[] overrideSpectralLibrariesFiles;
  private @Nullable File preferencesFile;
  private @Nullable File tempDirectory;
  private @Nullable File metadataFile;
  private @Nullable File csvDatabase;
  private @Nullable File projectImport;
  private @Nullable String outBaseFile;
  private @Nullable String keepInMemory;
  private @Nullable String numCores;
  private boolean keepRunningAfterBatch;
  private boolean loadTdfPseudoProfile;
  private boolean ignoreParameterWarnings;

  public MZmineArgumentParser(String[] args) {
    parse(args);
  }

  private static Options createOptions() {
    final Options options = new Options();
    options.addOption("h", "help", false, "print help and exit");
    options.addOption("v", "version", false, "print the MZmine version and exit");
    options.addOption("b", "batch", true, "batch mode file");
    options.addOption("i", "input", true,
        "input files: a glob expression or a text file containing one path per line");
    options.addOption("l", "libraries", true,
        "spectral libraries: a glob expression or a text file containing one path per line");
    options.addOption("p", "pref", true, "preferences file (use 'null' to skip loading one)");
    options.addOption("t", "temp", true, "temporary directory");
    options.addOption("r", "running", false, "keep MZmine running after a headless batch");
    options.addOption("m", "memory", true,
        "objects to keep in memory: none, all, features, centroids, raw, or masses_features");
    options.addOption(Option.builder().longOpt("threads").hasArg()
        .desc("number of processing threads, or 'auto'").build());
    options.addOption(Option.builder().longOpt("tdfpseudoprofile")
        .desc("load pseudo-profile frame spectra from TDF files").build());
    options.addOption(Option.builder().longOpt("metadata").hasArg()
        .desc("metadata file overriding the batch definition").build());
    options.addOption(Option.builder("o").longOpt("output").hasArg()
        .desc("base output path overriding batch export paths").build());
    options.addOption(Option.builder().longOpt("project").hasArg()
        .desc("project file overriding the batch project import").build());
    options.addOption(Option.builder().longOpt("database").hasArg()
        .desc("CSV database overriding the batch database import").build());
    options.addOption(Option.builder().longOpt("ignore-parameter-warnings")
        .desc("continue a batch despite parameter warnings").build());
    return options;
  }

  private void parse(String[] args) {
    final CommandLine commandLine;
    try {
      commandLine = new DefaultParser().parse(options, args);
    } catch (ParseException e) {
      new HelpFormatter().printHelp("MZmine", options, true);
      throw new IllegalArgumentException("Wrong command-line arguments: " + e.getMessage(), e);
    }

    if (commandLine.hasOption("help")) {
      new HelpFormatter().printHelp("MZmine", options, true);
      System.exit(0);
    }
    if (commandLine.hasOption("version")) {
      logger.info(() -> "MZmine version: " + SemverVersionReader.getMZmineVersion());
      System.exit(0);
    }

    batchFile = fileOption(commandLine, "batch");
    preferencesFile = fileOption(commandLine, "pref");
    tempDirectory = fileOption(commandLine, "temp");
    metadataFile = fileOption(commandLine, "metadata");
    projectImport = fileOption(commandLine, "project");
    csvDatabase = fileOption(commandLine, "database");
    outBaseFile = commandLine.getOptionValue("output");
    keepInMemory = commandLine.getOptionValue("memory");
    numCores = commandLine.getOptionValue("threads");
    keepRunningAfterBatch = commandLine.hasOption("running");
    loadTdfPseudoProfile = commandLine.hasOption("tdfpseudoprofile");
    ignoreParameterWarnings = commandLine.hasOption("ignore-parameter-warnings");
    overrideDataFiles = parseFiles(commandLine.getOptionValue("input"), "input data");
    overrideSpectralLibrariesFiles = parseFiles(commandLine.getOptionValue("libraries"),
        "spectral library");
  }

  private static @Nullable File fileOption(CommandLine commandLine, String option) {
    final String value = commandLine.getOptionValue(option);
    return value == null ? null : new File(value);
  }

  private static @Nullable File[] parseFiles(@Nullable String value, String description) {
    if (value == null) {
      return null;
    }
    try {
      return FileAndPathUtil.parseFileInputArgument(value);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Could not resolve " + description + " files from: " + value, e);
      throw new IllegalArgumentException("Could not resolve " + description + " files", e);
    }
  }

  public @Nullable String getNumCores() {
    return numCores;
  }

  public @Nullable File getMetadataFile() {
    return metadataFile;
  }

  public @Nullable File getTempDirectory() {
    return tempDirectory;
  }

  public @Nullable File getPreferencesFile() {
    return preferencesFile;
  }

  public @Nullable File getBatchFile() {
    return batchFile;
  }

  public boolean isKeepRunningAfterBatch() {
    return keepRunningAfterBatch;
  }

  public @Nullable String isKeepInMemory() {
    return keepInMemory;
  }

  public boolean isLoadTdfPseudoProfile() {
    return loadTdfPseudoProfile;
  }

  public @Nullable File[] getOverrideDataFiles() {
    return overrideDataFiles;
  }

  public @Nullable File[] getOverrideSpectralLibrariesFiles() {
    return overrideSpectralLibrariesFiles;
  }

  public @Nullable String getOutBaseFile() {
    return outBaseFile;
  }

  public boolean isIgnoreParameterWarnings() {
    return ignoreParameterWarnings;
  }

  public @Nullable File getCsvDatabase() {
    return csvDatabase;
  }

  public @Nullable File getProjectImport() {
    return projectImport;
  }
}

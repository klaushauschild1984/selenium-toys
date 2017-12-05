/*
 * Selenium Toys Copyright (C) 2017 Klaus Hauschild
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package org.openqa.selenium.toys;

import static org.openqa.selenium.chrome.ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY;
import static org.openqa.selenium.chrome.ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY;
import static org.openqa.selenium.chrome.ChromeDriverService.CHROME_DRIVER_VERBOSE_LOG_PROPERTY;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.BrowserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.PatternFilenameFilter;

public class ChromeWebDriverFactory extends AbstractDownloadingWebDriverFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(ChromeWebDriverFactory.class);

  private static final String DOWNLOAD_URL = "http://chromedriver.storage.googleapis.com";
  private static final String LATEST_RELEASE_URL = DOWNLOAD_URL + "/LATEST_RELEASE";

  public ChromeWebDriverFactory() {
    super( //
        BrowserType.CHROME, //
        (targetDirectory) -> {
          final String extension = getExecutableExtension(true);
          final Pattern executablePattern =
              Pattern.compile("chromedriver-(?<version>.*)" + extension);
          final File[] files =
              targetDirectory.listFiles(new PatternFilenameFilter(executablePattern));
          if (files == null || files.length != 1) {
            return Optional.empty();
          }
          final File executable = files[0];
          final Matcher executableMatcher = executablePattern.matcher(executable.getName());
          if (!executableMatcher.find()) {
            throw new IllegalStateException(
                String.format("Unable to determine version of executable %s", executable));
          }
          final String version = executableMatcher.group("version");
          return Optional
              .of(new DownloadWebDriverExecutable.WebDriverExecutable(executable, version));
        }, //
        () -> getString(LATEST_RELEASE_URL), //
        (version, targetDirectory) -> {
          final String system;
          if (SystemUtils.IS_OS_WINDOWS) {
            system = "win32";
          } else if (SystemUtils.IS_OS_MAC) {
            system = "mac64";
          } else if (SystemUtils.IS_OS_LINUX) {
            final StringBuilder linuxBuilder = new StringBuilder("linux");
            // TODO this property does not reflect the OS architecture, it is the VM`s "bitness"
            if (SystemUtils.OS_ARCH.contains("64")) {
              linuxBuilder.append("64");
            } else {
              linuxBuilder.append("32");
            }
            system = linuxBuilder.toString();
          } else {
            throw new UnsupportedOperationException(
                String.format("Unsupported operation system: %s %s", SystemUtils.OS_NAME,
                    SystemUtils.OS_VERSION));
          }
          LOGGER.debug("System '{}' detected.", system);
          final String downloadUrl =
              DOWNLOAD_URL + String.format("/%s/chromedriver_%s.zip", version, system);
          LOGGER.debug("Download chromedriver from {}", downloadUrl);
          downloadZipAndExtract(downloadUrl, targetDirectory);
          final File chromeDriverFile = new File(targetDirectory,
              String.format("chromedriver%s", getExecutableExtension(false)));
          final File chromeDriverFileWithVersion = new File(targetDirectory,
              String.format("chromedriver-%s%s", version, getExecutableExtension(false)));
          if (!chromeDriverFile.renameTo(chromeDriverFileWithVersion)) {
            throw new RuntimeException();
          }
          if (!chromeDriverFileWithVersion.setExecutable(true, false)) {
            throw new RuntimeException();
          }
          return new DownloadWebDriverExecutable.WebDriverExecutable(chromeDriverFileWithVersion,
              version);
        }, //
        ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY //
    );
  }

  private static String getExecutableExtension(final boolean forRegex) {
    if (SystemUtils.IS_OS_WINDOWS) {
      final String exe = ".exe";
      if (forRegex) {
        return "\\" + exe;
      }
      return exe;
    }
    return "";
  }

  @Override
  protected void beforeInitialization(final Map<String, Object> options) {
    if (!LOGGER.isDebugEnabled()) {
      System.setProperty(CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, Boolean.TRUE.toString());
      return;
    }
    final File workDirectory = getWorkDirectory(options);
    final File logFile = new File(workDirectory, "chromedriver.log");
    try {
      logFile.getParentFile().mkdirs();
      logFile.createNewFile();
    } catch (final IOException exception) {
      throw new RuntimeException(String.format("Unable to create %s", logFile));
    }
    logFile.deleteOnExit();
    System.setProperty(CHROME_DRIVER_LOG_PROPERTY, logFile.getAbsolutePath());
    if (LOGGER.isTraceEnabled()) {
      System.setProperty(CHROME_DRIVER_VERBOSE_LOG_PROPERTY, Boolean.TRUE.toString());
    }
  }

  @Override
  protected WebDriver instantiateWebDriver(final Map<String, Object> options) {
    return new ChromeDriver();
  }

}

package org.openqa.selenium.toys.factory.chrome;

import static org.openqa.selenium.remote.BrowserType.CHROME;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.toys.Webdriver;
import org.openqa.selenium.toys.factory.AbstractWebdriverFactory;
import org.openqa.selenium.toys.factory.DownloadUtils;
import org.openqa.selenium.toys.factory.WebdriverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.PatternFilenameFilter;

/**
 * {@link WebdriverFactory} implementation for Google Chrome using the {@link ChromeDriver}.<br />
 * As additional initialization step the latest release of the chrome driver will be downloaded from
 * <a href=
 * "http://chromedriver.storage.googleapis.com">http://chromedriver.storage.googleapis.com</a>.
 */
public class ChromeWebdriverFactory extends AbstractWebdriverFactory {

  public static final String WORK_DIRECTORY = CHROME + "_workDirectory";
  public static final String EXPECTED_VERSION = CHROME + "_expectedVersion";
  public static final Pattern EXECUTABLE_PATTERN =
      Pattern.compile(String.format("chromedriver-(?<version>.*)%s", getExecutableExtension(true)));
  private static final Logger LOGGER = LoggerFactory.getLogger(ChromeWebdriverFactory.class);
  private static final String DOWNLOAD_URL = "http://chromedriver.storage.googleapis.com";
  private static final String LATEST_RELEASE_URL = DOWNLOAD_URL + "/LATEST_RELEASE";
  private static boolean initialized;

  private static void initialize(final String workDirectory, final String expectedVersion) {
    if (initialized) {
      return;
    }

    if (System.getProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY) != null) {
      initialized = true;
      return;
    }

    LOGGER.info("Initialize {}", ChromeWebdriverFactory.class);

    final File chromeDriverDirectory = new File(workDirectory, "chromedriver");
    chromeDriverDirectory.mkdir();
    LOGGER.debug("Chromedriver work directory {}", chromeDriverDirectory);

    final File chromeDriverExecutable;

    final File existingChromeDriverExecutable =
        getExistingChromeDriverExecutable(chromeDriverDirectory);
    if (expectedVersion == null && existingChromeDriverExecutable == null) {
      // no expected version; there is no local chromedriver -> download the latest and use it
      chromeDriverExecutable = downloadChromeDriver(chromeDriverDirectory, null);
    } else if (expectedVersion == null && existingChromeDriverExecutable != null) {
      // no expected version; there is a local chromedriver -> use local instance
      chromeDriverExecutable = existingChromeDriverExecutable;
    } else if (expectedVersion != null && existingChromeDriverExecutable == null) {
      // version expected; there is no local chromedriver -> download version and use it
      chromeDriverExecutable = downloadChromeDriver(chromeDriverDirectory, expectedVersion);
    } else {
      // version expected; there is a local chromedriver
      final Matcher matcher = EXECUTABLE_PATTERN.matcher(existingChromeDriverExecutable.getName());
      matcher.find();
      final String existingVersion = matcher.group("version");
      if (Objects.equals(existingVersion, expectedVersion)) {
        chromeDriverExecutable = existingChromeDriverExecutable;
      } else {
        LOGGER.debug("Expect version {} but found {}. Remove it.", existingVersion,
            expectedVersion);
        if (!existingChromeDriverExecutable.delete()) {
          throw new RuntimeException();
        }
        chromeDriverExecutable = downloadChromeDriver(chromeDriverDirectory, expectedVersion);
      }
    }

    LOGGER.info("Install {} for Selenium.", chromeDriverExecutable);
    System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY,
        chromeDriverExecutable.getAbsolutePath());

    initialized = true;
  }

  private static File getExistingChromeDriverExecutable(final File chromeDriverDirectory) {
    final File[] files =
        chromeDriverDirectory.listFiles(new PatternFilenameFilter(EXECUTABLE_PATTERN));
    if (files == null || files.length != 1) {
      return null;
    }
    return files[0];
  }

  private static File downloadChromeDriver(final File chromeDriverDirectory,
      final String expectedVersion) {
    final String version;
    if (expectedVersion == null) {
      LOGGER.debug("Download latest version.");
      version = DownloadUtils.getString(LATEST_RELEASE_URL);
    } else {
      LOGGER.debug("Download version {}.", expectedVersion);
      version = expectedVersion;
    }
    final String system;
    if (SystemUtils.IS_OS_WINDOWS) {
      system = "win32";
    } else if (SystemUtils.IS_OS_MAC) {
      system = "mac64";
    } else if (SystemUtils.IS_OS_LINUX) {
      final StringBuilder linuxBuilder = new StringBuilder("linux");
      // TODO this property does not reflect the OS architecture, it is the "bitness" of the VM
      if (SystemUtils.OS_ARCH.contains("64")) {
        linuxBuilder.append("64");
      } else {
        linuxBuilder.append("32");
      }
      system = linuxBuilder.toString();
    } else {
      throw new UnsupportedOperationException(String.format("Unsupported operation system: %s %s",
          SystemUtils.OS_NAME, SystemUtils.OS_VERSION));
    }
    LOGGER.debug("System '{}' detected.", system);
    return downloadChromeDriver(version, system, chromeDriverDirectory);
  }

  private static File downloadChromeDriver(final String version, final String system,
      final File targetDirectory) {
    final String downloadUrl =
        DOWNLOAD_URL + String.format("/%s/chromedriver_%s.zip", version, system);
    LOGGER.debug("Download chromedriver from {}", downloadUrl);
    DownloadUtils.downloadZipAndExtract(downloadUrl, targetDirectory);
    final File chromeDriverFile =
        new File(targetDirectory, String.format("chromedriver%s", getExecutableExtension(false)));
    final File chromeDriverFileWithVersion = new File(targetDirectory,
        String.format("chromedriver-%s%s", version, getExecutableExtension(false)));
    if (!chromeDriverFile.renameTo(chromeDriverFileWithVersion)) {
      throw new RuntimeException();
    }
    if (!chromeDriverFileWithVersion.setExecutable(true, false)) {
      throw new RuntimeException();
    }
    return chromeDriverFileWithVersion;
  }

  private static String getExecutableExtension(final boolean forRegex) {
    if (SystemUtils.IS_OS_WINDOWS) {
      if (forRegex) {
        return "\\.exe";
      }
      return ".exe";
    }
    return "";
  }

  @Override
  protected WebDriver create(final Class<?> testClass, final Webdriver webdriver,
      final Map<String, String> options) {
    final String workDirectory = Optional.ofNullable(options.get(WORK_DIRECTORY))
        .orElse(System.getProperty("java.io.tmpdir"));
    final String expectedVersion = options.get(EXPECTED_VERSION);
    initialize(workDirectory, expectedVersion);
    return new ChromeDriver();
  }

}

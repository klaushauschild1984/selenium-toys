package org.openqa.selenium.toys.factory.chrome;

import static org.openqa.selenium.remote.BrowserType.CHROME;

import java.io.File;
import java.util.Map;

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

  public static final String EXPECTED_VERSION = CHROME + "_expectedVersion";
  private static final Logger LOGGER = LoggerFactory.getLogger(ChromeWebdriverFactory.class);
  private static final String DOWNLOAD_URL = "http://chromedriver.storage.googleapis.com";
  private static final String LATEST_RELEASE_URL = DOWNLOAD_URL + "/LATEST_RELEASE";

  private static boolean initialized;

  private static void initialize(final String expectedVersion) {
    if (initialized) {
      return;
    }

    if (System.getProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY) != null) {
      initialized = true;
      return;
    }

    LOGGER.info("Initialize {}", ChromeWebdriverFactory.class);

    final File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
    final File chromeDriverDirectory = new File(tempDirectory, "chromedriver");
    chromeDriverDirectory.mkdir();
    LOGGER.debug("Store chromedriver locally at {}", chromeDriverDirectory);

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
      throw new UnsupportedOperationException();
    }

    LOGGER.info("Install {} for Selenium.", chromeDriverExecutable);
    System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY,
        chromeDriverExecutable.getAbsolutePath());
    initialized = true;
  }

  private static File getExistingChromeDriverExecutable(final File chromeDriverDirectory) {
    final File[] files = chromeDriverDirectory.listFiles(
        new PatternFilenameFilter(String.format("chromedriver.*%s", getExecutableExtension(true))));
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
      system = "linux32";
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
    chromeDriverFile.renameTo(chromeDriverFileWithVersion);
    return chromeDriverFileWithVersion;
  }

  private static String getExecutableExtension(final boolean forRegex) {
    if (SystemUtils.IS_OS_WINDOWS) {
      if (forRegex) {
        return "//.exe";
      }
      return ".exe";
    }
    return "";
  }

  @Override
  protected WebDriver create(final Class<?> testClass, final Webdriver webdriver,
      final Map<String, String> options) {
    final String expectedVersion = options.get(EXPECTED_VERSION);
    initialize(expectedVersion);
    return new ChromeDriver();
  }

}

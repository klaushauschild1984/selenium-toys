package de.hauschild.selenium.toys.factory.chrome;

import static org.openqa.selenium.remote.BrowserType.CHROME;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;

import de.hauschild.selenium.toys.factory.AbstractWebDriverFactory;
import de.hauschild.selenium.toys.factory.DownloadUtils;
import de.hauschild.selenium.toys.factory.WebDriverFactory;

/**
 * {@link WebDriverFactory} implementation for Google Chrome using the {@link ChromeDriver}.<br />
 * As additional initialization step the latest release of the chrome driver will be downloaded from
 * <a href=
 * "http://chromedriver.storage.googleapis.com">http://chromedriver.storage.googleapis.com</a>.
 */
public class ChromeWebDriverFactory extends AbstractWebDriverFactory {

  public static final String EXPECTED_VERSION = CHROME + "_expectedVersion";
  public static final String FORCE_UPDATE = CHROME + "_forceUpdate";

  private static final String DOWNLOAD_URL = "http://chromedriver.storage.googleapis.com";
  private static final String LATEST_RELEASE_URL = DOWNLOAD_URL + "/LATEST_RELEASE";

  private static boolean INITIALIZED;

  private static void initialize(final String expectedVersion, final boolean forceUpdate) {
    if (INITIALIZED) {
      return;
    }

    if (System.getProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY) != null) {
      INITIALIZED = true;
      return;
    }

    final File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
    final File chromeDriverDirectory = new File(tempDirectory, "chromedriver");
    final File versionInfoFile = new File(chromeDriverDirectory, ".version");
    final Entry<String, String> versionInfo = readVersionInfo(versionInfoFile);

    final File chromeDriverExecutable;
    if (versionInfo == null) {
      chromeDriverExecutable = downloadChromeDriver(chromeDriverDirectory, versionInfoFile, null);
    } else if (forceUpdate) {
      chromeDriverExecutable = downloadChromeDriver(chromeDriverDirectory, versionInfoFile, null);
    } else if (expectedVersion != null && !Objects.equals(expectedVersion, versionInfo.getKey())) {
      chromeDriverExecutable =
          downloadChromeDriver(chromeDriverDirectory, versionInfoFile, versionInfo.getKey());
    } else {
      chromeDriverExecutable = useLocalChromeDriver(chromeDriverDirectory, versionInfo);
    }

    System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY,
        chromeDriverExecutable.getAbsolutePath());
    INITIALIZED = true;
  }

  private static File useLocalChromeDriver(final File chromeDriverDirectory,
      final Entry<String, String> versionInfo) {
    final File chromeDriverExecutable;
    chromeDriverExecutable = new File(chromeDriverDirectory, versionInfo.getValue());
    return chromeDriverExecutable;
  }

  private static File downloadChromeDriver(final File chromeDriverDirectory,
      final File versionInfoFile, final String expectedVersion) {
    final File chromeDriverExecutable;
    final String version;
    if (expectedVersion == null) {
      version = DownloadUtils.getString(LATEST_RELEASE_URL);
    } else {
      version = expectedVersion;
    }
    final Entry<String, String> versionInfo = readVersionInfo(versionInfoFile);
    if (versionInfo != null && Objects.equals(version, versionInfo.getKey())) {
      return new File(chromeDriverDirectory, versionInfo.getValue());
    }
    chromeDriverExecutable = downloadChromeDriver(version, "win32", chromeDriverDirectory);
    writeVersionInfo(versionInfoFile, version, chromeDriverExecutable);
    return chromeDriverExecutable;
  }

  private static File downloadChromeDriver(final String version, final String system,
      final File targetDirectory) {
    final String downloadUrl =
        DOWNLOAD_URL + String.format("/%s/chromedriver_%s.zip", version, system);
    DownloadUtils.downloadZipAndExtract(downloadUrl, targetDirectory);
    return new File(targetDirectory, "chromedriver.exe");
  }

  private static void writeVersionInfo(final File versionInfoFile, final String version,
      final File executable) {
    try {
      FileUtils.writeLines(versionInfoFile, Arrays.asList(version, executable.getName()));
    } catch (final IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  private static Entry<String, String> readVersionInfo(final File versionFile) {
    if (!versionFile.exists()) {
      return null;
    }
    try {
      final List<String> lines = FileUtils.readLines(versionFile, Charsets.UTF_8);
      return new SimpleEntry<>(lines.get(0), lines.get(1));
    } catch (final IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  @Override
  protected WebDriver create(final Class<?> testClass,
      final de.hauschild.selenium.toys.WebDriver webDriverAnnotation,
      final Map<String, String> options) {
    final String expectedVersion = options.get(EXPECTED_VERSION);
    final boolean forceUpdate = Boolean
        .valueOf(MoreObjects.firstNonNull(options.get(FORCE_UPDATE), Boolean.TRUE.toString()));
    initialize(expectedVersion, forceUpdate);
    return new ChromeDriver();
  }

}

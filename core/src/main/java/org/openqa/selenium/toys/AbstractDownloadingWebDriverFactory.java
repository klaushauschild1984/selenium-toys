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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.WebDriver;

public abstract class AbstractDownloadingWebDriverFactory implements WebDriverFactory {

  public static final String WORK_DIRECTORY = "WORK_DIRECTORY";
  public static final String EXPECTED_VERSION = "EXPECTED_VERSION";
  public static final String FORCE_UPDATE = "FORCE_UPDATE";

  private final String browserType;
  private final Function<File, Optional<DownloadWebDriverExecutable.WebDriverExecutable>> getWebDriverExecutableFromWorkDirectory;
  private final Supplier<String> getLatestVersion;
  private final BiFunction<String, File, DownloadWebDriverExecutable.WebDriverExecutable> downloadExpectedVersion;
  private final String systemPropertyForExecutable;

  private WebDriver webDriver;

  protected AbstractDownloadingWebDriverFactory(final String browserType,
      final Function<File, Optional<DownloadWebDriverExecutable.WebDriverExecutable>> getWebDriverExecutableFromWorkDirectory,
      final Supplier<String> getLatestVersion,
      final BiFunction<String, File, DownloadWebDriverExecutable.WebDriverExecutable> downloadExpectedVersion,
      final String systemPropertyForExecutable) {
    this.browserType = browserType;
    this.getWebDriverExecutableFromWorkDirectory = getWebDriverExecutableFromWorkDirectory;
    this.getLatestVersion = getLatestVersion;
    this.downloadExpectedVersion = downloadExpectedVersion;
    this.systemPropertyForExecutable = systemPropertyForExecutable;
  }

  protected static String getString(final String url) {
    try (final CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
      try (final CloseableHttpResponse response = httpClient.execute(new HttpGet(url))) {
        handleHttpStatus(response);
        return EntityUtils.toString(response.getEntity()).trim();
      }
    } catch (final IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  protected static void download(final String url, final Consumer<InputStream> downloadHandler) {
    try (final CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
      try (final CloseableHttpResponse response = httpClient.execute(new HttpGet(url))) {
        handleHttpStatus(response);
        try (final InputStream inputStream =
            new BufferedInputStream(response.getEntity().getContent())) {
          downloadHandler.accept(inputStream);
        }
      }
    } catch (final IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  protected static void downloadZipAndExtract(final String url, final File targetDirectory) {
    download(url, inputStream -> {
      try {
        final byte[] bytes = IOUtils.toByteArray(inputStream);
        try (final ZipFile zipFile = new ZipFile(new SeekableInMemoryByteChannel(bytes))) {
          final Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
          while (entries.hasMoreElements()) {
            final ZipArchiveEntry entry = entries.nextElement();
            final File file = new File(targetDirectory, entry.getName());
            if (entry.isDirectory()) {
              file.mkdir();
              continue;
            }
            try (final OutputStream outputStream =
                new BufferedOutputStream(new FileOutputStream(file))) {
              IOUtils.copy(zipFile.getInputStream(entry), outputStream);
            }
          }
        }
      } catch (final IOException exception) {
        throw new RuntimeException(exception);
      }
    });
  }

  private static void handleHttpStatus(final CloseableHttpResponse response) throws IOException {
    final StatusLine statusLine = response.getStatusLine();
    if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
      throw new IOException(String.format("Http GET not successful. Status: %s", statusLine));
    }
  }

  @Override
  public WebDriver create(final Map<String, Object> options) {
    new Initialization(() -> {
      beforeInitialization(options);

      final File workDirectory = getWorkDirectory(options);
      final String expectedVersion = (String) options.get(EXPECTED_VERSION);
      final boolean forceUpdate = Boolean.parseBoolean((String) options.get(FORCE_UPDATE));
      final File webDriverExecutable =
          new DownloadWebDriverExecutable(workDirectory, getWebDriverExecutableFromWorkDirectory,
              getLatestVersion, downloadExpectedVersion).get(expectedVersion, forceUpdate);
      new SystemPropertyWebDriverExecutableSetup(systemPropertyForExecutable, webDriverExecutable)
          .setup();
      webDriver = instantiateWebDriver(options);
      new WebDriverShutdownHook(webDriver).install();

      afterInitialization(options);
    }).initialize();
    return webDriver;
  }

  protected File getWorkDirectory(final Map<String, Object> options) {
    return new File(((String) Optional.ofNullable(options.get(WORK_DIRECTORY))
        .orElse(System.getProperty("java.io.tmpdir"))), browserType);
  }

  protected void beforeInitialization(final Map<String, Object> options) {
    // override this to perform additional stuff before initialization
  }

  protected abstract WebDriver instantiateWebDriver(final Map<String, Object> options);

  protected void afterInitialization(final Map<String, Object> options) {
    // override this to perform additional stuff after initialization
  }

  @Override
  public String createdBrowserType() {
    return browserType;
  }

}

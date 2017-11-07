package de.hauschild.selenium.toys.factory.chrome;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.testng.Assert;

import de.hauschild.selenium.toys.factory.WebDriverFactory;

/**
 * {@link WebDriverFactory} implementation for Google Chrome using the {@link ChromeDriver}.<br />
 * As additional initialization step the latest release of the chrome driver will be downloaded from
 * {@value DOWNLOAD_URL}.
 */
public class ChromeWebDriverFactory implements WebDriverFactory {

  private static final String DOWNLOAD_URL = "http://chromedriver.storage.googleapis.com";
  private static final String LATEST_RELEASE_URL = DOWNLOAD_URL + "/LATEST_RELEASE";

  private static boolean INITIALIZED;

  private static void initialize() {
    if (INITIALIZED) {
      return;
    }

    final File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
    final File chromeDriverDirectory = new File(tempDirectory, "chromedriver");
    File chromeDriverExecutable = new File(chromeDriverDirectory, "chromedriver.exe");
    if (!chromeDriverExecutable.exists()) {
      chromeDriverDirectory.mkdir();
      try (final CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
        final String latestRelease = getLatestRelease(httpClient);
        final String downloadBaseUrl = String.format("%s/%s/", DOWNLOAD_URL, latestRelease);
        // TODO currently only windows
        final String downloadBinaryUrl = downloadBaseUrl + "chromedriver_win32.zip";
        chromeDriverExecutable =
            downloadBinary(httpClient, downloadBinaryUrl, chromeDriverDirectory);
      } catch (final IOException exception) {
        Assert.fail("Unable to download or setup chrome driver.", exception);
      }
    }
    System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY,
        chromeDriverExecutable.getAbsolutePath());
    INITIALIZED = true;
  }

  private static File downloadBinary(final CloseableHttpClient httpClient, final String downloadUrl,
      final File chromeDriverDirectory) throws IOException {
    try (final CloseableHttpResponse response = httpClient.execute(new HttpGet(downloadUrl))) {
      handleHttpStatus(response);
      try (final InputStream responseContent =
          new BufferedInputStream(response.getEntity().getContent())) {
        final ZipInputStream zipInputStream = new ZipInputStream(responseContent);
        final ZipEntry entry = zipInputStream.getNextEntry();
        final File binaryFile = new File(chromeDriverDirectory, entry.getName());
        final FileOutputStream binaryFileOutputStream = new FileOutputStream(binaryFile);
        IOUtils.copy(zipInputStream, binaryFileOutputStream);
        binaryFileOutputStream.close();
        return binaryFile;
      }
    }
  }

  private static String getLatestRelease(final CloseableHttpClient httpClient) throws IOException {
    try (final CloseableHttpResponse response =
        httpClient.execute(new HttpGet(LATEST_RELEASE_URL))) {
      handleHttpStatus(response);
      return EntityUtils.toString(response.getEntity()).trim();
    }
  }

  private static void handleHttpStatus(final CloseableHttpResponse response) throws IOException {
    final int statusCode = response.getStatusLine().getStatusCode();
    if (statusCode != HttpStatus.SC_OK) {
      throw new IOException(String.format("Status code: %s", statusCode));
    }
  }

  @Override
  public WebDriver create(final Class<?> testClass) {
    initialize();
    return new ChromeDriver();
  }

}

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

public enum ChromeWebDriverUtils {

  ;

  private static final String DOWNLOAD_URL = "http://chromedriver.storage.googleapis.com";
  private static final String LATEST_RELEASE_URL = DOWNLOAD_URL + "/LATEST_RELEASE";

  /**
   * Retrieves the latest released version of chrome driver.
   * 
   * @return the latest chrome driver version
   */
  public static String getLatestRelease() {
    try (final CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
      try (final CloseableHttpResponse response =
          httpClient.execute(new HttpGet(LATEST_RELEASE_URL))) {
        handleHttpStatus(response);
        return EntityUtils.toString(response.getEntity()).trim();
      }
    } catch (final IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  /**
   * Downloads ant extracts the chrome driver.
   * 
   * @param version the version
   * @param system the system (linux32, linus64, mac64, win32)
   * @param targetDirectory the target directory
   * @return the file pointing to the chrome driver
   */
  public static File downloadChromeDriver(final String version, final String system,
      final File targetDirectory) {
    final String downloadUrl =
        DOWNLOAD_URL + String.format("/%s/chromedriver_%s.zip", version, system);
    try (final CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
      try (final CloseableHttpResponse response = httpClient.execute(new HttpGet(downloadUrl))) {
        handleHttpStatus(response);
        try (final InputStream responseContent =
            new BufferedInputStream(response.getEntity().getContent())) {
          final ZipInputStream zipInputStream = new ZipInputStream(responseContent);
          final ZipEntry entry = zipInputStream.getNextEntry();
          final File binaryFile = new File(targetDirectory, entry.getName());
          final FileOutputStream binaryFileOutputStream = new FileOutputStream(binaryFile);
          IOUtils.copy(zipInputStream, binaryFileOutputStream);
          binaryFileOutputStream.close();
          return binaryFile;
        }
      }
    } catch (final IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  private static void handleHttpStatus(final CloseableHttpResponse response) throws IOException {
    final int statusCode = response.getStatusLine().getStatusCode();
    if (statusCode != HttpStatus.SC_OK) {
      throw new IOException(String.format("Status code: %s", statusCode));
    }
  }

}

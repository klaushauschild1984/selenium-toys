package org.openqa.selenium.toys.factory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import org.testng.Assert;
import org.testng.annotations.Test;

public class DownloadUtilsTest {

  private static final String URL = "https://httpstat.us/";

  @Test
  public void getStringTest() {
    Assert.assertEquals(DownloadUtils.getString(URL + "200"), "200 OK");
    try {
      DownloadUtils.getString(URL + "500");
      Assert.fail("exception expected");
    } catch (final RuntimeException e) {
      // exception expected
    }
  }

  @Test
  public void downloadTest() {
    final Consumer<InputStream> closingInputStreamConsumer = inputStream -> {
      try {
        inputStream.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    };
    DownloadUtils.download(URL + "200", closingInputStreamConsumer);
    try {
      DownloadUtils.download(URL + "500", closingInputStreamConsumer);
      Assert.fail("exception expected");
    } catch (final RuntimeException e) {
      // exception expected
    }
  }

  @Test
  public void downloadZipAndExtractTest() {
    try {
      DownloadUtils.downloadZipAndExtract(URL + "200", new File(""));
      Assert.fail("exception expected");
    } catch (final RuntimeException e) {
      // exception expected
    }
  }

}

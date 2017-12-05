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

import org.testng.annotations.Test;

public class DownloadUtilsTest {

  private static final String URL = "https://httpstat.us/";

  @Test
  public void getStringTest() {
    /*
    Assert.assertEquals(DownloadUtils.getString(URL + "200"), "200 OK");
    try {
      DownloadUtils.getString(URL + "500");
      Assert.fail("exception expected");
    } catch (final RuntimeException e) {
      // exception expected
    }
    */
  }

  @Test
  public void downloadTest() {
    /*
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
    */
  }

  @Test
  public void downloadZipAndExtractTest() {
    /*
    try {
      DownloadUtils.downloadZipAndExtract(URL + "200", new File(""));
      Assert.fail("exception expected");
    } catch (final RuntimeException e) {
      // exception expected
    }
    */
  }

}

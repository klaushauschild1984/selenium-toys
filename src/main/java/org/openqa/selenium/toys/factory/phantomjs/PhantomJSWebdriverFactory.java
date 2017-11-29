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

package org.openqa.selenium.toys.factory.phantomjs;

import java.io.File;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.toys.Webdriver;
import org.openqa.selenium.toys.factory.AbstractWebdriverFactory;
import org.openqa.selenium.toys.factory.DownloadUtils;

public class PhantomJSWebdriverFactory extends AbstractWebdriverFactory {

  private static boolean initialized = false;

  public PhantomJSWebdriverFactory() {
    super(BrowserType.PHANTOMJS);
  }

  private static void initialize() {
    if (initialized) {
      return;
    }

    final File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
    final File driverDirectory = new File(tempDirectory, "phantomJs");
    driverDirectory.mkdir();
    DownloadUtils.downloadZipAndExtract(
        "https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-2.1.1-windows.zip",
        driverDirectory);

    final File driverExecutable = new File(
        new File(new File(driverDirectory, "phantomjs-2.1.1-windows"), "bin"), "phantomjs.exe");
    System.setProperty("phantomjs.binary.path", driverExecutable.getAbsolutePath());
    initialized = true;
  }

  @Override
  protected WebDriver create(final Class<?> testClass, final Webdriver webdriver,
      final Map<String, String> options) {
    initialize();
    return new PhantomJSDriver();
  }

}

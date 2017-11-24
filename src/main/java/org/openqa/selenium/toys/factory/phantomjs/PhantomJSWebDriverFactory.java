package org.openqa.selenium.toys.factory.phantomjs;

import java.io.File;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.toys.factory.AbstractWebDriverFactory;
import org.openqa.selenium.toys.factory.DownloadUtils;

public class PhantomJSWebDriverFactory extends AbstractWebDriverFactory {

  private static boolean initialized = false;

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
  protected WebDriver create(final Class<?> testClass,
      final org.openqa.selenium.toys.WebDriver webDriverAnnotation,
      final Map<String, String> options) {
    initialize();
    return new PhantomJSDriver();
  }

}

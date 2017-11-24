package org.openqa.selenium.toys;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.google.common.io.Files;

class Screenshots {

  private final WebDriver webDriver;
  private final File baseDirectory;

  public Screenshots(final WebDriver webDriver, final TakeScreenshots takeScreenshot,
      final Class<? extends SeleniumTests> clazz) {
    this.webDriver = webDriver;
    if (takeScreenshot.baseDirectory().isEmpty()) {
      final File tempDirectory = Files.createTempDir();
      baseDirectory = new File(tempDirectory, clazz.getName());
    } else {
      baseDirectory = new File(new File(takeScreenshot.baseDirectory()), clazz.getName());
    }
    baseDirectory.mkdirs();
  }

  void start() {
    screenshot("start");
  }

  void failure() {
    screenshot("failure");
  }

  void finish() {
    screenshot("finish");
  }

  void screenshot(final String label) {

  }

  private BufferedImage takeScreenshot() {
    if (!(webDriver instanceof TakesScreenshot)) {
      throw new IllegalStateException(
          String.format("Web driver %s not capable to take screen shots", webDriver));
    }
    final byte[] screenshotBytes = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
    try {
      return ImageIO.read(new ByteArrayInputStream(screenshotBytes));
    } catch (final IOException exception) {
      throw new RuntimeException(exception);
    }
  }

}

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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.io.Files;

class Screenshots {

  private static final Logger LOGGER = LoggerFactory.getLogger(Screenshots.class);
  private static final String FILE_FORMAT = "png";

  private final WebDriver webDriver;
  private final File baseDirectory;
  private final boolean compareToExistingScreenshots;
  private final Map<String, AtomicInteger> indices = Maps.newHashMap();

  public Screenshots(final WebDriver webDriver, final TakeScreenshots takeScreenshots,
      final Class<? extends SeleniumTests> clazz) {
    this.webDriver = webDriver;
    if (takeScreenshots.baseDirectory().isEmpty()) {
      final File tempDirectory = Files.createTempDir();
      baseDirectory = new File(tempDirectory, getClassDirectory(clazz));
    } else {
      baseDirectory = new File(new File(takeScreenshots.baseDirectory()), getClassDirectory(clazz));
    }
    baseDirectory.mkdirs();
    LOGGER.debug("Take screenshots for {}. Store in {}", clazz.getName(), baseDirectory);
    compareToExistingScreenshots = takeScreenshots.compareToExistingScreenshots();
  }

  private String getClassDirectory(final Class<? extends SeleniumTests> clazz) {
    return clazz.getName().replace(".", File.separator);
  }

  void start(final String methodName) {
    indices.put(methodName, new AtomicInteger());
    screenshot(methodName, null);
  }

  void failure(final String methodName) {
    screenshot(methodName, "failure");
  }

  void screenshot(final String methodName, final String label) {
    final File methodDirectory = getMethodDirectory(methodName);
    final BufferedImage screenshot = takeScreenshot();
    final int index = indices.get(methodName).getAndIncrement();
    final File screenshotFile = getScreenshotFile(index, methodDirectory, label);
    if (screenshotFile.exists() && compareToExistingScreenshots) {
      final BufferedImage existingScreenshot = readScreenshotFromFile(screenshotFile);
      final File differenceImageFile = getScreenshotFile(index, methodDirectory, "diff");
      if (new ScreenshotComparator(differenceImage -> {
        writeScreenshotToFile(screenshot, getScreenshotFile(index, methodDirectory, "new"));
        writeScreenshotToFile(differenceImage, differenceImageFile);
      }).compare(existingScreenshot, screenshot) != 0) {

        throw new ScreenshotAssertionError(String.format(
            "There are image differences. See %s for further details.", differenceImageFile));
      }
    } else {
      writeScreenshotToFile(screenshot, screenshotFile);
    }
  }

  private BufferedImage readScreenshotFromFile(final File screenshotFile) {
    try {
      return ImageIO.read(screenshotFile);
    } catch (final IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  private void writeScreenshotToFile(final BufferedImage screenshot, final File screenshotFile) {
    try {
      LOGGER.debug("Take screenshot {}", screenshotFile);
      ImageIO.write(screenshot, FILE_FORMAT, screenshotFile);
    } catch (final IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  private File getMethodDirectory(final String methodName) {
    final File methodDirectory = new File(baseDirectory, methodName);
    methodDirectory.mkdir();
    return methodDirectory;
  }

  private File getScreenshotFile(final int index, final File methodDirectory, final String label) {
    final StringBuilder builder = new StringBuilder();
    builder.append(StringUtils.leftPad("" + index, 3, "0"));
    if (label != null) {
      builder.append("-");
      builder.append(label);
    }
    builder.append(".");
    builder.append(FILE_FORMAT);
    return new File(methodDirectory, builder.toString());
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

  class ScreenshotAssertionError extends AssertionError {

    public ScreenshotAssertionError(final Object detailMessage) {
      super(detailMessage);
    }

  }

}

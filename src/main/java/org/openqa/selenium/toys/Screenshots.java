package org.openqa.selenium.toys;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
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

  private final WebDriver webDriver;
  private final File baseDirectory;
  private final Map<Method, AtomicInteger> indices = Maps.newHashMap();

  public Screenshots(final WebDriver webDriver, final TakeScreenshots takeScreenshots,
      final Class<? extends SeleniumTests> clazz) {
    this.webDriver = webDriver;
    if (takeScreenshots.baseDirectory().isEmpty()) {
      final File tempDirectory = Files.createTempDir();
      baseDirectory = new File(tempDirectory, clazz.getName());
    } else {
      baseDirectory = new File(new File(takeScreenshots.baseDirectory()), clazz.getName());
    }
    baseDirectory.mkdirs();
    LOGGER.debug("Take screenshots for {}. Store in {}", clazz.getName(), baseDirectory);
  }

  void start(final Method method) {
    indices.put(method, new AtomicInteger());
    screenshot(method, "start");
  }

  void failure(final Method method) {
    screenshot(method, "failure");
  }

  void screenshot(final Method method, final String label) {
    final File methodDirectory = new File(baseDirectory, method.getName());
    methodDirectory.mkdir();
    final BufferedImage screenshot = takeScreenshot();
    try {
      final int index = indices.get(method).getAndIncrement();
      final File screenshotFile = new File(methodDirectory,
          String.format("%s-%s.png", StringUtils.leftPad("" + index, 3, "0"), label));
      LOGGER.debug("Take screenshot {}", screenshotFile);
      ImageIO.write(screenshot, "png", screenshotFile);
    } catch (final IOException exception) {
      throw new RuntimeException(exception);
    }
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

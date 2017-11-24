package org.openqa.selenium.toys.factory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.toys.WebDriver;
import org.springframework.core.annotation.AnnotationUtils;

import com.google.common.collect.Maps;

public abstract class AbstractWebDriverFactory implements WebDriverFactory {

  public static final String IMPLICITLY_WAIT = "implicitlyWait";

  @Override
  public org.openqa.selenium.WebDriver create(final Class<?> testClass) {
    final WebDriver webDriverAnnotation = getWebDriverAnnotation(testClass);
    final Map<String, String> options = toMap(webDriverAnnotation.options());
    final org.openqa.selenium.WebDriver webDriver = create(testClass, webDriverAnnotation, options);
    configureWebDriver(webDriver, options);
    return webDriver;
  }

  protected abstract org.openqa.selenium.WebDriver create(final Class<?> testClass,
      final WebDriver webDriverAnnotation, final Map<String, String> options);

  protected void configureWebDriver(final org.openqa.selenium.WebDriver webDriver,
      final Map<String, String> options) {
    final String implicitlyWait = options.get(IMPLICITLY_WAIT);
    if (implicitlyWait != null) {
      webDriver.manage().timeouts().implicitlyWait(Long.valueOf(implicitlyWait),
          TimeUnit.MILLISECONDS);
    }
  }

  private WebDriver getWebDriverAnnotation(final Class<?> testClass) {
    final WebDriver webDriverAnnotation =
        AnnotationUtils.findAnnotation(testClass, WebDriver.class);
    if (webDriverAnnotation == null) {
      throw new AssertionError(String.format(
          "The test class %s misses the %s annotation to specify the implementation to use.",
          testClass.getName(), WebDriver.class.getName()));
    }
    return webDriverAnnotation;
  }

  private Map<String, String> toMap(final String[] keyValues) {
    final Map<String, String> map = Maps.newHashMap();
    if (keyValues != null) {
      for (int i = 0; i < keyValues.length; i++) {
        final String key = keyValues[i];
        String value = null;
        if (i + 1 < keyValues.length) {
          value = keyValues[i + 1];
        }
        map.put(key, value);
      }
    }
    return map;
  }

}

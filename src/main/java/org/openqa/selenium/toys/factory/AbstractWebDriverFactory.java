package org.openqa.selenium.toys.factory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.toys.WebDriver;
import org.springframework.core.annotation.AnnotationUtils;
import org.testng.Assert;

import com.google.common.collect.Maps;

public abstract class AbstractWebDriverFactory implements WebDriverFactory {

  public static final String IMPLICITLY_WAIT = "implicitlyWait";

  @Override
  public org.openqa.selenium.WebDriver create(final Class<?> testClass) {
    final WebDriver webDriverAnnotation = getWebDriverAnnotation(testClass);
    final Map<String, String> options = toMap(Arrays.asList(webDriverAnnotation.options()));
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
      Assert.fail(String.format(
          "The test class %s misses the %s annotation to specify the implementation to use.",
          testClass.getName(), WebDriver.class.getName()));
    }
    return webDriverAnnotation;
  }

  private Map<String, String> toMap(final List<String> keyValues) {
    final Map<String, String> map = Maps.newHashMap();
    for (int i = 0; i < keyValues.size(); i++) {
      final String key = keyValues.get(i);
      String value = null;
      if (i + 1 < keyValues.size()) {
        value = keyValues.get(i + 1);
      }
      map.put(key, value);
    }
    return map;
  }

}

package org.openqa.selenium.toys.factory;

import static org.openqa.selenium.toys.Webdriver.IMPLICITLY_WAIT;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.toys.Webdriver;
import org.springframework.core.annotation.AnnotationUtils;

import com.google.common.collect.Maps;

public abstract class AbstractWebdriverFactory implements WebdriverFactory {

  @Override
  public WebDriver create(final Class<?> testClass) {
    final Webdriver webdriver = getWebDriverAnnotation(testClass);
    final Map<String, String> options = toMap(webdriver.options());
    final org.openqa.selenium.WebDriver webDriver = create(testClass, webdriver, options);
    configureWebDriver(webDriver, options);
    return webDriver;
  }

  protected abstract org.openqa.selenium.WebDriver create(final Class<?> testClass,
      final Webdriver webdriver, final Map<String, String> options);

  protected void configureWebDriver(final org.openqa.selenium.WebDriver webDriver,
      final Map<String, String> options) {
    Optional.ofNullable(options.get(IMPLICITLY_WAIT)) //
        .ifPresent(implicitlyWait -> webDriver.manage().timeouts()
            .implicitlyWait(Long.valueOf(implicitlyWait), TimeUnit.MILLISECONDS));
  }

  private Webdriver getWebDriverAnnotation(final Class<?> testClass) {
    return Optional.ofNullable(AnnotationUtils.findAnnotation(testClass, Webdriver.class)) //
        .orElseThrow(() -> new AssertionError(String.format(
            "The test class %s misses the %s annotation to specify the implementation to use.",
            testClass.getName(), Webdriver.class.getName())));
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

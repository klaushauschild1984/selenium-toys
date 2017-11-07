package de.hauschild.selenium.toys.factory;

import org.springframework.core.annotation.AnnotationUtils;
import org.testng.Assert;

import de.hauschild.selenium.toys.WebDriver;

public abstract class AbstractWebDriverFactory implements WebDriverFactory {

  protected WebDriver getWebDriverAnnotation(final Class<?> testClass) {
    final WebDriver webDriverAnnotation =
        AnnotationUtils.findAnnotation(testClass, WebDriver.class);
    if (webDriverAnnotation == null) {
      Assert.fail(String.format(
          "The test class %s misses the %s annotation to specify the implementation to use.",
          testClass.getName(), WebDriver.class.getName()));
    }
    return webDriverAnnotation;
  }

}

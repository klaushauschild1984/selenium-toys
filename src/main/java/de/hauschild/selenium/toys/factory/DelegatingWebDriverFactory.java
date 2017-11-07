package de.hauschild.selenium.toys.factory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.testng.Assert;

import com.google.common.collect.ImmutableMap;

import de.hauschild.selenium.toys.WebDriver;
import de.hauschild.selenium.toys.factory.chrome.ChromeWebDriverFactory;

/**
 * Implementation of {@link WebDriverFactory} that delegates to the concrete {@link WebDriverFactory
 * implementations} and configures common settings.
 */
public class DelegatingWebDriverFactory extends AbstractWebDriverFactory {

  private static final Map<String, WebDriverFactory> WEB_DRIVER_FACTORIES =
      ImmutableMap.<String, WebDriverFactory>builder() //
          .put(WebDriver.CHROME, new ChromeWebDriverFactory()) //
          .build();

  @Override
  public org.openqa.selenium.WebDriver create(final Class<?> testClass) {
    final WebDriver webDriverAnnotation = getWebDriverAnnotation(testClass);
    final WebDriverFactory webDriverFactory = getWebDriverFactory(webDriverAnnotation);
    final org.openqa.selenium.WebDriver webDriver = webDriverFactory.create(testClass);
    configureWebDriver(webDriverAnnotation, webDriver);
    return webDriver;
  }

  protected void configureWebDriver(final WebDriver webDriverAnnotation,
      final org.openqa.selenium.WebDriver webDriver) {
    final long implicitlyWait = webDriverAnnotation.implicitlyWait();
    if (implicitlyWait != WebDriver.NOT_SET) {
      webDriver.manage().timeouts().implicitlyWait(implicitlyWait, TimeUnit.MILLISECONDS);
    }
  }

  private WebDriverFactory getWebDriverFactory(final WebDriver webDriverAnnotation) {
    final String webDriverName = webDriverAnnotation.value();
    final WebDriverFactory webDriverFactory = WEB_DRIVER_FACTORIES.get(webDriverName);
    if (webDriverFactory == null) {
      Assert.fail(String.format("Unknown web driver %s.", webDriverName));
      return null;
    }
    return webDriverFactory;
  }

}

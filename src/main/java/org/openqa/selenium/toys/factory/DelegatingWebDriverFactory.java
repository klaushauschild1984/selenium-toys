package org.openqa.selenium.toys.factory;

import java.util.Map;

import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.toys.WebDriver;
import org.openqa.selenium.toys.factory.chrome.ChromeWebDriverFactory;
import org.openqa.selenium.toys.factory.phantomjs.PhantomJSWebDriverFactory;
import org.testng.Assert;

import com.google.common.collect.ImmutableMap;

/**
 * Implementation of {@link WebDriverFactory} that delegates to the concrete {@link WebDriverFactory
 * implementations} and configures common settings.
 */
public class DelegatingWebDriverFactory extends AbstractWebDriverFactory {

  private static final Map<String, WebDriverFactory> WEB_DRIVER_FACTORIES =
      ImmutableMap.<String, WebDriverFactory>builder() //
          .put(BrowserType.CHROME, new ChromeWebDriverFactory()) //
          .put(BrowserType.PHANTOMJS, new PhantomJSWebDriverFactory()) //
          .build();

  @Override
  protected org.openqa.selenium.WebDriver create(final Class<?> testClass,
      final WebDriver webDriverAnnotation, final Map<String, String> options) {
    final WebDriverFactory webDriverFactory = getWebDriverFactory(webDriverAnnotation);
    return webDriverFactory.create(testClass);
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

package org.openqa.selenium.toys.factory;

import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.toys.Webdriver;
import org.openqa.selenium.toys.factory.chrome.ChromeWebdriverFactory;
import org.openqa.selenium.toys.factory.phantomjs.PhantomJSWebdriverFactory;

import com.google.common.collect.ImmutableMap;

/**
 * Implementation of {@link WebDriverFactory} that delegates to the concrete {@link WebDriverFactory
 * implementations} and configures common settings.
 */
public class DelegatingWebDriverFactory extends AbstractWebDriverFactory {

  private static final Map<String, WebDriverFactory> WEB_DRIVER_FACTORIES =
      ImmutableMap.<String, WebDriverFactory>builder() //
          .put(BrowserType.CHROME, new ChromeWebdriverFactory()) //
          .put(BrowserType.PHANTOMJS, new PhantomJSWebdriverFactory()) //
          .build();

  @Override
  protected WebDriver create(final Class<?> testClass, final Webdriver webdriver,
      final Map<String, String> options) {
    final WebDriverFactory webDriverFactory = getWebDriverFactory(webdriver);
    return webDriverFactory.create(testClass);
  }

  private WebDriverFactory getWebDriverFactory(final Webdriver webdriver) {
    final String webDriverName = webdriver.value();
    final WebDriverFactory webDriverFactory = WEB_DRIVER_FACTORIES.get(webDriverName);
    if (webDriverFactory == null) {
      throw new AssertionError(String.format("Unknown web driver %s.", webDriverName));
    }
    return webDriverFactory;
  }

}

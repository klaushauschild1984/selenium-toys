package org.openqa.selenium.toys.factory;

import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.toys.Webdriver;
import org.openqa.selenium.toys.factory.chrome.ChromeWebdriverFactory;
import org.openqa.selenium.toys.factory.phantomjs.PhantomJSWebdriverFactory;

import com.google.common.collect.ImmutableMap;

/**
 * Implementation of {@link WebdriverFactory} that delegates to the concrete {@link WebdriverFactory
 * implementations} and configures common settings.
 */
public class DelegatingWebdriverFactory extends AbstractWebdriverFactory {

  private static final Map<String, WebdriverFactory> WEB_DRIVER_FACTORIES =
      ImmutableMap.<String, WebdriverFactory>builder() //
          .put(BrowserType.CHROME, new ChromeWebdriverFactory()) //
          .put(BrowserType.PHANTOMJS, new PhantomJSWebdriverFactory()) //
          .build();

  @Override
  protected WebDriver create(final Class<?> testClass, final Webdriver webdriver,
      final Map<String, String> options) {
    final WebdriverFactory webdriverFactory = getWebDriverFactory(webdriver);
    return webdriverFactory.create(testClass);
  }

  private WebdriverFactory getWebDriverFactory(final Webdriver webdriver) {
    final String webDriverName = webdriver.value();
    final WebdriverFactory webdriverFactory = WEB_DRIVER_FACTORIES.get(webDriverName);
    if (webdriverFactory == null) {
      throw new AssertionError(String.format("Unknown web driver %s.", webDriverName));
    }
    return webdriverFactory;
  }

}

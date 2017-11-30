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

package org.openqa.selenium.toys.factory;

import java.util.Map;
import java.util.Optional;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.toys.Webdriver;
import org.openqa.selenium.toys.factory.chrome.ChromeWebdriverFactory;
import org.openqa.selenium.toys.factory.firefox.FirefoxWebdriverFactory;
import org.openqa.selenium.toys.factory.phantomjs.PhantomJSWebdriverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * Implementation of {@link WebdriverFactory} that delegates to the concrete {@link WebdriverFactory
 * implementations} and configures common settings.
 */
public class DelegatingWebdriverFactory extends AbstractWebdriverFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(DelegatingWebdriverFactory.class);

  private static final Map<String, WebdriverFactory> WEBDRIVER_FACTORIES = Maps.newHashMap();

  static {
    register(new ChromeWebdriverFactory());
    register(new FirefoxWebdriverFactory());
    register(new PhantomJSWebdriverFactory());
  }

  public DelegatingWebdriverFactory() {
    super(null);
  }

  /**
   * Registers a new {@link WebdriverFactory} for its {@link WebdriverFactory#supportedBrowserType()
   * supported browser type}. If a factory is already registered for the browser type the old one
   * will be overridden.
   * <p>
   * All factories included in this binary are already registered. If you register a custom factory
   * YOU have to manage that {@link #register(WebdriverFactory)} is invoked before the actual tests.
   * </p>
   * 
   * @param webdriverFactory the webdriver factory to register
   */
  public static void register(final WebdriverFactory webdriverFactory) {
    final String browserType = webdriverFactory.supportedBrowserType();
    Optional.ofNullable(WEBDRIVER_FACTORIES.get(browserType)) //
        .ifPresent(registeredWebdriverFactory -> {
          LOGGER.warn("Override already registered factory {} for browser type [{}].",
              registeredWebdriverFactory.getClass(), browserType);
        });
    LOGGER.debug("Register {} for browser type [{}].", webdriverFactory.getClass(), browserType);
    WEBDRIVER_FACTORIES.put(browserType, webdriverFactory);
  }

  @Override
  protected WebDriver create(final Class<?> testClass, final Webdriver webdriver,
      final Map<String, String> options) {
    final WebdriverFactory webdriverFactory = getWebDriverFactory(webdriver);
    return webdriverFactory.create(testClass);
  }

  private WebdriverFactory getWebDriverFactory(final Webdriver webdriver) {
    final String webDriverName = webdriver.value();
    return Optional.ofNullable(WEBDRIVER_FACTORIES.get(webDriverName)) //
        .orElseThrow(
            () -> new AssertionError(String.format("Unknown web driver %s.", webDriverName)));
  }

}

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
    return Optional.ofNullable(WEB_DRIVER_FACTORIES.get(webDriverName)) //
        .orElseThrow(
            () -> new AssertionError(String.format("Unknown web driver %s.", webDriverName)));
  }

}

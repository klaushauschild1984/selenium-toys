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

package org.openqa.selenium.toys;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public enum WebDriverFactoryRegistry {

  ;

  private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverFactoryRegistry.class);

  private static final Map<String, WebDriverFactory> FACTORIES = Maps.newHashMap();

  /**
   * Registers a new {@link WebDriverFactory} for its {@link WebDriverFactory#createdBrowserType()}
   * supported browser type}. If a factory is already registered for the browser type the old one
   * will be overridden.
   * <p>
   * All factories included in this binary are already registered. If you register a custom factory
   * YOU have to manage that {@link #register(WebDriverFactory)} is invoked before the actual tests.
   * </p>
   *
   * @param webDriverFactory the web driver factory to register
   */
  public static void register(final WebDriverFactory webDriverFactory) {
    final String browserType = webDriverFactory.createdBrowserType();
    Optional.ofNullable(FACTORIES.get(browserType)) //
        .ifPresent(registeredWebDriverFactory -> {
          LOGGER.warn("Override already registered factory {} for browser type [{}].",
              registeredWebDriverFactory.getClass(), browserType);
        });
    LOGGER.debug("Register {} for browser type [{}].", webDriverFactory.getClass(), browserType);
    FACTORIES.put(browserType, webDriverFactory);
  }

  public static WebDriverFactory getWebDriverFactory(final String browserType) {
    return Optional.ofNullable(FACTORIES.get(browserType)) //
        .orElseThrow(() -> new IllegalArgumentException(
            String.format("Unsupported browser type [%s].", browserType)));
  }

}

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

import org.openqa.selenium.WebDriver;

public interface WebDriverFactory {

  /**
   * Creates the {@link WebDriver} and makes it ready to use for Selenium.
   * 
   * @param options options
   * @return the created {@link WebDriver}, at this point it is ready to use
   */
  WebDriver create(Map<String, Object> options);

  /**
   * Returns the created browser type. Typically values of the constants from
   * {@link org.openqa.selenium.remote.BrowserType}.
   * 
   * @return the created browser type
   */
  String createdBrowserType();

}

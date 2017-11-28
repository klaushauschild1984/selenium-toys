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

import org.openqa.selenium.WebDriver;

/**
 * A web driver factory creates a web driver and initializes it.
 */
public interface WebdriverFactory {

  /**
   * Creates the web driver and initializes it.
   * 
   * @param testClass the test class
   * @return the initialized web driver
   */
  WebDriver create(Class<?> testClass);

}

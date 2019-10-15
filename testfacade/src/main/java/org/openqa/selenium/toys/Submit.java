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

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

public class Submit {

  private final WebElement element;
  private final Runnable screenshotTaker;

  Submit(final WebElement element, final Runnable screenshotTaker) {
    this.element = element;
    this.screenshotTaker = screenshotTaker;
  }

  public void enter() {
    element.sendKeys(Keys.ENTER);
    screenshotTaker.run();
  }

  public void submit() {
    element.submit();
    screenshotTaker.run();
  }

}

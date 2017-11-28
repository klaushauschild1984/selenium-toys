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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

class Type {

  private final WebDriver webDriver;
  private final String text;
  private final Runnable screenshotTaker;

  Type(final WebDriver webDriver, final String text, final Runnable screenshotTaker) {
    this.webDriver = webDriver;
    this.text = text;
    this.screenshotTaker = screenshotTaker;
  }

  public Submit on(final By by) {
    final WebElement element = webDriver.findElement(by);
    element.sendKeys(text);
    return new Submit(element, screenshotTaker);
  }

}

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

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class Expect {

  private final WebDriver webDriver;
  private final By by;

  Expect(final WebDriver webDriver, final By by) {
    this.webDriver = webDriver;
    this.by = by;
  }

  public void hasText(final String expectedText) {
    final WebElement element = webDriver.findElement(by);
    final String text = element.getText();
    assertThat(text, is(expectedText));
  }

  public void isPresent() {
    try {
      final WebElement element = webDriver.findElement(by);
    } catch (final NoSuchElementException exception) {
      Assert.fail(String.format("%s is not present", by));
    }
  }

  public void isVisible() {
    isPresent();
    final WebElement element = webDriver.findElement(by);
    assertThat(element.isDisplayed(), is(true));
  }
}

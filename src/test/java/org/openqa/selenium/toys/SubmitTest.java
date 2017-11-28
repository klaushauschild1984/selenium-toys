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

import org.mockito.Mockito;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class SubmitTest {

  @Test
  public void submitTest() {
    final WebElement webElement = Mockito.mock(WebElement.class);
    final Submit submit = new Submit(webElement, () -> {});
    submit.submit();
    Mockito.verify(webElement).submit();
  }

  @Test
  public void enterTest() {
    final WebElement webElement = Mockito.mock(WebElement.class);
    final Submit submit = new Submit(webElement, () -> {});
    submit.enter();
    Mockito.verify(webElement).sendKeys(Keys.ENTER);
  }

}

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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class TypeTest {

  @Test
  public void onTest() {
    final WebDriver webDriver = Mockito.mock(WebDriver.class);
    final WebElement webElement = Mockito.mock(WebElement.class);

    final Type type = new Type(webDriver, "text", () -> {});
    final By byId = By.id("id");
    Mockito.when(webDriver.findElement(byId)).thenReturn(webElement);
    type.on(byId);
    Mockito.verify(webElement).sendKeys("text");
  }

}

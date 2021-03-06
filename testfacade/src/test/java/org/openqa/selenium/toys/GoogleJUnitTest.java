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

import static org.openqa.selenium.remote.BrowserType.CHROME;
import static org.openqa.selenium.toys.AbstractDownloadingWebDriverFactory.WORK_DIRECTORY;
import static org.openqa.selenium.toys.RunWithWebDriver.IMPLICITLY_WAIT;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.toys.junit4.SeleniumJUnit4Tests;

@RunWithWebDriver(value = CHROME, options = { //
    @Option(key = WORK_DIRECTORY, value = "target"), //
    @Option(key = IMPLICITLY_WAIT, value = "500"), //
})
@WebDriverEntryPoint("http://www.google.com")
public class GoogleJUnitTest extends SeleniumJUnit4Tests {

  @Test
  public void calculatorTest() {
    type("2+2") //
        .on(By.id("lst-ib")) //
        .submit();

    expect(By.id("cwtltblr")) //
        .hasText("4");
  }

}

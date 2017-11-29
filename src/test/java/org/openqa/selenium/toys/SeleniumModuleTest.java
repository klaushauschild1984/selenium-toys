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
import static org.openqa.selenium.toys.Webdriver.IMPLICITLY_WAIT;
import static org.openqa.selenium.toys.factory.chrome.ChromeWebdriverFactory.WORK_DIRECTORY;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

@Webdriver(value = CHROME, options = { //
    @Option(key = WORK_DIRECTORY, value = "target"), //
    @Option(key = IMPLICITLY_WAIT, value = "500"), //
})
@EntryPoint("http://www.google.com")
public class SeleniumModuleTest extends SeleniumTestNGTests {

  @Test
  public void calculatorTest() {
    use(new GoogleSearch()).typeAndSubmit("2+2");

    use(new GooleSearchResult()).expect("4");
  }

  static class GoogleSearch extends SeleniumModule {

    void typeAndSubmit(final String text) {
      type(text) //
          .on(By.id("lst-ib")) //
          .submit();
    }

  }

  static class GooleSearchResult extends SeleniumModule {

    void expect(final String text) {
      expect(By.id("cwtltblr")) //
          .hasText(text);
    }

  }

}

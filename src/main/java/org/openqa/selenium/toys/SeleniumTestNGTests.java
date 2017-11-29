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

import java.lang.reflect.Method;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class SeleniumTestNGTests implements SeleniumApi {

  private final SeleniumTests seleniumTests = new SeleniumTests(getClass());

  @BeforeMethod
  public void before(final Method method) {
    seleniumTests.before(method);
  }

  @Override
  public WebDriver getWebDriver() {
    return seleniumTests.getWebDriver();
  }

  @Override
  public Type type(final String text) {
    return seleniumTests.type(text);
  }

  @Override
  public Expect expect(final By by) {
    return seleniumTests.expect(by);
  }

  @Override
  public void click(final By on) {
    seleniumTests.click(on);
  }

  @AfterMethod
  public void after(final ITestResult testResult) {
    final Method method = testResult.getMethod().getConstructorOrMethod().getMethod();
    seleniumTests.after(method, testResult.getStatus() == ITestResult.FAILURE,
        testResult.getThrowable());
  }

}

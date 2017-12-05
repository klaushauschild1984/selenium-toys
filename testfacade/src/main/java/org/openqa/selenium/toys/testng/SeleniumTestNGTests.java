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

package org.openqa.selenium.toys.testng;

import java.lang.reflect.Method;

import org.openqa.selenium.toys.SeleniumModule;
import org.openqa.selenium.toys.SeleniumTests;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class SeleniumTestNGTests extends SeleniumModule {

  public SeleniumTestNGTests() {
    setSeleniumTests(new SeleniumTests(getClass()));
  }

  @BeforeMethod
  public void before(final Method method) {
    seleniumTests.before(method);
  }

  @AfterMethod
  public void after(final ITestResult testResult) {
    final Method method = testResult.getMethod().getConstructorOrMethod().getMethod();
    seleniumTests.after(method, testResult.getStatus() == ITestResult.FAILURE,
        testResult.getThrowable());
  }

}

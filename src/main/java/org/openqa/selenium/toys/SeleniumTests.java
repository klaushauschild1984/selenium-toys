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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.toys.factory.DelegatingWebdriverFactory;
import org.openqa.selenium.toys.factory.WebdriverFactory;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @deprecated do not extends this class directly.
 */
@Deprecated
abstract class SeleniumTests {

  private WebdriverFactory webdriverFactory = new DelegatingWebdriverFactory();
  private WebDriver webDriver;
  private Screenshots screenshots;

  /**
   * @deprecated do not invoke this directly
   */
  @Deprecated
  protected void before(final Method method) {
    // create the web driver
    final Class<?> testClass = this.getClass();
    checkUniqueMethodNames(testClass);
    webDriver = webdriverFactory.create(testClass);

    // inject the entry point
    final EntryPoint entryPoint =
        Optional.ofNullable(AnnotationUtils.findAnnotation(testClass, EntryPoint.class)) //
            .orElseThrow(() -> new AssertionError(String.format(
                "Test class %s is not annotated with %s to specify the entry point of the test.",
                testClass.getName(), EntryPoint.class.getName())));
    webDriver.get(entryPoint.value());

    // take screenshots
    Optional.ofNullable(AnnotationUtils.findAnnotation(testClass, TakeScreenshots.class)) //
        .ifPresent(takeScreenshots -> {
          screenshots = new Screenshots(webDriver, takeScreenshots, getClass());
          try {
            screenshots.start(method.getName());
          } catch (final AssertionError assertionError) {
            after(method, true, assertionError);
            throw assertionError;
          }
        });
  }

  /**
   * @deprecated do not invoke this directly
   */
  @Deprecated
  public void after(final Method method, final boolean hasFailure, final Throwable cause) {
    if (screenshots != null) {
      if (hasFailure && !(cause instanceof Screenshots.ScreenshotAssertionError)) {
        screenshots.failure(method.getName());
      }
    }

    webDriver.quit();
  }

  /**
   * @deprecated Don't use {@link WebDriver} directly. It' at your own risk.
   */
  @Deprecated
  protected WebDriver getWebDriver() {
    return webDriver;
  }

  protected Type type(final String text) {
    waitForDocumentReady();
    final String methodName = getInvokingMethodName();
    return new Type(webDriver, text, getScreenshotTaker(methodName));
  }

  protected Expect expect(final By by) {
    waitForDocumentReady();
    return new Expect(webDriver, by);
  }

  protected void click(final By on) {
    waitForDocumentReady();
    final WebElement element = webDriver.findElement(on);
    element.click();
    getScreenshotTaker(getInvokingMethodName()).run();
  }

  private Runnable getScreenshotTaker(final String methodName) {
    return () -> {
      if (screenshots == null) {
        return;
      }
      waitForDocumentReady();
      screenshots.screenshot(methodName, null);
    };
  }

  private String getInvokingMethodName() {
    return Thread.currentThread().getStackTrace()[3].getMethodName();
  }

  private void checkUniqueMethodNames(final Class<?> testClass) {
    final List<String> methodNameList = Arrays.stream(testClass.getDeclaredMethods()) //
        .map(Method::getName) //
        .collect(Collectors.toList());
    final Set<String> methodNameSet = new HashSet<>(methodNameList);
    if (methodNameList.size() != methodNameSet.size()) {
      throw new IllegalStateException(String
          .format("There are not unique method names within test class %s", testClass.getName()));
    }
  }

  private void waitForDocumentReady() {
    final WebDriverWait wait = new WebDriverWait(webDriver, 30);

    wait.until((ExpectedCondition<Boolean>) driver -> ((JavascriptExecutor) driver)
        .executeScript("return document.readyState").equals("complete"));
    wait.until((ExpectedCondition<Boolean>) driver -> {
      try {
        return ((Long) ((JavascriptExecutor) driver).executeScript("return jQuery.active") == 0);
      } catch (Exception e) {
        // no jQuery present
        return true;
      }
    });
  }

}

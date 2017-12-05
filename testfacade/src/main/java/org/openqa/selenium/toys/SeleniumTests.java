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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.core.annotation.AnnotationUtils;

public class SeleniumTests implements SeleniumApi {

  private final Class<?> testClass;

  private WebDriver webDriver;
  private Screenshots screenshots;

  public SeleniumTests(final Class<?> clazz) {
    this.testClass = clazz;
    checkUniqueMethodNames(testClass);

    // register common web driver factories
    WebDriverFactoryRegistry.register(new ChromeWebDriverFactory());
  }

  public void before(final Method method) {
    // create the web driver
    final RunWithWebDriver runWithWebDriver = Optional
        .ofNullable(AnnotationUtils.findAnnotation(testClass, RunWithWebDriver.class)) //
        .orElseThrow(() -> new AssertionError(String.format(
            "Test class %s is not annotated with %s to specify which web driver should be used for running.",
            testClass.getName(), RunWithWebDriver.class.getName())));
    final WebDriverFactory webDriverFactory =
        WebDriverFactoryRegistry.getWebDriverFactory(runWithWebDriver.value());
    final Map<String, Object> options = Arrays.stream(runWithWebDriver.options()) //
        .collect(Collectors.toMap(Option::key, Option::value));
    webDriver = webDriverFactory.create(options);

    // inject the entry point
    final WebDriverEntryPoint entryPoint =
        Optional.ofNullable(AnnotationUtils.findAnnotation(testClass, WebDriverEntryPoint.class)) //
            .orElseThrow(() -> new AssertionError(String.format(
                "Test class %s is not annotated with %s to specify the entry point of the test.",
                testClass.getName(), WebDriverEntryPoint.class.getName())));
    webDriver.get(entryPoint.value());

    // setup screenshots
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

  public void after(final Method method, final boolean hasFailure, final Throwable cause) {
    if (screenshots != null) {
      if (hasFailure && !(cause instanceof Screenshots.ScreenshotAssertionError)) {
        screenshots.failure(method.getName());
      }
    }

    webDriver.quit();
  }

  @Override
  public WebDriver getWebDriver() {
    return webDriver;
  }

  @Override
  public Type type(final String text) {
    waitForDocumentReady();
    final String methodName = getInvokingMethodName();
    return new Type(webDriver, text, getScreenshotTaker(methodName));
  }

  @Override
  public Expect expect(final By by) {
    waitForDocumentReady();
    return new Expect(webDriver, by);
  }

  @Override
  public void click(final By on) {
    waitForDocumentReady();
    final WebElement element = webDriver.findElement(on);
    element.click();
    getScreenshotTaker(getInvokingMethodName()).run();
  }

  @Override
  public <T extends SeleniumModule> T use(final T seleniumModule) {
    seleniumModule.setSeleniumTests(this);
    return seleniumModule;
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
    // filter stack trace by class name
    final List<StackTraceElement> relevantStackTraceElements = Arrays
        .stream(Thread.currentThread().getStackTrace()).filter(stackTraceElement -> Objects
            .equals(stackTraceElement.getClassName(), testClass.getName()))
        .collect(Collectors.toList());
    // the last element is the invoking method
    return relevantStackTraceElements.get(relevantStackTraceElements.size() - 1).getMethodName();
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

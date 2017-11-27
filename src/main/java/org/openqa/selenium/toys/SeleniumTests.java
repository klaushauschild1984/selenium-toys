package org.openqa.selenium.toys;

import java.lang.reflect.Method;
import java.util.Optional;

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
          screenshots.start(method);
        });
  }

  /**
   * @deprecated do not invoke this directly
   */
  @Deprecated
  public void after(final Method method, final boolean hasFailure) {
    if (screenshots != null) {
      if (hasFailure) {
        screenshots.failure(method);
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
    return new Type(webDriver, text);
  }

  protected Expect expect(final By by) {
    waitForDocumentReady();
    return new Expect(webDriver, by);
  }

  protected void click(final By on) {
    waitForDocumentReady();
    final WebElement element = webDriver.findElement(on);
    element.click();
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

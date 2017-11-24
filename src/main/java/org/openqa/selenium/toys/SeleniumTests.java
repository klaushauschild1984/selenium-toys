package org.openqa.selenium.toys;

import java.lang.reflect.Method;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.toys.factory.DelegatingWebDriverFactory;
import org.openqa.selenium.toys.factory.WebDriverFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class SeleniumTests {

  private WebDriverFactory webDriverFactory = new DelegatingWebDriverFactory();
  private WebDriver webDriver;
  private Screenshots screenshots;

  @BeforeMethod
  public void before(final Method method) {
    // create the web driver
    final Class<?> testClass = this.getClass();
    webDriver = webDriverFactory.create(testClass);

    // inject the entry point
    final EntryPoint entryPointAnnotation =
        AnnotationUtils.findAnnotation(testClass, EntryPoint.class);
    if (entryPointAnnotation == null) {
      throw new AssertionError(String.format(
          "Test class %s is not annotated with %s to specify the entry point of the test.",
          testClass.getName(), EntryPoint.class.getName()));
    }
    webDriver.get(entryPointAnnotation.value());

    // take screenshots
    final TakeScreenshots takeScreenshotAnnotation =
        AnnotationUtils.findAnnotation(testClass, TakeScreenshots.class);
    if (takeScreenshotAnnotation != null) {
      screenshots = new Screenshots(webDriver, takeScreenshotAnnotation, getClass());
      screenshots.start();
    }
  }

  @AfterMethod
  public void after(final ITestResult testResult) {
    if (screenshots != null) {
      if (testResult.getStatus() == ITestResult.FAILURE) {
        screenshots.failure();
      } else {
        screenshots.finish();
      }
    }

    webDriver.close();
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

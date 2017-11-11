package de.hauschild.selenium.toys;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.core.annotation.AnnotationUtils;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import com.google.common.collect.Lists;

import de.hauschild.selenium.toys.factory.DelegatingWebDriverFactory;
import de.hauschild.selenium.toys.factory.WebDriverFactory;
import de.hauschild.selenium.toys.reporter.Reporter;

@Listeners({Reporter.class})
public abstract class SeleniumTests {

  private final ThreadLocal<List<Entry<String, BufferedImage>>> screenshots =
      ThreadLocal.withInitial(Lists::newArrayList);

  private WebDriverFactory webDriverFactory = new DelegatingWebDriverFactory();
  private WebDriver webDriver;

  @BeforeMethod
  public void before() {
    // create the web driver
    final Class<?> testClass = this.getClass();
    webDriver = webDriverFactory.create(testClass);

    // inject the entry point
    final EntryPoint entryPointAnnotation =
        AnnotationUtils.findAnnotation(testClass, EntryPoint.class);
    if (entryPointAnnotation == null) {
      Assert.fail(String.format(
          "Test class %s is not annotated with %s to specify the entry point of the test.",
          testClass.getName(), EntryPoint.class.getName()));
    }
    webDriver.get(entryPointAnnotation.value());
  }

  @AfterMethod
  public void after(final ITestResult testResult) throws IOException {
    takeScreenshotOnFailure(testResult);
    testResult.setAttribute("screenshots", screenshots.get());

    webDriver.close();
  }

  private void takeScreenshotOnFailure(final ITestResult testResult) throws IOException {
    if (testResult.getStatus() != ITestResult.FAILURE) {
      return;
    }
    screenshot("failure");
  }

  protected void screenshot(final String label) {
    final BufferedImage screenshot = screenshot();
    screenshots.get().add(new SimpleEntry<>(label, screenshot));
  }

  private BufferedImage screenshot() {
    if (!(webDriver instanceof TakesScreenshot)) {
      throw new IllegalStateException(
          String.format("Web driver %s not capable to take screen shots", webDriver));
    }
    final byte[] screenshotBytes = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
    try {
      return ImageIO.read(new ByteArrayInputStream(screenshotBytes));
    } catch (final IOException exception) {
      throw new RuntimeException(exception);
    }
  }

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

    wait.until((ExpectedCondition<Boolean>) webDriver -> ((JavascriptExecutor) webDriver)
        .executeScript("return document.readyState").equals("complete"));
    wait.until((ExpectedCondition<Boolean>) webDriver -> {
      try {
        return ((Long) ((JavascriptExecutor) webDriver).executeScript("return jQuery.active") == 0);
      } catch (Exception e) {
        // no jQuery present
        return true;
      }
    });
  }

}

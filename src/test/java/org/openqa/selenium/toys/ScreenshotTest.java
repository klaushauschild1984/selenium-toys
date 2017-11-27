package org.openqa.selenium.toys;

import static org.openqa.selenium.remote.BrowserType.CHROME;
import static org.openqa.selenium.toys.factory.chrome.ChromeWebdriverFactory.WORK_DIRECTORY;

import org.testng.annotations.Test;

@Webdriver(value = CHROME, options = { //
    WORK_DIRECTORY, "target", //
})
@EntryPoint("http://www.google.com")
@TakeScreenshots(baseDirectory = "target/screenshots")
public class ScreenshotTest extends SeleniumTestNGTests {

  @Test
  public void test() {

  }

}

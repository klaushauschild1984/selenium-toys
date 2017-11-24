package org.openqa.selenium.toys;

import static org.openqa.selenium.remote.BrowserType.CHROME;
import static org.openqa.selenium.toys.factory.AbstractWebDriverFactory.IMPLICITLY_WAIT;

import org.openqa.selenium.By;

@WebDriver(value = CHROME, options = { //
    IMPLICITLY_WAIT, "500", //
})
@EntryPoint("http://www.google.com")
@TakeScreenshots(baseDirectory = "C:\\Workspaces\\_\\selenium-toys\\target\\screenshots")
public class GoogleTest extends SeleniumTests {

  // @Test
  public void failingCalculatorTest() {
    type("2+2") //
        .on(By.id("lst-ib")) //
        .submit();

    expect(By.id("cwtltblr")) //
        .hasText("5");
  }

}

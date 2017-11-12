package de.hauschild.selenium.toys;

import static de.hauschild.selenium.toys.factory.AbstractWebDriverFactory.IMPLICITLY_WAIT;
import static org.openqa.selenium.remote.BrowserType.CHROME;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

@WebDriver(value = CHROME, options = { //
    IMPLICITLY_WAIT, "500", //
})
@EntryPoint("http://www.google.com")
public class GoogleTest extends SeleniumTests {

  @Test
  public void failingCalculatorTest() {
    screenshot("start");

    type("2+2") //
        .on(By.id("lst-ib")) //
        .submit();

    screenshot("2plus2");

    expect(By.id("cwtltblr")) //
        .hasText("5");

    screenshot("equals4");
  }

}

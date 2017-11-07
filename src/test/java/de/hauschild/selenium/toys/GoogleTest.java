package de.hauschild.selenium.toys;

import static de.hauschild.selenium.toys.WebDriver.IMPLICITLY_WAIT;
import static de.hauschild.selenium.toys.factory.chrome.ChromeWebDriverFactory.CHROME;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

@WebDriver(value = CHROME, options = { //
    IMPLICITLY_WAIT, "500", //
})
@EntryPoint("http://www.google.com")
public class GoogleTest extends SeleniumTests {

  @Test
  public void failingCalculatorTest() {
    type("2+2") //
        .on(By.id("lst-ib")) //
        .submit();

    expect(By.id("cwtltblr")) //
        .hasText("5");
  }

}

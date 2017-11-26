package org.openqa.selenium.toys;

import static org.openqa.selenium.remote.BrowserType.CHROME;
import static org.openqa.selenium.toys.Webdriver.IMPLICITLY_WAIT;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

@Webdriver(value = CHROME, options = { //
    IMPLICITLY_WAIT, "500", //
})
@EntryPoint("http://www.google.com")
public class GoogleTest extends SeleniumTestNGTests {

  @Test
  public void failingCalculatorTest() {
    type("2+2") //
        .on(By.id("lst-ib")) //
        .submit();

    expect(By.id("cwtltblr")) //
        .hasText("4");
  }

}

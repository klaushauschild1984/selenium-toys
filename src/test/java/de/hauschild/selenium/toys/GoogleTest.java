package de.hauschild.selenium.toys;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

@WebDriver(value = WebDriver.CHROME, implicitlyWait = 500)
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

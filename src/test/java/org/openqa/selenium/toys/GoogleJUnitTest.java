package org.openqa.selenium.toys;

import static org.openqa.selenium.remote.BrowserType.CHROME;
import static org.openqa.selenium.toys.Webdriver.IMPLICITLY_WAIT;
import static org.openqa.selenium.toys.factory.chrome.ChromeWebdriverFactory.WORK_DIRECTORY;

import org.junit.Test;
import org.openqa.selenium.By;

@Webdriver(value = CHROME, options = { //
    WORK_DIRECTORY, "target", //
    IMPLICITLY_WAIT, "500", //
})
@EntryPoint("http://www.google.com")
public class GoogleJUnitTest extends SeleniumJUnit4Tests {

  @Test
  public void calculatorTest() {
    type("2+2") //
        .on(By.id("lst-ib")) //
        .submit();

    expect(By.id("cwtltblr")) //
        .hasText("5");
  }

}

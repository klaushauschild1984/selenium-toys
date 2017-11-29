package org.openqa.selenium.toys;

import static org.openqa.selenium.remote.BrowserType.CHROME;
import static org.openqa.selenium.toys.Webdriver.IMPLICITLY_WAIT;
import static org.openqa.selenium.toys.factory.chrome.ChromeWebdriverFactory.WORK_DIRECTORY;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

@Webdriver(value = CHROME, options = { //
    @Option(key = WORK_DIRECTORY, value = "target"), //
    @Option(key = IMPLICITLY_WAIT, value = "500"), //
})
@EntryPoint("http://www.google.com")
public class SeleniumModuleTest extends SeleniumTestNGTests {

  @Test
  public void calculatorTest() {
    use(new GoogleSearch()).typeAndSubmit("2+2");

    use(new GooleSearchResult()).expect("4");
  }

  static class GoogleSearch extends SeleniumModule {

    void typeAndSubmit(final String text) {
      type(text) //
          .on(By.id("lst-ib")) //
          .submit();
    }

  }

  static class GooleSearchResult extends SeleniumModule {

    void expect(final String text) {
      expect(By.id("cwtltblr")) //
          .hasText(text);
    }

  }

}

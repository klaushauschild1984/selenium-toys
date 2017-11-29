package org.openqa.selenium.toys;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public abstract class SeleniumModule implements SeleniumApi {

  SeleniumTests seleniumTests;

  void setSeleniumTests(final SeleniumTests seleniumTests) {
    this.seleniumTests = seleniumTests;
  }

  @Override
  public WebDriver getWebDriver() {
    return seleniumTests.getWebDriver();
  }

  @Override
  public Type type(final String text) {
    return seleniumTests.type(text);
  }

  @Override
  public Expect expect(final By by) {
    return seleniumTests.expect(by);
  }

  @Override
  public void click(final By on) {
    seleniumTests.click(on);
  }

  @Override
  public <T extends SeleniumModule> T use(final T seleniumModule) {
    return seleniumTests.use(seleniumModule);
  }

}

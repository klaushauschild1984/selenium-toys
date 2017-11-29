package org.openqa.selenium.toys;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public interface SeleniumApi {

  /**
   * @deprecated If you really need it, here it is. But blame not me if something works not
   *             probably.
   */
  @Deprecated
  WebDriver getWebDriver();

  Type type(final String text);

  Expect expect(final By by);

  void click(final By on);

  <T extends SeleniumModule> T use(final T seleniumModule);

}

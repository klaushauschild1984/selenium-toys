package org.openqa.selenium.toys.factory.firefox;

import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.toys.Webdriver;
import org.openqa.selenium.toys.factory.AbstractWebdriverFactory;

public class FirefoxWebdriverFactory extends AbstractWebdriverFactory {

  public FirefoxWebdriverFactory() {
    super(BrowserType.FIREFOX);
  }

  @Override
  protected WebDriver create(final Class<?> testClass, final Webdriver webdriver,
      final Map<String, String> options) {
    final FirefoxProfile firefoxProfile = new FirefoxProfile();
    final FirefoxOptions firefoxOptions = new FirefoxOptions();
    firefoxOptions.setProfile(firefoxProfile);
    return new FirefoxDriver(firefoxOptions);
  }

}

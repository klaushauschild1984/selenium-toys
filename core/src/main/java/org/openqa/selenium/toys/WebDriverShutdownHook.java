package org.openqa.selenium.toys;

import org.openqa.selenium.WebDriver;

public class WebDriverShutdownHook {

  private final WebDriver webDriver;

  public WebDriverShutdownHook(final WebDriver webDriver) {
    this.webDriver = webDriver;
  }

  public void install() {
    new Initialization(() -> {
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        webDriver.quit();
      }));
    }).initialize();
  }

}

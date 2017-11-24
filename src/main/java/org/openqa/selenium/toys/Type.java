package org.openqa.selenium.toys;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

class Type {

  private final WebDriver webDriver;
  private final String text;

  Type(final WebDriver webDriver, final String text) {
    this.webDriver = webDriver;
    this.text = text;
  }

  public Submit on(final By by) {
    final WebElement element = webDriver.findElement(by);
    element.sendKeys(text);
    return new Submit(element);
  }

}

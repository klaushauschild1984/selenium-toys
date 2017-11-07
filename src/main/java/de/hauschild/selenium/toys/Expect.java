package de.hauschild.selenium.toys;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

class Expect {

  private final WebDriver webDriver;
  private final By by;

  Expect(final WebDriver webDriver, final By by) {
    this.webDriver = webDriver;
    this.by = by;
  }

  public void hasText(final String expectedText) {
    final WebElement element = webDriver.findElement(by);
    final String text = element.getText();
    Assert.assertEquals(text, expectedText);
  }
}

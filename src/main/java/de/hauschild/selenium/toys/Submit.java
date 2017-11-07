package de.hauschild.selenium.toys;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

class Submit {

  private final WebElement element;

  Submit(final WebElement element) {
    this.element = element;
  }

  public void enter() {
    element.sendKeys(Keys.ENTER);
  }

  public void submit() {
    element.submit();
  }

}

package org.openqa.selenium.toys;

import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class TypeTest {

  @Test
  public void onTest() {
    final WebDriver webDriver = Mockito.mock(WebDriver.class);
    final WebElement webElement = Mockito.mock(WebElement.class);

    final Type type = new Type(webDriver, "text");
    final By byId = By.id("id");
    Mockito.when(webDriver.findElement(byId)).thenReturn(webElement);
    type.on(byId);
    Mockito.verify(webElement).sendKeys("text");
  }

}

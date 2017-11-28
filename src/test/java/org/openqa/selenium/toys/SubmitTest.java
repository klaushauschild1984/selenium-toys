package org.openqa.selenium.toys;

import org.mockito.Mockito;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class SubmitTest {

  @Test
  public void submitTest() {
    final WebElement webElement = Mockito.mock(WebElement.class);
    final Submit submit = new Submit(webElement);
    submit.submit();
    Mockito.verify(webElement).submit();
  }

  @Test
  public void enterTest() {
    final WebElement webElement = Mockito.mock(WebElement.class);
    final Submit submit = new Submit(webElement);
    submit.enter();
    Mockito.verify(webElement).sendKeys(Keys.ENTER);
  }

}

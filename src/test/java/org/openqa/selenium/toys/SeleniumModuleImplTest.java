package org.openqa.selenium.toys;

import org.testng.annotations.Test;

public class SeleniumModuleImplTest {

  /**
   * This test ensures that the abstract {@link SeleniumModule} implements all methods defined by
   * {@link SeleniumApi}.
   */
  @Test
  public void verifySeleniumModuleImplementsAllMethods() {
    new SeleniumModule() {

    };
  }

}

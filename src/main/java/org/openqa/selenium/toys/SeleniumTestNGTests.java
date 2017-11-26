package org.openqa.selenium.toys;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class SeleniumTestNGTests extends SeleniumTests {

  @BeforeMethod
  public void before() {
    super.before();
  }

  @AfterMethod
  public void after(final ITestResult testResult) {
    after(testResult.getStatus() == ITestResult.FAILURE);
  }

}

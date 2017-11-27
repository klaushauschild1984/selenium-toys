package org.openqa.selenium.toys;

import java.lang.reflect.Method;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class SeleniumTestNGTests extends SeleniumTests {

  @BeforeMethod
  public void before(final Method method) {
    super.before(method);
  }

  @AfterMethod
  public void after(final ITestResult testResult) {
    final Method method = testResult.getMethod().getConstructorOrMethod().getMethod();
    after(method, testResult.getStatus() == ITestResult.FAILURE);
  }

}

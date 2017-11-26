package org.openqa.selenium.toys;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestWatcher;

public abstract class SeleniumJUnit4Tests extends SeleniumTests {

  @Rule
  public AfterWithResultRule afterWithResultRule = new AfterWithResultRule(this);

  @Before
  public void before() {
    super.before();
  }

  @AfterWithResult
  public void after(final boolean success) {
    super.after(!success);
  }

  @Retention(RUNTIME)
  @Target(METHOD)
  @interface AfterWithResult {
  }

  class AfterWithResultRule extends TestWatcher {

    private final Object testClassInstance;

    AfterWithResultRule(final Object testClassInstance) {
      this.testClassInstance = testClassInstance;
    }

    protected void succeeded(final Description description) {
      invokeAfterHackMethods(true);
    }

    protected void failed(final Throwable e, final Description description) {
      invokeAfterHackMethods(false);
    }

    void invokeAfterHackMethods(final boolean successful) {
      for (final Method afterHackMethod : getMethods(this.testClassInstance.getClass())) {
        try {
          afterHackMethod.invoke(this.testClassInstance, successful);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
          throw new RuntimeException("error while invoking afterHackMethod " + afterHackMethod);
        }
      }
    }

    private List<Method> getMethods(final Class<?> testClass) {
      return Arrays.stream(testClass.getMethods()) //
          .filter(method -> method.isAnnotationPresent(AfterWithResult.class)) //
          .collect(Collectors.toList());
    }
  }

}

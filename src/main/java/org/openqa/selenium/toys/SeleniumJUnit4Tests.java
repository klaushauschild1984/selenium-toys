package org.openqa.selenium.toys;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.rules.TestWatcher;

public abstract class SeleniumJUnit4Tests extends SeleniumTests {

  @Rule
  public JUnitBeforeAndAfterHandler junitBeforeAndAfterHandler =
      new JUnitBeforeAndAfterHandler(this);

  @JUnitBefore
  public void before(final Method method) {
    super.before(method);
  }

  @JUnitAfter
  public void after(final Method method, final boolean success) {
    super.after(method, !success);
  }

  @Retention(RUNTIME)
  @Target(METHOD)
  @interface JUnitBefore {
  }

  @Retention(RUNTIME)
  @Target(METHOD)
  @interface JUnitAfter {
  }

  class JUnitBeforeAndAfterHandler extends TestWatcher {

    private final Object testInstance;

    JUnitBeforeAndAfterHandler(final Object testInstance) {
      this.testInstance = testInstance;
    }

    @Override
    protected void starting(final org.junit.runner.Description description) {
      for (final Method method : getMethods(testInstance.getClass(), JUnitBefore.class)) {
        try {
          method.invoke(testInstance, method);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
          throw new RuntimeException("error while invoking method " + method);
        }
      }
    }

    protected void succeeded(final Description description) {
      invokeAfterMethods(true);
    }

    protected void failed(final Throwable e, final Description description) {
      invokeAfterMethods(false);
    }

    void invokeAfterMethods(final boolean successful) {
      for (final Method method : getMethods(testInstance.getClass(), JUnitAfter.class)) {
        try {
          method.invoke(testInstance, method, successful);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
          throw new RuntimeException("error while invoking method " + method);
        }
      }
    }

    private List<Method> getMethods(final Class<?> testClass,
        final Class<? extends Annotation> annotationType) {
      return Arrays.stream(testClass.getMethods()) //
          .filter(method -> method.isAnnotationPresent(annotationType)) //
          .collect(Collectors.toList());
    }
  }

}

/*
 * Selenium Toys Copyright (C) 2017 Klaus Hauschild
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package org.openqa.selenium.toys.junit4;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

class JUnitBeforeAndAfterHandler extends TestWatcher {

  private final Object testInstance;

  JUnitBeforeAndAfterHandler(final Object testInstance) {
    this.testInstance = testInstance;
  }

  @Override
  protected void starting(final Description description) {
    for (final Method method : getMethods(testInstance.getClass(), JUnitBefore.class)) {
      try {
        method.invoke(testInstance, method);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        throw new RuntimeException("error while invoking method " + method);
      }
    }
  }

  @Override
  protected void succeeded(final Description description) {
    invokeAfterMethods(true, null);
  }

  @Override
  protected void failed(final Throwable cause, final Description description) {
    invokeAfterMethods(false, cause);
  }

  private void invokeAfterMethods(final boolean successful, final Throwable cause) {
    for (final Method method : getMethods(testInstance.getClass(), JUnitAfter.class)) {
      try {
        method.invoke(testInstance, method, successful, cause);
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

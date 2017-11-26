package org.openqa.selenium.toys;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openqa.selenium.remote.BrowserType;

/**
 * Test classes extending {@link SeleniumTests} use this annotation to specify the underlying
 * implementation for web tests.<br/>
 * Depending on the used implementation additional setup will be performed.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Webdriver {

  public static final String IMPLICITLY_WAIT = "implicitlyWait";

  /**
   * Valid values are the constants defined in {@link BrowserType}.
   * 
   * @return the name of the web driver
   */
  String value();

  String[] options() default {};

}

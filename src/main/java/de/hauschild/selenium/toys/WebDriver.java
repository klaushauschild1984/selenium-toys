package de.hauschild.selenium.toys;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Test classes extending {@link SeleniumTests} use this annotation to specify the underlying
 * implementation for web tests.<br/>
 * Supported implementations are:
 * <ul>
 * <li>chrome</li>
 * </ul>
 * Depending on the used implementation additional setup will be performed.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WebDriver {

  String CHROME = "chrome";
  long NOT_SET = -1;

  String value();

  long implicitlyWait() default NOT_SET;

}

# Selenium Toys [![Build Status](https://travis-ci.org/klaushauschild1984/selenium-toys.svg?branch=master)](https://travis-ci.org/klaushauschild1984/selenium-toys) [![Quality Gate](https://sonarcloud.io/api/badges/gate?key=de.hauschild.selenium-toys%3Aselenium-toys)](https://sonarcloud.io/dashboard?id=de.hauschild.selenium-toys%3Aselenium-toys)


This project provides a rich toolbox for easy writing [Selenium](http://www.seleniumhq.org/) test cases.

## Usage Example

```java
import static org.openqa.selenium.remote.BrowserType.CHROME;

import org.openqa.selenium.toys.Webdriver;
import org.openqa.selenium.toys.EntryPoint;
import org.openqa.selenium.toys.SeleniumTestNGTests;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

@Webdriver(value = CHROME)
@EntryPoint("http://www.google.com")
public class GoogleTest extends SeleniumTestNGTests {

  @Test
  public void calculatorTest() {
    type("2+2") //
        .on(By.id("lst-ib")) //
        .submit();

    expect(By.id("cwtltblr")) //
        .hasText("5");
  }

}
```
The above code demonstrates the easy usage. For setup just extend `SeleniumTests` and annotate your test with
`@Webdriver` and `@EntryPoint`. This annotations define the use selenium web driver and entry point of the test case.
The specified web driver will be automatically downloaded and installed. For further details see the corresponding
javadoc of the web driver factory. There are a set of common settings for each web driver and each web driver factory
can gives you the opportunity to make web driver specific configurations.

Within the test you have access to a fluently designed api for writing readable tests. In this case `Google` will be
opened and its calculator capabilities are tested. We type `2+2` into the input field and expect stupidly `5` as the
result.

## Maven integration

To use Selenium Toys just add the following repository and dependency to you `pom.xml`:
```xml
<project>
...
  <dependencies>
    <dependency>
      <groupId>org.seleniumhq.selenium-toys</groupId>
      <artifactId>selenium-toys</artifactId>
      <version>1.0-SNAPSHOT</version>
    </dependency>
  </dependencies>
  ...
  <repositories>
    <repository>
      <id>selenium-toys-repo</id>
      <name>Selenium Toys Repository</name>
      <url>https://github.com/klaushauschild1984/selenium-toys/tree/mvn-repo</url>
    </repository>
  </repositories>
...
</project>
```

## Supported test backends

There is no hard dependency to only one test backend like [TestNG](http://testng.org) or [JUnit](http://junit.org).
Extend and use `SeleniumTestNGTests` if you prefer TestNG. Extend and use `SeleniumJUnit4Tests` if you a JUnit4 guy.

## Screenshot compare

Writing maintainable UI tests is one big part. Defining expectations is the other big one. Selenium Toy provide through
Selenium functionality for DOM based text expectations. If you will go further and expect layout and UI behavior it is
nearly impossible to formulate those expectations.

For this scenarios there is the screenshot compare functionality. After every step a screenshot will be taken. This
screenshots will be compared to corresponding screenshot from previous test runs. If there is a difference between two
screenshots the test case will fail.

The intended way is that you will make an initial run and verify the taken screenshots by human inspection. The set of
screenshot has to be stored on a shared place where later test runs can access and compare them.

**This feature is still WORK IN PROGRESS**

## Supported WebDrivers

The `@Webdriver` annotation offers an `options` attribute. It a list of strings and will interpreted as map that has
its key value pairs lined up in one row.

* `Webdriver.IMPLICITLY_WAIT`

  Specifies the amount of milliseconds the driver should wait when searching for an element if it is not immediately
  present.

### [Chrome](https://sites.google.com/a/chromium.org/chromedriver/downloads)

* driver executable will be automatically downloaded and installed for Selenium. No extra configuration needed
* is the executable already locally present then this will be used
* `ChromeWebdriverFactory.EXPECTED_VERSION`

  With this option you can specify the version of the executable if you need. Omit this option and the latest version
  of the executable will be downloaded and used. 

### [PhantomJS](https://github.com/detro/ghostdriver)

* basic auto download support for fixed version

## Known limitations

* Currently there is no elegant way the modularize tests build with Selenium Toys. I work hard on that!
* A WebdriverFactory has to be explicitly registered at `DelegatingWebdriverFactory`. That makes it hard to write an
own factory.
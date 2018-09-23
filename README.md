# Selenium Toys [![Build Status](https://travis-ci.org/klaushauschild1984/selenium-toys.svg?branch=master)](https://travis-ci.org/klaushauschild1984/selenium-toys)

This project provides a rich toolbox for easy writing [Selenium](http://www.seleniumhq.org/) test cases.

## Components

Selenium Toys are currently divided into two parts:
* core
  * [![Quality Gate](https://sonarcloud.io/api/badges/gate?key=org.seleniumhq.selenium-toys%3Acore)](https://sonarcloud.io/dashboard?id=org.seleniumhq.selenium-toys%3Aselenium-toys)
* testfacade
  * [![Quality Gate](https://sonarcloud.io/api/badges/gate?key=org.seleniumhq.selenium-toys%3Atestfacade)](https://sonarcloud.io/dashboard?id=org.seleniumhq.selenium-toys%3Aselenium-toys)

`core` contains all parts for simple handling Selenium `WebDriver`. At `testfacade` you find test backend integrations
and a small but effective API for executing tests and expecting the results.

## Child's play example

```java
import static org.openqa.selenium.remote.BrowserType.CHROME;

import org.openqa.selenium.toys.RunWithWebDriver;
import org.openqa.selenium.toys.WebDriverEntryPoint;
import org.openqa.selenium.toys.testng.SeleniumTestNGTests;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

@RunWithWebDriver(value = CHROME)
@WebDriverEntryPoint("http://www.google.com")
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
      <artifactId>testfacade</artifactId>
      <version>2.0</version>
    </dependency>
  </dependencies>
  ...
  <repositories>
    <repository>
      <id>selenium-toys-repo</id>
      <name>Selenium Toys Repository</name>
      <url>https://raw.githubusercontent.com/klaushauschild1984/selenium-toys/mvn-repo/</url>
      <snapshots>
        <enabled>true</enabled>
        <updatePolicy>always</updatePolicy>
      </snapshots>
    </repository>
  </repositories>
...
</project>
```

## Supported test backends

There is no hard dependency to only one test backend like [TestNG](http://testng.org) or [JUnit](http://junit.org).
Extend and use `SeleniumTestNGTests` if you prefer TestNG. Extend and use `SeleniumJUnit4Tests` if you are a JUnit4 guy.

## Take screenshots

Writing maintainable UI tests is one big part. Defining expectations is the other big one. Selenium Toy provide through
Selenium functionality for DOM based text expectations. If you will go further and expect layout and UI behavior it is
nearly impossible to formulate those expectations.

In the first place just add the `@TakeScreenshots` annotation to your test and for every test step a screenshot will be
taken. If an assertion fails the causing situation is also documented via an additional screenshot named
`XXX-failure.png`.

In addition you can enable the comparison to already existing screenshots. If so at every test step a screenshot is
taken and compared to an already existing screenshot. The comparison is calculated by counting the different pixel
within the two images. If the threshold is surpassed the test will fail. Two addition files `XXX-new.png` and
`XXX-diff.png` will be generated to help you figuring out the problem.

## Supported WebDrivers

The `@RunWithWebDriver` annotation specifies the used web driver for the annotated test case. The extended test bridge
performs the complete Selenium setup and configuration of the web driver. There is a attribute `options` which you can
provide additional web driver configurations.

* `RunWithWebDriver.IMPLICITLY_WAIT`

  Specifies the amount of milliseconds the driver should wait when searching for an element if it is not immediately
  present.

* `AbstractDownloadingWebDriverFactory.EXPECTED_VERSION`

  With this option you can specify the version of the executable if you need. Omit this option and the latest version
  of the executable will be downloaded and used. 
  
* `AbstractDownloadingWebDriverFactory.WORK_DIRECTORY`

  Via this option the working directory for the web driver is specified. There will be the executable located and if
  not present then downloaded. Leave it unspecified to use the systems temporary directory.

* `AbstractDownloadingWebDriverFactory.FORCE_UPDATE`

  If there is already a local copy of the web driver executable this option controls if a check for an available newer
  version and if present a download is performed.

### [Chrome](https://sites.google.com/a/chromium.org/chromedriver/downloads) - `BrowserType.CHROME`

The web driver for Google Chrome.

### Custom web driver factory

You can implement you own `WebdriverFactory` to provide support an not included web driver. After the implementation the
class has to be registered at `DelegatingWebdriverFactory`. Every web driver factory is determined by its supported
browser type. Only one per type is allowed. But there is the possibility to override already registered factories. The
last registered factory will be used. At all cost you have to manage the the factory registration happens **before**
any test will be executed. Otherwise the test setup will fail.

## Modularize tests

There is a concept of modularization. Maybe you working on a test suite for a rich web application. It is possible to have similar test steps in many tests. To prevent code duplications and help maintain your tests use
`SeleniumModule`. By extending this class you are provided with the same api for test steps. The example below
modularize the usage example. Best practice is that a module has one method that performs the test steps. Within the
test you have to use it nd perform its action.

```java
import static org.openqa.selenium.remote.BrowserType.CHROME;

import org.openqa.selenium.toys.RunWithWebDriver;
import org.openqa.selenium.toys.WebDriverEntryPoint;
import org.openqa.selenium.toys.testng.SeleniumTestNGTests;
import org.openqa.selenium.toys.SeleniumModule;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

@RunWithWebDriver(value = CHROME)
@WebDriverEntryPoint("http://www.google.com")
public class SeleniumModuleTest extends SeleniumTestNGTests {

  @Test
  public void calculatorTest() {
    use(new GoogleSearch()).typeAndSubmit("2+2");

    use(new GooleSearchResult()).expect("4");
  }

  static class GoogleSearch extends SeleniumModule {

    void typeAndSubmit(final String text) {
      type(text) //
          .on(By.id("lst-ib")) //
          .submit();
    }

  }

  static class GooleSearchResult extends SeleniumModule {

    void expect(final String text) {
      expect(By.id("cwtltblr")) //
          .hasText(text);
    }

  }

}
```

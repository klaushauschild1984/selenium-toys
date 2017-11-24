# Selenium Toys [![Build Status](https://travis-ci.org/klaushauschild1984/selenium-toys.svg?branch=master)](https://travis-ci.org/klaushauschild1984/selenium-toys) [![Quality Gate](https://sonarcloud.io/api/badges/gate?key=de.hauschild.selenium-toys%3Aselenium-toys)](https://sonarcloud.io/dashboard?id=de.hauschild.selenium-toys%3Aselenium-toys)


This project provides a rich toolbox for easy writing [selenium](http://www.seleniumhq.org/) test cases.

## Usage Example

```java
import static org.openqa.selenium.remote.BrowserType.CHROME;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

@WebDriver(value = CHROME)
@EntryPoint("http://www.google.com")
public class GoogleTest extends SeleniumTests {

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
`@WebDriver` and `@EntryPoint`. This annotations define the use selenium web driver and entry point of the test case.
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
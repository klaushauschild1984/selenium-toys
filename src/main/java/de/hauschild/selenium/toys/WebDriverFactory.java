package de.hauschild.selenium.toys;

import org.openqa.selenium.WebDriver;

interface WebDriverFactory {

  WebDriver create(Class<?> testClass);

}

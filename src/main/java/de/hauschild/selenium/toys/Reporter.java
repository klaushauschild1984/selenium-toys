package de.hauschild.selenium.toys;

import java.util.List;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;

public class Reporter implements IReporter {

  @Override
  public void generateReport(final List<XmlSuite> xmlSuites, final List<ISuite> suites,
      final String outputDirectory) {
    System.out.println("It's alive!!!");
  }

}

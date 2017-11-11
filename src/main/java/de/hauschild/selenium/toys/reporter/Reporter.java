package de.hauschild.selenium.toys.reporter;

import java.util.List;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.TestListenerAdapter;
import org.testng.xml.XmlSuite;

public class Reporter extends TestListenerAdapter implements IReporter {

  @Override
  public void generateReport(final List<XmlSuite> xmlSuites, final List<ISuite> suites,
      final String outputDirectory) {}

}

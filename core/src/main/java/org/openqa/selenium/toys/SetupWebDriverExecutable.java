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

package org.openqa.selenium.toys;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetupWebDriverExecutable {

  private static final Logger LOGGER = LoggerFactory.getLogger(SetupWebDriverExecutable.class);

  private final String systemPropertyForExecutable;
  private final File webDriverExecutable;

  public SetupWebDriverExecutable(final String systemPropertyForExecutable,
      final File webDriverExecutable) {
    this.systemPropertyForExecutable = systemPropertyForExecutable;
    this.webDriverExecutable = webDriverExecutable;
  }

  public void setup() {
    LOGGER.debug("Setup web driver executable {}", webDriverExecutable);
    System.setProperty(systemPropertyForExecutable, webDriverExecutable.getAbsolutePath());
  }

}

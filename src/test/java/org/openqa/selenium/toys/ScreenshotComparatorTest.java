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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ScreenshotComparatorTest {

  public static final File FIRST =
      new File("src\\test\\resources\\org\\openqa\\selenium\\toys\\000.png");
  public static final File SECOND =
      new File("src\\test\\resources\\org\\openqa\\selenium\\toys\\001.png");

  @Test
  public void compareDifferentTest() throws Exception {
    final BufferedImage first = ImageIO.read(FIRST);
    final BufferedImage second = ImageIO.read(SECOND);

    Assert.assertTrue(new ScreenshotComparator(image -> {
      try {
        ImageIO.write(image, "png", new File("target\\diff.png"));
      } catch (final IOException exception) {
        throw new RuntimeException(exception);
      }
    }).compare(first, second) != 0);
  }

  @Test
  public void compareIdenticalTest() throws Exception {
    final BufferedImage first = ImageIO.read(FIRST);
    final BufferedImage second = ImageIO.read(FIRST);

    Assert.assertTrue(new ScreenshotComparator(image -> {
      Assert.fail();
    }).compare(first, second) == 0);
  }

}

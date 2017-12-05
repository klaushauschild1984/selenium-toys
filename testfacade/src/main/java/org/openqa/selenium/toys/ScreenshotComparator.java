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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.function.Consumer;

/**
 * Compare two {@link BufferedImage images}. Its {@link #compare(BufferedImage, BufferedImage)}
 * method return only <code>0</code> for identity. If the images are different the result represents
 * the negative count of different pixels.
 * <p>
 * If you provide a difference consumer you will get an difference image at the end of the
 * comparison.
 * </p>
 */
class ScreenshotComparator implements Comparator<BufferedImage> {

  /**
   * Builds a greyscale difference image. White pixel are identical. The darker the pixel the
   * greater the color difference.
   */
  private static final DifferencePixelStrategy GREYSCALE_STRATEGY = ((first, second, x, y) -> {
    final int rgb1 = first.getRGB(x, y);
    final int rgb2 = second.getRGB(x, y);
    final int r1 = (rgb1 >> 16) & 0xff;
    final int g1 = (rgb1 >> 8) & 0xff;
    final int b1 = (rgb1) & 0xff;
    final int r2 = (rgb2 >> 16) & 0xff;
    final int g2 = (rgb2 >> 8) & 0xff;
    final int b2 = (rgb2) & 0xff;
    int diff = Math.abs(r1 - r2);
    diff += Math.abs(g1 - g2);
    diff += Math.abs(b1 - b2);
    diff /= 3;
    diff = 255 - diff;
    return (diff << 16) | (diff << 8) | diff;
  });

  /**
   * Builds a difference image using only red, green blue.
   * <ul>
   * <li>both pixels are white, result is white</li>
   * <li>first pixel is white, second pixel not, result is red</li>
   * <li>second pixel is white, first pixel not, result is green</li>
   * <li>both pixels aren't white, result is blue</li>
   * </ul>
   */
  private static final DifferencePixelStrategy RGB_STRATEGY = ((first, second, x, y) -> {
    final int rgb1 = first.getRGB(x, y);
    final int rgb2 = second.getRGB(x, y);

    if (rgb1 == -1 && rgb2 == -1) {
      // both pixels are white -> result is white
      return -1;
    }
    if (rgb1 == -1 && rgb2 != -1) {
      // first is white, second not -> red
      return Color.RED.getRGB();
    }
    if (rgb1 != -1 && rgb2 == -1) {
      // second is white, first not -> green
      return Color.GREEN.getRGB();
    }

    // both aren't white
    return Color.BLUE.getRGB();
  });
  private static final double THRESHOLD = 0.01;

  private final DifferencePixelStrategy differencePixelStrategy = GREYSCALE_STRATEGY;
  private final Consumer<BufferedImage> differenceConsumer;

  ScreenshotComparator() {
    this(null);
  }

  ScreenshotComparator(final Consumer<BufferedImage> differenceConsumer) {
    this.differenceConsumer = differenceConsumer;
  }

  @Override
  public int compare(final BufferedImage first, final BufferedImage second) {
    if (first.getWidth() != second.getWidth() || first.getHeight() != second.getHeight()) {
      return -1;
    }
    BufferedImage differenceImage = null;
    if (differenceConsumer != null) {
      differenceImage =
          new BufferedImage(second.getWidth(), second.getHeight(), BufferedImage.TYPE_INT_RGB);
    }
    int differentPixels = 0;
    for (int x = 1; x < second.getWidth(); x++) {
      for (int y = 1; y < second.getHeight(); y++) {
        if (differenceImage != null) {
          final int pixel = differencePixelStrategy.calculate(first, second, x, y);
          differenceImage.setRGB(x, y, pixel);
        }
        if (first.getRGB(x, y) != second.getRGB(x, y)) {
          differentPixels++;
        }
      }
    }
    final int pixelCount = second.getWidth() * second.getHeight();
    final float differentPixelRatio = ((float) differentPixels) / pixelCount;
    if (differentPixelRatio > THRESHOLD) {
      if (differenceImage != null) {
        differenceConsumer.accept(differenceImage);
      }
      return -differentPixels;
    }
    return 0;
  }

  @FunctionalInterface
  interface DifferencePixelStrategy {

    int calculate(final BufferedImage first, final BufferedImage second, final int x, final int y);

  }

}

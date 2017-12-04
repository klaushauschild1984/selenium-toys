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
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

public class DownloadWebDriverExecutable {

  private final File targetDirectory;
  private final Function<File, Optional<WebDriverExecutable>> getWebDriverExecutableFromWorkDirectory;
  private final Supplier<String> getLatestVersion;
  private final BiFunction<String, File, DownloadWebDriverExecutable.WebDriverExecutable> downloadExpectedVersion;

  public DownloadWebDriverExecutable(final File targetDirectory,
      final Function<File, Optional<WebDriverExecutable>> getWebDriverExecutableFromWorkDirectory,
      final Supplier<String> getLatestVersion,
      final BiFunction<String, File, WebDriverExecutable> downloadExpectedVersion) {
    this.targetDirectory = targetDirectory;
    this.targetDirectory.mkdirs();
    this.getWebDriverExecutableFromWorkDirectory = getWebDriverExecutableFromWorkDirectory;
    this.getLatestVersion = getLatestVersion;
    this.downloadExpectedVersion = downloadExpectedVersion;
  }

  public File get(@Nullable final String expectedVersion, final boolean forceUpdate) {
    final Optional<WebDriverExecutable> webDriverExecutable =
        getWebDriverExecutableFromWorkDirectory.apply(targetDirectory);

    if (expectedVersion == null && !webDriverExecutable.isPresent()) {
      // no expected version, no local copy -> download latest
      final String latestVersion = getLatestVersion.get();
      return downloadExpectedVersion.apply(latestVersion, targetDirectory).get();
    }
    if (expectedVersion == null) {
      // no expected version, local copy present
      if (forceUpdate) {
        // update forced
        final String latestVersion = getLatestVersion.get();
        if (Objects.equals(latestVersion, webDriverExecutable.get().getVersion())) {
          // local copy's version matches latest version -> use local copy
          return webDriverExecutable.get().get();
        } else {
          // local copy's version differs from latest version -> delete old copy and download latest
          delete(webDriverExecutable.get());
          return downloadExpectedVersion.apply(latestVersion, targetDirectory).get();
        }
      } else {
        // no update forced -> use local copy
        return webDriverExecutable.get().get();
      }
    }
    if (!webDriverExecutable.isPresent()) {
      // version expected but no local copy -> download expected version
      return downloadExpectedVersion.apply(expectedVersion, targetDirectory).get();
    }
    if (Objects.equals(expectedVersion, webDriverExecutable.get().getVersion())) {
      // local copy's version matches expected version -> use local copy
      return webDriverExecutable.get().get();
    } else {
      // local copy's version differs from expected version -> delete old copy and download expected
      delete(webDriverExecutable.get());
      return downloadExpectedVersion.apply(expectedVersion, targetDirectory).get();
    }
  }

  private void delete(final WebDriverExecutable webDriverExecutable) {
    if (!webDriverExecutable.get().delete()) {
      throw new IllegalStateException(String
          .format("Unable to delete outdated executable version %s", webDriverExecutable.get()));
    }
  }

  static class WebDriverExecutable {

    private final File file;
    private final String version;

    WebDriverExecutable(final File file, final String version) {
      this.file = file;
      this.version = version;
    }

    File get() {
      return file;
    }

    public String getVersion() {
      return version;
    }

  }

}

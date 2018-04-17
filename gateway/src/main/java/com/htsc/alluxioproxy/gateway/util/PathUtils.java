package com.htsc.alluxioproxy.gateway.util;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Util class for paths.
 */
public final class PathUtils {
  private static final char SEPARATOR = '/';

  /**
   * Joins each element in paths in order, separated by '/'.
   *
   * @param base the base path
   * @param paths paths to concatenate
   * @return the joint path
   */
  public static String concatPath(Object base, Object... paths) {
    Preconditions.checkNotNull(base, "Failed to concatPath: base is null");
    Preconditions.checkNotNull(paths, "Failed to concatPath: a null set of paths");
    List<String> trimmedPathList = new ArrayList<>();
    String trimmedBase =
        CharMatcher.is(SEPARATOR).trimTrailingFrom(base.toString().trim());
    trimmedPathList.add(trimmedBase);
    for (Object path : paths) {
      Preconditions.checkNotNull(path, "The path is null");
      String trimmedPath =
          CharMatcher.is(SEPARATOR).trimFrom(path.toString().trim());
      if (!trimmedPath.isEmpty()) {
        trimmedPathList.add(trimmedPath);
      }
    }
    if (trimmedPathList.size() == 1 && trimmedBase.isEmpty()) {
      // base must be "[/]+"
      return "/";
    }
    return Joiner.on(SEPARATOR).join(trimmedPathList);
  }

  private PathUtils() {} // Prevents instantiation.
}

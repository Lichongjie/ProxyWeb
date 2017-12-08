package web;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Util class for paths.
 */
public final class PathUtils {
  private static final char SEPARATOR = '/';

  /**
   * Creates a directory recursively. If the directory already exists, it's a no-op.
   *
   * @param dir the directory to create
   * @throws IOException if failed to create the directory
   */
  public static void mkdirs(File dir) throws IOException {
    if (!dir.mkdirs() && !dir.isDirectory()) {
      throw new IOException(String.format("Failed to create directory %s", dir));
    }
  }

  /**
   * Gets the filename from a file identifier ID.
   *
   * @param fileID the file ID
   * @return the filename
   */
  public static String getFileName(String fileID) {
    return fileID.substring(fileID.lastIndexOf("/") + 1);
  }

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

package com.htsc.alluxioproxy;

import com.google.gson.Gson;

/**
 * Represents the response of transcode.
 */
public class TranscodeResponse {
  private static final Gson GSON = new Gson();

  private final int errorCode;
  private final String description;

  /**
   * Constructor for {@link TranscodeResponse}.
   *
   * @param errorCode the error code
   * @param description the error description
   */
  public TranscodeResponse(int errorCode, String description) {
    this.errorCode = errorCode;
    this.description = description;
  }

  /**
   * @return {@link #errorCode}
   */
  public int getErrorCode() {
    return errorCode;
  }

  /**
   * @return {@link #description}
   */
  public String getDescription() {
    return description;
  }

  /**
   * Parses an instance of {@link TranscodeResponse} from json.
   *
   * @param str the json string
   * @return an instance of {@link TranscodeResponse}
   */
  public static TranscodeResponse fromString(String str) {
    return GSON.fromJson(str, TranscodeResponse.class);
  }
}

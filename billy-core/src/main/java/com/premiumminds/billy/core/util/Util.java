package com.premiumminds.billy.core.util;

public class Util {
  public static <T extends Object> T orNull(T arg) {
    return arg == null ? null : arg;
  }
}
package com.premiumminds.billy.core.util;
import java.math.MathContext;
import java.math.RoundingMode;

public class BillyMathContext {
  private static MathContext instance;

  public static final int SCALE = 2;

  public static MathContext get() {
    if (BillyMathContext.instance == null) {
      BillyMathContext.instance = new MathContext(34, RoundingMode.HALF_EVEN);
    }
    return BillyMathContext.instance;
  }
}
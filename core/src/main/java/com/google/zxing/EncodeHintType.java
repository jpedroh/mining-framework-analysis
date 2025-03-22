package com.google.zxing;

/**
 * These are a set of hints that you may pass to Writers to specify their behavior.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */public enum EncodeHintType {
  ERROR_CORRECTION,
  CHARACTER_SET,
  DATA_MATRIX_SHAPE,
  @Deprecated MIN_SIZE,
  @Deprecated MAX_SIZE,
  MARGIN,
  PDF417_COMPACT,
  PDF417_COMPACTION,
  PDF417_DIMENSIONS,
  AZTEC_LAYERS,
  QR_VERSION,
  QR_MASK_PATTERN,
  GS1_FORMAT,
  FORCE_CODE_SET
}
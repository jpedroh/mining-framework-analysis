package org.movsim.simulator;

/**
 * The Interface Constants.
 */
public interface Constants {
  final String RELEASE_VERSION = "1.0";

  /** The COMMEN t_ char. */
  final String COMMENT_CHAR = "#";

  /** The SMAL l_ value. */
  final double SMALL_VALUE = 1e-7;

  /** The MA x_ vehicl e_ speed. */
  final double MAX_VEHICLE_SPEED = 200 / 3.6;

  /** The most right lane (related to list index) */
  final int MOST_RIGHT_LANE = 0;

  final int TO_LEFT = 1;

  final int TO_RIGHT = -1;

  final int NO_CHANGE = 0;

  /** The MODE l_ nam e_ idm. */
  final String MODEL_NAME_IDM = "IDM";

  /** The MODE l_ nam e_ acc. */
  final String MODEL_NAME_ACC = "ACC";

  /** The MODE l_ nam e_ ov m_ vdiff. */
  final String MODEL_NAME_OVM_VDIFF = "OVM_VDIFF";

  /** The MODE l_ nam e_ gipps. */
  final String MODEL_NAME_GIPPS = "GIPPS";

  /** The MODE l_ nam e_ newell. */
  final String MODEL_NAME_NEWELL = "NEWELL";

  /** The MODE l_ nam e_ nsm. */
  final String MODEL_NAME_NSM = "NSM";

  final String MODEL_NAME_BARL = "BARL";

  /** The MODE l_ nam e_ kca. */
  final String MODEL_NAME_KKW = "KKW";

  final String MODEL_NAME_KRAUSS = "KRAUSS";

  final String OBSTACLE_KEY_NAME = "Obstacle";

  /** The gap infinity. */
  final double GAP_INFINITY = 10000;

  /** The invalid gap */
  final double INVALID_GAP = -1;

  final double VEHICLE_WIDTH = 4.4;

  final double CRITICAL_GAP = 2;
}
package org.movsim.simulator.roadnetwork;

/**
 * <p>
 * Lanes value constants.
 * </p>
 * <p>
 * Lanes are numbered from the inside laneIndex to the outside laneIndex. So, for example, on a three laneIndex road LANE1 is the inside
 * laneIndex, LANE2 is the middle laneIndex and LANE3 is the outside laneIndex.
 * </p>
 * Lanes numbering is independent of whether traffic drives on the right or the left, indeed references to "right lanes"
 * and "left lanes" is conscientiously eschewed.
 * <p>
 * </p>
 */
public final class Lanes {
  private Lanes() {
  }

  public static final int LANE1 = 1;

  public static final int LANE2 = 2;

  public static final int LANE3 = 3;

  public static final int LANE4 = 4;

  public static final int LANE5 = 5;

  public static final int HARD_SHOULDER = -1;

  public static final int NONE = -2;

  public final static int TO_LEFT = -1;

  public final static int TO_RIGHT = 1;

  public final static int NO_CHANGE = 0;

  public static final int MOST_INNER_LANE = LANE1;

  /** laneIndex=0 is the internal overtaking lane (xodr reserves this for middlelane) */
  public static final int OVERTAKING = 0;

  public enum Type {
    TRAFFIC("driving"),
    ENTRANCE("mwyEntry"),
    EXIT("mwyExit"),
    SHOULDER("shoulder"),
    RESTRICTED("restricted"),
    BICYCLE("biking")
    ;

    private final String openDriveIdentifier;

    Type(String keyword) {
      this.openDriveIdentifier = keyword;
    }

    public String getOpenDriveIdentifier() {
      return openDriveIdentifier;
    }
  }

  public enum LaneSectionType {
    LEFT("-", true),
    RIGHT("+", false)
    ;

    private final String idAppender;

    private final boolean reverseDirection;

    private LaneSectionType(String idAppender, boolean reverseDirection) {
      this.idAppender = idAppender;
      this.reverseDirection = reverseDirection;
    }

    public String idAppender() {
      return idAppender;
    }

    public boolean isReverseDirection() {
      return reverseDirection;
    }
  }

  public enum RoadLinkElementType {
    ROAD("road"),
    JUNCTION("junction")
    ;

    private final String xodrIdentifier;

    RoadLinkElementType(String keyword) {
      this.xodrIdentifier = keyword;
    }

    public String xodrIdentifier() {
      return xodrIdentifier;
    }
  }
}
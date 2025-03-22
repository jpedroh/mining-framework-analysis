package org.movsim.input.model.output.impl;
import org.jdom.Element;
import org.movsim.input.model.output.TrajectoriesInput;

/**
 * The Class TrajectoriesInputImpl.
 */
public class TrajectoriesInputImpl implements TrajectoriesInput {
  /** The dt. */
  private double dt;

  /** The start time. */
  private double startTime;

  /** The end time. */
  private double endTime;

  /** The start position. */
  private double startPosition;

  /** The end position. */
  private double endPosition;

  /** The is initialized. */
  private boolean isInitialized;

  /**
 	 * Instantiates a new trajectories input impl.
 	 *
 	 * @param elem the elem
 	 */
  public TrajectoriesInputImpl(Element elem) {
    if (elem == null) {
      isInitialized = false;
      return;
    }
    dt = Double.parseDouble(elem.getAttributeValue("dt"));
    startTime = Double.parseDouble(elem.getAttributeValue("start_time"));

<<<<<<< /usr/src/app/output/movsim/movsim/dda7cfc15c5dc1102f21327f720973fa36dbc38b/src/main/java/org/movsim/input/model/output/impl/TrajectoriesInputImpl.java/left.java
    endTime = Double.parseDouble(elem.getAttributeValue("end_time"))
=======
    endTime = startTime + Double.parseDouble(elem.getAttributeValue("duration"))
>>>>>>> /usr/src/app/output/movsim/movsim/dda7cfc15c5dc1102f21327f720973fa36dbc38b/src/main/java/org/movsim/input/model/output/impl/TrajectoriesInputImpl.java/right.java
    ;
    startPosition = Double.parseDouble(elem.getAttributeValue(
<<<<<<< /usr/src/app/output/movsim/movsim/dda7cfc15c5dc1102f21327f720973fa36dbc38b/src/main/java/org/movsim/input/model/output/impl/TrajectoriesInputImpl.java/left.java
    "start_x"
=======
    "x"
>>>>>>> /usr/src/app/output/movsim/movsim/dda7cfc15c5dc1102f21327f720973fa36dbc38b/src/main/java/org/movsim/input/model/output/impl/TrajectoriesInputImpl.java/right.java
    ));

<<<<<<< /usr/src/app/output/movsim/movsim/dda7cfc15c5dc1102f21327f720973fa36dbc38b/src/main/java/org/movsim/input/model/output/impl/TrajectoriesInputImpl.java/left.java
    endPosition = Double.parseDouble(elem.getAttributeValue("end_x"))
=======
    endPosition = startPosition + Double.parseDouble(elem.getAttributeValue("length"))
>>>>>>> /usr/src/app/output/movsim/movsim/dda7cfc15c5dc1102f21327f720973fa36dbc38b/src/main/java/org/movsim/input/model/output/impl/TrajectoriesInputImpl.java/right.java
    ;
    isInitialized = true;
  }

  /**
	 * Gets the dt.
	 *
	 * @return the dt
	 */
  public double getDt() {
    return dt;
  }

  /**
	 * Gets the start time.
	 *
	 * @return the startTime
	 */
  public double getStartTime() {
    return startTime;
  }

  /**
	 * Gets the end time.
	 *
	 * @return the endTime
	 */
  public double getEndTime() {
    return endTime;
  }

  /**
	 * Gets the start position.
	 *
	 * @return the startPosition
	 */
  public double getStartPosition() {
    return startPosition;
  }

  /**
	 * Gets the end position.
	 *
	 * @return the endPosition
	 */
  public double getEndPosition() {
    return endPosition;
  }

  /**
	 * Checks if is initialized.
	 *
	 * @return the isInitialized
	 */
  public boolean isInitialized() {
    return isInitialized;
  }
}
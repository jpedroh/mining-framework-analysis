package org.movsim.input.model.simulation.impl;
import org.jdom.Element;
import org.movsim.input.XmlElementNames;
import org.movsim.input.model.simulation.RampData;
import org.movsim.input.model.simulation.UpstreamBoundaryData;

/**
 * The Class RampDataImpl.
 */
public class RampDataImpl implements RampData {
  private final long id;

  /** The center position. */
  private final double rampStartPosition;

  /** The road length. */
  private final double roadLength;

  /** The ramp length. */
  private final double rampMergingLength;

  /** The with logging. */
  private final boolean withLogging;

  private final UpstreamBoundaryData upstreamData;

  /**
     * Instantiates a new ramp data impl.
     * 
     * @param elem
     *            the elem
     */
  @SuppressWarnings(value = { "unchecked" }) public RampDataImpl(Element elem) {
    this.id = Long.parseLong(elem.getAttributeValue("id"));
    this.rampStartPosition = Double.parseDouble(elem.getAttributeValue("x"));
    this.roadLength = Double.parseDouble(elem.getAttributeValue("length"));
    this.rampMergingLength = Double.parseDouble(elem.getAttributeValue("merge_length"));
    this.withLogging = Boolean.parseBoolean(elem.getAttributeValue("logging"));
    final Element upInflowElem = elem.getChild(XmlElementNames.RoadTrafficSource);
    upstreamData = new UpstreamBoundaryDataImpl(upInflowElem);
  }

  @Override public double getRampStartPosition() {
    return rampStartPosition;
  }

  @Override public double getRampMergingLength() {
    return rampMergingLength;
  }

  @Override public double getRoadLength() {
    return roadLength;
  }

  @Override public boolean withLogging() {
    return withLogging;
  }

  @Override public UpstreamBoundaryData getUpstreamBoundaryData() {
    return upstreamData;
  }

  @Override public long getId() {
    return id;
  }
}
package storm.lrb.model;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.hub.cs.dbis.lrb.types.PositionReport;
import de.hub.cs.dbis.lrb.types.SegmentIdentifier;
import de.hub.cs.dbis.lrb.util.Constants;
import de.hub.cs.dbis.lrb.util.Time;

/**
 * The {@code Accident} class serves to record both current and historical data about accidents.
 * 
 * @author richter
 */
public class Accident implements Serializable {
  private static final long serialVersionUID = 1L;

  private static final Logger LOG = LoggerFactory.getLogger(Accident.class);

  private long startMinute;

  private int position;

  private boolean over = false;

  private Set<SegmentIdentifier> involvedSegs = new HashSet<SegmentIdentifier>();

  private Set<Integer> involvedCars = new HashSet<Integer>();

  private int maxPos;

  private int minPos;

  /**
	 * timestamp of last positionreport indicating that the accident is still active
	 */
  private volatile long lastUpdateTime = Integer.MAX_VALUE - 1;

  private PositionReport posReport;

  public static void validatePositionReport(PositionReport pos) {
  }

  protected Accident() {
  }

  public Accident(long startMinute, long lastUpdateTime, int position, int maxPos, int minPos, PositionReport posReport) {
    this.startMinute = startMinute;
    this.lastUpdateTime = lastUpdateTime;
    this.position = position;
    this.maxPos = maxPos;
    this.minPos = minPos;
    this.posReport = posReport;
  }

  public Accident(Accident accident) {
    this.position = accident.getAccidentPosition();
    this.lastUpdateTime = accident.getLastUpdateTime();
    this.involvedSegs = accident.getInvolvedSegs();
    this.involvedCars = accident.getInvolvedCars();
    this.over = accident.isOver();
    this.posReport = accident.getPosReport();
    this.startMinute = Time.getMinute(this.getPosReport().getTime());
  }

  public Accident(PositionReport report) {
    this.position = report.getPosition();
    this.lastUpdateTime = report.getTime();
    this.assignSegments(report.getXWay(), report.getPosition(), report.getDirection());
    this.posReport = report;
  }

  /**
	 * assigns segments to accidents according to LRB req. (within 5 segments upstream)
	 * 
	 * @param xway
	 *            of accident
	 * @param pos
	 *            of accident
	 * @param dir
	 *            of accident
	 */
  private void assignSegments(int xway, int pos, short dir) {
    short segment = (short) (pos / Constants.NUMBER_OF_POSITIONS);
    if (dir == 0) {
      for (short i = segment; 0 < i && i > segment - 5; i--) {
        SegmentIdentifier segmentTriple = new SegmentIdentifier(xway, i, dir);
        this.involvedSegs.add(segmentTriple);
      }
    } else {
      for (short i = segment; i < segment + 5 && i < 100; i++) {
        SegmentIdentifier segmentTriple = new SegmentIdentifier(xway, i, dir);
        this.involvedSegs.add(segmentTriple);
      }
    }
    LOG.debug("ACC:: assigned segments to accident: " + this.involvedSegs.toString());
  }

  public boolean active(long minute) {
    if (this.isOver()) {
      return minute <= Time.getMinute(this.lastUpdateTime);
    } else {
      return minute > this.startMinute;
    }
  }

  /**
	 * get accident position
	 * 
	 * @return position number
	 */
  public int getAccidentPosition() {
    return this.position;
  }

  public PositionReport getPosReport() {
    return this.posReport;
  }

  public void setPosReport(PositionReport posReport) {
    this.posReport = posReport;
  }

  public boolean isOver() {
    return this.over;
  }

  public void setOver(boolean over) {
    this.over = over;
  }

  /**
	 * a shortcut for retrieval of {@code posReport.time}
	 * 
	 * @return
	 */
  public long getStartTime() {
    return this.getPosReport().getTime();
  }

  public long getLastUpdateTime() {
    return this.lastUpdateTime;
  }

  public void setLastUpdateTime(long lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
  }

  /**
	 * get all vids of vehicles involved in that accident
	 * 
	 * @return vehicles hashset wtih vids
	 */
  public Set<Integer> getInvolvedCars() {
    return this.involvedCars;
  }

  public Set<SegmentIdentifier> getInvolvedSegs() {
    return this.involvedSegs;
  }

  protected void recordUpdateTime(int curTime) {
    this.lastUpdateTime = curTime;
  }

  public boolean active(int minute) {
    return (minute > this.getStartTime() / 60 && minute <= (Time.getMinute(this.lastUpdateTime)));
  }

  /**
	 * Checks if accident
	 * 
	 * @param minute
	 * @return
	 */
  public boolean over(int minute) {
    return (minute > (this.lastUpdateTime + 1));
  }

  public void setOver(long timeinseconds) {
    this.over = true;
  }

  /**
	 * update accident information (includes updating time and adding vid of current position report if not already
	 * present
	 * 
	 * @param report
	 *            positionreport of accident car
	 */
  public void updateAccident(PositionReport report) {
    this.getInvolvedCars().add(report.getVid());
    this.lastUpdateTime = report.getTime();
  }

  /**
	 * add car ids of involved cars to accident info
	 * 
	 * @param vehicles
	 *            hashset wtih vids
	 */
  public void addAccVehicles(HashSet<Integer> vehicles) {
    this.getInvolvedCars().addAll(vehicles);
  }
}